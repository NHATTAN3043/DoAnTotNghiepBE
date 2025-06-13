package vn.nextcore.device.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vn.nextcore.device.dto.resp.*;
import vn.nextcore.device.entity.*;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.DeliveryNoteRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
public class ParseUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy");

    private static String METHOD_LIST = "list";
    private static String METHOD_DETAILS = "details";


    public static DeviceResponse convertDeviceToDeviceRes(Device device, String method) {
        try {
            DeviceResponse deviceResponse = new DeviceResponse();
            deviceResponse.setDeviceId(device.getId());
            deviceResponse.setName(device.getName());
            deviceResponse.setIsBroken(device.getIsBroken());
            if (METHOD_LIST.equals(method)) {
                deviceResponse.setGroupName(device.getGroup().getName());
                deviceResponse.setProviderName(device.getProvider().getName());
                String fistImg = null;
                Iterator<Image> iterator = device.getImages().iterator();
                if (iterator.hasNext()) {
                    fistImg = iterator.next().getName();
                }
                deviceResponse.setImage(fistImg);
            }

            if (METHOD_DETAILS.equals(method)) {
                deviceResponse.setPriceBuy(device.getPriceBuy() != null ? String.valueOf(device.getPriceBuy().longValue()) : "");
                deviceResponse.setPriceSell(device.getPriceSell() != null ? String.valueOf(device.getPriceSell()) : "");
                deviceResponse.setDateSell(device.getDateSell() != null ? dateFormat.format(device.getDateSell()) : "");

                if (device.getUsingBy() != null) {
                    deviceResponse.setUsingBy(convertUserToUserResponse(device.getUsingBy()));
                }

                deviceResponse.setGroup(convertGroupToGroupResponse(device.getGroup()));

                deviceResponse.setProvider(convertProviderToProviderResponse(device.getProvider()));

                List<ImageResponse> images = new ArrayList<>();
                if (device.getImages() != null) {
                    images = device.getImages().stream().map(image ->
                            new ImageResponse(String.valueOf(image.getId()), image.getName())
                    ).collect(Collectors.toList());
                }
                deviceResponse.setImages(images);

                List<NoteDevice> sortedNoteDevices = device.getNoteDevices()
                        .stream()
                        .sorted(Comparator.comparing(NoteDevice::getId))
                        .collect(Collectors.toList());

                List<NoteDeviceResponse> noteDeviceResponses = new ArrayList<>();

                for (NoteDevice noteDevice: sortedNoteDevices) {
                    NoteDeviceResponse noteDeviceResponse = new NoteDeviceResponse();
                    noteDeviceResponse.setNoteDeviceId(noteDevice.getId());
                    noteDeviceResponse.setDescriptionDevice(noteDevice.getDescriptionDevice());
                    noteDeviceResponse.setPriceMaintenance(noteDevice.getPriceDevice());
                    noteDeviceResponse.setDeliveryNoteResponse(convertDeliveryNoteToDeliveryNoteRes(noteDevice.getDeliveryNote()));

                    noteDeviceResponses.add(noteDeviceResponse);
                }
                deviceResponse.setNoteDeviceResponses(noteDeviceResponses);
            }

            List<SpecificationResponse> specifications = new ArrayList<>();
            if (device.getSpecifications() != null) {
                specifications = device.getSpecifications().stream().map(specification ->
                        new SpecificationResponse(specification.getId(), specification.getName(), specification.getValue())
                ).collect(Collectors.toList());
            }
            deviceResponse.setSpecifications(specifications);
            deviceResponse.setDateBuy(dateFormat.format(device.getDateBuy()));
            deviceResponse.setDateMaintenance(dateFormat.format(device.getDateMaintenance()));
            deviceResponse.setDescription(device.getDescription());
            deviceResponse.setStatus(device.getStatus());

            return deviceResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public static ReqResponse convertRequestToReqResponse(Request request, String method) {
        try {
            ReqResponse reqResponse = new ReqResponse();
            reqResponse.setRequestId(request.getId());
            reqResponse.setTitle(request.getTitle());
            reqResponse.setDescriptions(request.getDescription());
            reqResponse.setType(request.getRequestType());
            reqResponse.setStatus(request.getStatus());
            if (request.getCreatedDate() != null)
                reqResponse.setCreatedDate(timeFormat.format(request.getCreatedDate()));
            if (request.getApprovedDate() != null)
                reqResponse.setApprovedDate(timeFormat.format(request.getApprovedDate()));

            reqResponse.setCreatedBy(convertUserToUserResponse(request.getCreatedBy()));

            if (request.getApprover() != null) {
                reqResponse.setApproveBy(convertUserToUserResponse(request.getApprover()));
            }

            // convert extra details
            if (METHOD_DETAILS.equals(method)) {
                if (request.getRequestGroups() != null && !request.getRequestGroups().isEmpty()) {
                    List<ReqGroupResponse> reqGroupResponses = new ArrayList<>();
                    for (RequestGroup requestGroup : request.getRequestGroups()) {
                        ReqGroupResponse reqGroupResponse = new ReqGroupResponse();
                        reqGroupResponse.setReqGroupId(requestGroup.getId());
                        reqGroupResponse.setGroupId(requestGroup.getGroup().getId());
                        reqGroupResponse.setGroupName(requestGroup.getGroup().getName());
                        reqGroupResponse.setReqQuantity(requestGroup.getQuantity());
                        reqGroupResponse.setStatus(requestGroup.getStatus());
                        reqGroupResponses.add(reqGroupResponse);
                    }
                    reqResponse.setGroupRequest(reqGroupResponses);
                }

                if (request.getDeliveryNotes() != null && !request.getDeliveryNotes().isEmpty()) {
                    List<DeliveryNoteResponse> deliveryNotes = new ArrayList<>();
                    // sort by deliveryDate asc
                    List<DeliveryNote> sortedDeliveryNotes = request.getDeliveryNotes()
                            .stream()
                            .sorted(Comparator.comparing(DeliveryNote::getDeliveryDate))
                            .collect(Collectors.toList());

                    for (DeliveryNote deliveryNote : sortedDeliveryNotes) {
                        DeliveryNoteResponse deliveryNoteResponse = new DeliveryNoteResponse();
                        deliveryNoteResponse.setDeliveryNoteId(deliveryNote.getId());
                        deliveryNoteResponse.setTypeAction(deliveryNote.getTypeNote());
                        deliveryNoteResponse.setTimeCreated(timeFormat.format(deliveryNote.getDeliveryDate()));
                        deliveryNoteResponse.setIsConfirm(deliveryNote.getIsConfirm());
                        deliveryNoteResponse.setDescription(deliveryNote.getDescription());
                        deliveryNoteResponse.setCreatedBy(convertUserToUserResponse(deliveryNote.getCreatedBy()));

                        if (deliveryNote.getProvider() != null) {
                            deliveryNoteResponse.setProvider(convertProviderToProviderResponse(deliveryNote.getProvider()));
                        }
                        List<NoteDeviceResponse> noteDeviceResponses = new ArrayList<>();
                        for (NoteDevice noteDevice : deliveryNote.getNoteDevices()) {
                            NoteDeviceResponse noteDeviceResponse = new NoteDeviceResponse();
                            noteDeviceResponse.setNoteDeviceId(noteDevice.getId());
                            noteDeviceResponse.setDescriptionDevice(noteDevice.getDescriptionDevice());
                            noteDeviceResponse.setPriceMaintenance(noteDevice.getPriceDevice());

                            DeviceResponse deviceResponse = new DeviceResponse();
                            deviceResponse.setDeviceId(noteDevice.getDevice().getId());
                            deviceResponse.setName(noteDevice.getDevice().getName());
                            deviceResponse.setGroupName(noteDevice.getDevice().getGroup().getName());
                            Set<Image> images = noteDevice.getDevice().getImages();
                            if (images != null && !images.isEmpty()) {
                                Image firstImage = images.iterator().next();
                                deviceResponse.setImage(firstImage.getName());
                            }
                            deviceResponse.setStatus(noteDevice.getDevice().getStatus());
                            List<SpecificationResponse> specifications = new ArrayList<>();
                            if (noteDevice.getDevice().getSpecifications() != null) {
                                specifications = noteDevice.getDevice().getSpecifications().stream().map(specification ->
                                        new SpecificationResponse(specification.getId(), specification.getName(), specification.getValue())
                                ).collect(Collectors.toList());
                            }
                            deviceResponse.setSpecifications(specifications);
                            noteDeviceResponse.setDevice(deviceResponse);

                            noteDeviceResponses.add(noteDeviceResponse);
                        }
                        deliveryNoteResponse.setNoteDeviceResponses(noteDeviceResponses);
                        deliveryNotes.add(deliveryNoteResponse);
                    }
                    reqResponse.setDeliveryNoteResponses(deliveryNotes);
                }
            }

            return reqResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static DeliveryNoteResponse convertDeliveryNoteToDeliveryNoteRes(DeliveryNote deliveryNote) {
        try {
            DeliveryNoteResponse deliveryNoteResponse = new DeliveryNoteResponse();

            if (deliveryNote != null) {
                deliveryNoteResponse.setDeliveryNoteId(deliveryNote.getId());
                deliveryNoteResponse.setTypeAction(deliveryNote.getTypeNote());
                deliveryNoteResponse.setDescription(deliveryNote.getDescription());
                deliveryNoteResponse.setTimeCreated(timeFormat.format(deliveryNote.getCreatedAt()));
                deliveryNoteResponse.setIsConfirm(deliveryNote.getIsConfirm());
                deliveryNoteResponse.setCreatedBy(convertUserToUserResponse(deliveryNote.getCreatedBy()));
                deliveryNoteResponse.setAssignee(convertUserToUserResponse(deliveryNote.getRequest().getCreatedBy()));
                deliveryNoteResponse.setProvider(convertProviderToProviderResponse(deliveryNote.getProvider()));
            }

            return deliveryNoteResponse;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static UserResponse convertUserToUserResponse(User user) {
        try {
            UserResponse userResponse = new UserResponse();

            if (user != null) {
                userResponse.setId(user.getId());
                userResponse.setUserName(user.getUserName());
                userResponse.setEmail(user.getEmail());
                userResponse.setGender(user.getGender());
                userResponse.setPhoneNumber(user.getPhoneNumber());
                userResponse.setAvatarUrl(user.getAvatarUrl());
                userResponse.setAddress(user.getAddress());
                if (user.getDateOfBirth() != null) {
                    userResponse.setDateOfBirth(dateFormat.format(user.getDateOfBirth()));
                }
                if (user.getRole() != null) {
                    userResponse.setRole(new RoleResponse(user.getRole().getId(), user.getRole().getName()));
                }
                if (user.getDepartment() != null) {
                    userResponse.setDepartment(new DepartmentResponse(user.getDepartment().getId(), user.getDepartment().getName()));
                }
                if (user.getDevicesUsing() != null) {
                    for (Device device : user.getDevicesUsing()) {
                        DeviceResponse deviceResponse = convertDeviceToDeviceRes(device, METHOD_LIST);
                        userResponse.getDeviceResponseList().add(deviceResponse);
                    }
                }
            }

            return userResponse;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static GroupResponse convertGroupToGroupResponse (Group group) {
        try {
            GroupResponse groupResponse = new GroupResponse();

            if (groupResponse != null) {
                groupResponse.setId(group.getId());
                groupResponse.setName(group.getName());
                groupResponse.setQuantity(group.getQuantity());
            }

            return groupResponse;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static GroupResponse convertGroupToGroupResponse (Group group, Integer activeQuantity, Integer stockQuantity, Integer maintenanceQuantity) {
        try {
            GroupResponse groupResponse = new GroupResponse();

            if (groupResponse != null) {
                groupResponse.setId(group.getId());
                groupResponse.setName(group.getName());
                groupResponse.setQuantity(group.getQuantity());
                groupResponse.setUsedQuantity(activeQuantity);
                groupResponse.setStockQuantity(stockQuantity);
                groupResponse.setMaintenanceQuantity(maintenanceQuantity);
            }

            return groupResponse;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static ProviderResponse convertProviderToProviderResponse (Provider provider) {
        try {
            ProviderResponse providerResponse = new ProviderResponse();
            if (provider != null) {
                providerResponse.setId(provider.getId());
                providerResponse.setName(provider.getName());
                providerResponse.setAddress(provider.getAddress());
                providerResponse.setPhoneNumber(provider.getPhoneNumber());
            }

            return providerResponse;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // parse string to date if field is date
    public static Object parseValue(String field, String value, SimpleDateFormat dateFormat) throws ParseException {
        if (field.toLowerCase().contains("date")) {
            return dateFormat.parse(value);
        }
        return value;
    }
}
