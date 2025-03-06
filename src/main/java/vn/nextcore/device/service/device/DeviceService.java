package vn.nextcore.device.service.device;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.nextcore.device.dto.req.DeviceRequest;
import vn.nextcore.device.dto.req.FilterRequest;
import vn.nextcore.device.dto.req.SpecificationRequest;
import vn.nextcore.device.dto.resp.DeviceResponse;
import vn.nextcore.device.dto.resp.ListDeviceResponse;
import vn.nextcore.device.entity.*;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.*;
import vn.nextcore.device.security.jwt.JwtUtil;
import vn.nextcore.device.repository.criteria.IDeviceCriteriaRepository;
import vn.nextcore.device.service.storageFiles.IStorageService;
import vn.nextcore.device.util.JsonUtils;
import vn.nextcore.device.util.ParseUtils;
import vn.nextcore.device.validation.HandlerValidateParams;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DeviceService implements IDeviceService {
    private final String FILTERS = "filters";
    private final String GROUP_ID = "groupId";
    private final String PROVIDER_ID = "providerId";
    private final String DATE_BUY = "dateBuy";
    private final String DATE_MAINTENANCE = "dateMaintenance";
    private final String ORD_DATE_BUY = "ordDateBuy";
    private final String ORD_DATE_MAINTENANCE = "ordDateMaintenance";
    private final String OFFSET = "offset";
    private final String LIMIT = "limit";
    private final String STATUS = "status";

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private SpecificationRepository specificationRepository;

    @Autowired
    private IStorageService storageService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IDeviceCriteriaRepository deviceCriteriaRepository;

    @Value("${file.upload-dir}")
    private String uploadImgDeviceDir;

    @Value("${file.types}")
    private String[] VALID_IMAGE_TYPES;

    private List<String> allowedFields = Arrays.asList("name", "groupId", "providerId", "dateBuy", "dateMaintenance");

    private final String STATUS_STOCK = "stock";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private int MAX_SIZE_IMAGES_UPLOAD = 5;

    @Override
    public DeviceResponse getInfoDevice(String id) {
        try {
            // validate id
            HandlerValidateParams.validatePositiveInt(id, ErrorCodeEnum.ER058);

            Device deviceExists = deviceRepository.findDeviceByIdAndDeletedAtIsNull(Long.valueOf(id));
            if (deviceExists == null)
                throw new HandlerException(ErrorCodeEnum.ER057.getCode(), ErrorCodeEnum.ER057.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.NOT_FOUND);

            DeviceResponse result = ParseUtils.convertDeviceToDeviceRes(deviceExists, "infoUpdate");
            return result;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEVICE_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DeviceResponse deleteDevice(String id) {
        DeviceResponse result = new DeviceResponse();
        try {
            // validate id
            HandlerValidateParams.validatePositiveInt(id, ErrorCodeEnum.ER058);
            Device deviceExists = deviceRepository.findDeviceByIdAndDeletedAtIsNull(Long.valueOf(id));
            if (deviceExists == null) {
                throw new HandlerException(ErrorCodeEnum.ER057.getCode(), ErrorCodeEnum.ER057.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.NOT_FOUND);
            }
            deviceExists.setDeletedAt(new Date());
            deviceRepository.save(deviceExists);
            result.setDeviceId(deviceExists.getId());
            return result;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEVICE_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ListDeviceResponse getAllDevices(Map<String, String> allParams) {
        try {
            String ordDateBuy = validateSortParam(allParams, ORD_DATE_BUY, ErrorCodeEnum.ER037);
            String ordDateMaintenance = validateSortParam(allParams, ORD_DATE_MAINTENANCE, ErrorCodeEnum.ER038);
            String offset = validateIntParam(allParams, OFFSET, "0", ErrorCodeEnum.ER041);
            String limit = validatePositiveIntParam(allParams, LIMIT, "10", ErrorCodeEnum.ER042);
            String status = validateOptionalParam(allParams, STATUS, ErrorCodeEnum.ER054);

            // Decode and validate filters
            List<FilterRequest> filters = validateFilters(allParams);

            // Query devices
            ListDeviceResponse result = deviceCriteriaRepository.listDeviceCriteria(
                    status, ordDateBuy, ordDateMaintenance, Integer.valueOf(offset), Integer.valueOf(limit), filters
            );

            // Check for empty results
            if (result.getTotalRecords() == 0) {
                throw new HandlerException(ErrorCodeEnum.ER043.getCode(), ErrorCodeEnum.ER043.getMessage(), HttpStatus.NOT_FOUND);
            }

            return result;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEVICE_PATH.getPath(), handlerException.getStatus());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = {JsonProcessingException.class, IOException.class, HandlerException.class, Exception.class})
    public DeviceResponse createDevice(DeviceRequest deviceRequest, String specificationJson, MultipartFile[] images, HttpServletRequest request) {
        Device newDevice = new Device();
        DeviceResponse deviceResponse = new DeviceResponse();
        try {
            // set info normal of device
            setInfoDevice(deviceRequest, newDevice);
            // add group info
            Group group = groupRepository.findGroupById(Long.valueOf(deviceRequest.getGroupId()));
            group.setQuantity(group.getQuantity() + 1);
            groupRepository.save(group);
            newDevice.setGroup(group);

            // add createBy
            User user = jwtUtil.extraUserFromRequest(request);
            newDevice.setCreatedBy(user);

            // add provider
            Provider provider = providerRepository.findProviderById(Long.valueOf(deviceRequest.getProviderId()));
            newDevice.setProvider(provider);

            // add set specification
            if (specificationJson != null && !specificationJson.isEmpty()) {
                Set<SpecificationRequest> specificationRequestSet = JsonUtils.parseJsonToSet(specificationJson,
                        new TypeReference<Set<SpecificationRequest>>() {
                        }, ErrorCodeEnum.ER062);

                Set<Specification> specifications = addInfoSpecification(specificationRequestSet);
                newDevice.setSpecifications(specifications);
            }

            // upload images
            if (images != null) {
                // check files image
                CheckFilesValid(images, images.length);

                Set<Image> imageSet = processImages(images, newDevice);
                newDevice.setImages(imageSet);
            }

            newDevice.setStatus(STATUS_STOCK);
            newDevice.setCreatedAt(new Date());
            deviceRepository.save(newDevice);

            deviceResponse.setDeviceId(newDevice.getId());
            return deviceResponse;
        } catch (JsonProcessingException jex) {
            jex.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER107.getCode(), ErrorCodeEnum.ER107.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.BAD_REQUEST);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER032.getCode(), ErrorCodeEnum.ER032.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = {JsonProcessingException.class, IOException.class, Exception.class, HandlerException.class})
    public DeviceResponse updateDevice(String id, DeviceRequest deviceRequest, String specificationJson, MultipartFile[] images, String imagesDelete, String specificationDelete) {
        DeviceResponse deviceResponse = new DeviceResponse();
        Set<Long> imagesRequestDelete = new HashSet<>();
        try {
            // validate deviceId
            HandlerValidateParams.validatePositiveInt(id, ErrorCodeEnum.ER058);

            Device deviceExists = deviceRepository.findDeviceByIdAndDeletedAtIsNull(Long.valueOf(id));
            if (deviceExists == null) {
                throw new HandlerException(ErrorCodeEnum.ER057.getCode(), ErrorCodeEnum.ER057.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.NOT_FOUND);
            }

            // set info normal of device
            setInfoDevice(deviceRequest, deviceExists);
            updateGroupInfo(deviceRequest.getGroupId(), deviceExists);

            Provider newProvider = providerRepository.findProviderById(Long.valueOf(deviceRequest.getProviderId()));
            deviceExists.setProvider(newProvider);

            // handle specification delete
            if (specificationDelete != null && !specificationDelete.isEmpty()) {
                deleteSpecifications(specificationDelete, deviceExists);
            }

            // handle specification add
            if (specificationJson != null && !specificationJson.isEmpty()) {
                updateSpecifications(specificationJson, deviceExists);
            }

            // handle delete images
            if (imagesDelete != null && !imagesDelete.isEmpty()) {
                deleteImages(imagesDelete, deviceExists);
            }

            // upload images
            if (images != null) {
                // check files image
                int quantityImagesOfDevice = imageRepository.countByDeviceId(Long.valueOf(id));
                CheckFilesValid(images, quantityImagesOfDevice + images.length - imagesRequestDelete.size());

                Set<Image> imageSet = processImages(images, deviceExists);
                deviceExists.setImages(imageSet);
            }

            deviceExists.setUpdatedAt(new Date());
            deviceRepository.save(deviceExists);
            deviceResponse.setDeviceId(deviceExists.getId());
            return deviceResponse;
        } catch (JsonProcessingException jex) {
            jex.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER107.getCode(), ErrorCodeEnum.ER107.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.BAD_REQUEST);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER032.getCode(), ErrorCodeEnum.ER032.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEVICE_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void updateGroupInfo(String groupId, Device device) {
        Group newGroup = groupRepository.findGroupById(Long.valueOf(groupId));
        device.getGroup().setQuantity(device.getGroup().getQuantity() - 1);
        newGroup.setQuantity(newGroup.getQuantity() + 1);
        groupRepository.save(newGroup);
        device.setGroup(newGroup);
    }

    private void updateSpecifications(String specificationJson, Device device) throws JsonProcessingException {
        Set<SpecificationRequest> specificationRequestSet = JsonUtils.parseJsonToSet(specificationJson, new TypeReference<Set<SpecificationRequest>>() {
        }, ErrorCodeEnum.ER062);
        Set<Specification> specificationsAdd = addInfoSpecification(specificationRequestSet);
        specificationsAdd.forEach(device::addSpecification);
    }

    private void deleteSpecifications(String specificationDelete, Device device) throws JsonProcessingException {
        Set<Long> specificationRequestDelete = JsonUtils.parseJsonToSet(specificationDelete, new TypeReference<Set<Long>>() {
        }, ErrorCodeEnum.ER061);
        checkSpecificationOfDevice(specificationRequestDelete, device.getId());
        Set<Specification> specificationsDelete = specificationRepository.findAllByIdIn(specificationRequestDelete);
        specificationsDelete.forEach(device::removeSpecification);
    }

    private void deleteImages(String imagesDelete, Device device) throws JsonProcessingException {
        Set<Long> imagesRequestDelete = JsonUtils.parseJsonToSet(imagesDelete, new TypeReference<Set<Long>>() {
        }, ErrorCodeEnum.ER060);
        checkImagesExistsOfDevice(imagesRequestDelete, device.getId());
        imagesRequestDelete.forEach(imageId -> deleteSingleImage(imageId, device.getId()));
    }

    private void deleteSingleImage(Long imageId, Long deviceId) {
        Image image = imageRepository.findById(imageId).orElseThrow(() ->
                new HandlerException(ErrorCodeEnum.ER059.getCode(), ErrorCodeEnum.ER059.getMessage(), HttpStatus.BAD_REQUEST));
        imageRepository.deleteById(image.getId());
        boolean isDeleteImageSuccess = storageService.delete(image.getName());
    }

    private void checkSpecificationOfDevice(Set<Long> specificationsDelete, Long deviceId) {
        boolean allExists = specificationsDelete.stream()
                .allMatch(id -> deviceRepository.existsByDeviceIdAndSpecificationId(deviceId, id));
        if (!allExists) {
            throw new HandlerException(ErrorCodeEnum.ER063.getCode(), ErrorCodeEnum.ER063.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private void checkImagesExistsOfDevice(Set<Long> imagesRequestDelete, Long deviceId) {
        for (Long id : imagesRequestDelete) {
            if (!imageRepository.existsByIdAndDeviceId(id, Long.valueOf(deviceId))) {
                throw new HandlerException(ErrorCodeEnum.ER059.getCode(), ErrorCodeEnum.ER059.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void setInfoDevice(DeviceRequest request, Device device) throws ParseException {
        device.setName(request.getName());
        device.setPriceBuy(Double.valueOf(request.getPriceBuy()));
        device.setDateBuy(dateFormat.parse(request.getDateBuy()));
        device.setDateMaintenance(dateFormat.parse(request.getDateMaintenance()));

        if (request.getPriceSell() != null)
            device.setPriceSell(Double.valueOf(request.getPriceSell()));
        if (request.getDescription() != null)
            device.setDescription(request.getDescription());
        if (request.getDateSell() != null)
            device.setDateSell(dateFormat.parse(request.getDateSell()));
    }

    // validate field ord
    private String validateSortParam(Map<String, String> allParams, String paramKey, ErrorCodeEnum errorCode) {
        String paramValue = JsonUtils.getValueByKey(allParams, paramKey);
        if (paramValue != null) {
            HandlerValidateParams.validateSortField(paramValue, errorCode);
        }
        return paramValue;
    }

    // validate field type Integer
    private String validateIntParam(Map<String, String> allParams, String paramKey, String defaultValue, ErrorCodeEnum errorCode) {
        String paramValue = JsonUtils.getValueByKey(allParams, paramKey);
        if (paramValue == null) {
            paramValue = defaultValue;
        }
        HandlerValidateParams.validateInt(paramValue, errorCode);
        return paramValue;
    }

    // validate is positive integer
    private String validatePositiveIntParam(Map<String, String> allParams, String paramKey, String defaultValue, ErrorCodeEnum errorCode) {
        String paramValue = validateIntParam(allParams, paramKey, defaultValue, errorCode);
        HandlerValidateParams.validatePositiveInt(paramValue, errorCode);
        return paramValue;
    }

    // validate option
    private String validateOptionalParam(Map<String, String> allParams, String paramKey, ErrorCodeEnum errorCode) {
        String paramValue = JsonUtils.getValueByKey(allParams, paramKey);
        if (paramValue != null) {
            HandlerValidateParams.validateStatusField(paramValue, errorCode);
        }
        return paramValue;
    }

    private List<FilterRequest> validateFilters(Map<String, String> allParams) {
        String filterEncode = JsonUtils.getValueByKey(allParams, FILTERS);
        if (filterEncode == null || filterEncode.isEmpty()) {
            return new ArrayList<>();
        }

        // decode filters json
        List<FilterRequest> filters = JsonUtils.decodeAndList(filterEncode);

        for (FilterRequest filter : filters) {
            validateFilterField(filter);
            validateFilterValues(filter);
        }

        return filters;
    }

    // validate field in filters allowed
    private void validateFilterField(FilterRequest filter) {
        if (!allowedFields.contains(filter.getField())) {
            throw new HandlerException(ErrorCodeEnum.ER051.getCode(), ErrorCodeEnum.ER051.getMessage(filter.getField()), HttpStatus.BAD_REQUEST);
        }
    }

    // validate value in filters
    private void validateFilterValues(FilterRequest filter) {
        if (filter.getValues().size() > 2 || filter.getValues().isEmpty()) {
            throw new HandlerException(ErrorCodeEnum.ER053.getCode(), ErrorCodeEnum.ER053.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if (GROUP_ID.equals(filter.getField()) || PROVIDER_ID.equals(filter.getField())) {
            HandlerValidateParams.validatePositiveInt(filter.getValues().get(0),
                    GROUP_ID.equals(filter.getField()) ? ErrorCodeEnum.ER039 : ErrorCodeEnum.ER040);
        }

        if (DATE_BUY.equals(filter.getField()) || DATE_MAINTENANCE.equals(filter.getField())) {
            validateDateFilter(filter);
        }
    }

    // validate field date in filters
    private void validateDateFilter(FilterRequest filter) {
        for (String value : filter.getValues()) {
            HandlerValidateParams.validateFormatDateField(value,
                    DATE_BUY.equals(filter.getField()) ? ErrorCodeEnum.ER046 : ErrorCodeEnum.ER047);
            HandlerValidateParams.checkInvalidDateField(value,
                    DATE_BUY.equals(filter.getField()) ? ErrorCodeEnum.ER048 : ErrorCodeEnum.ER049);
        }

        if (filter.getValues().size() == 2) {
            HandlerValidateParams.checkCompareDates(filter.getValues().get(0), filter.getValues().get(1),
                    ErrorCodeEnum.ER050);
        }
    }

    // METHOD ADD info specification
    private Set<Specification> addInfoSpecification(Set<SpecificationRequest> specificationsReq) {
        Set<Specification> specifications = new HashSet<>();
        if (specificationsReq != null) {
            for (SpecificationRequest specificationReq : specificationsReq) {
                // check name and value not blank
                validateSpecificationRequest(specificationReq.getName(), specificationReq.getValue());

                Specification specificationExists = specificationRepository.findSpecificationByNameAndValue(specificationReq.getName(), specificationReq.getValue());
                if (specificationExists != null) {
                    specifications.add(specificationExists);
                } else {
                    Specification newSpecification = new Specification();
                    newSpecification.setName(specificationReq.getName());
                    newSpecification.setValue(specificationReq.getValue());
                    specificationRepository.save(newSpecification);
                    specifications.add(newSpecification);
                }
            }
        }
        return specifications;
    }

    private void validateSpecificationRequest(String name, String value) {
        if (name == null || name.isEmpty()) {
            throw new HandlerException(ErrorCodeEnum.ER033.getCode(), ErrorCodeEnum.ER033.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.BAD_REQUEST);
        }
        if (value == null || value.isEmpty()) {
            throw new HandlerException(ErrorCodeEnum.ER034.getCode(), ErrorCodeEnum.ER034.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.BAD_REQUEST);
        }
    }

    // handle save files image
    private Set<Image> processImages(MultipartFile[] images, Device device) throws IOException {
        Set<Image> imageSet = new HashSet<>();

        for (MultipartFile image : images) {
            // upload image
            String fileName = storageService.saveFile(image);

            Image newImage = new Image();
            newImage.setName(fileName);
            newImage.setPath(uploadImgDeviceDir + fileName);
            newImage.setDevice(device);
            imageSet.add(newImage);
        }
        return imageSet;
    }

    // validate file
    private void CheckFilesValid(MultipartFile[] multipartFiles, int imagesLength) {
        // check files length
        if (imagesLength > MAX_SIZE_IMAGES_UPLOAD) {
            throw new HandlerException(ErrorCodeEnum.ER031.getCode(), ErrorCodeEnum.ER031.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.BAD_REQUEST);
        }

        for (MultipartFile file : multipartFiles) {
            // check file size
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new HandlerException(ErrorCodeEnum.ER029.getCode(), ErrorCodeEnum.ER029.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            // check file type
            String contentType = file.getContentType();
            if (contentType == null || !isValidImageType(contentType)) {
                throw new HandlerException(ErrorCodeEnum.ER030.getCode(), ErrorCodeEnum.ER030.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    // check file type
    private boolean isValidImageType(String contentType) {
        return Arrays.stream(VALID_IMAGE_TYPES)
                .anyMatch(type -> type.equalsIgnoreCase(contentType));
    }
}
