package vn.nextcore.device.util;

import org.springframework.http.HttpStatus;
import vn.nextcore.device.dto.resp.*;
import vn.nextcore.device.entity.Device;
import vn.nextcore.device.entity.Image;
import vn.nextcore.device.entity.Request;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.exception.HandlerException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ParseUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public static DeviceResponse convertDeviceToDeviceRes(Device device, String method) {
        try {
            DeviceResponse deviceResponse = new DeviceResponse();
            deviceResponse.setDeviceId(device.getId());
            deviceResponse.setName(device.getName());
            if ("list".equals(method)) {
                deviceResponse.setGroupName(device.getGroup().getName());
                deviceResponse.setProviderName(device.getProvider().getName());
                String fistImg = null;
                Iterator<Image> iterator = device.getImages().iterator();
                if (iterator.hasNext()) {
                    fistImg = iterator.next().getName();
                }
                deviceResponse.setImage(fistImg);
            }

            if ("infoUpdate".equals(method)) {
                deviceResponse.setPriceBuy(device.getPriceBuy() != null ? String.valueOf(device.getPriceBuy()) : "");
                deviceResponse.setPriceSell(device.getPriceSell() != null ? String.valueOf(device.getPriceSell()) : "");
                deviceResponse.setDateSell(device.getDateSell() != null ? dateFormat.format(device.getDateSell()) : "");

                GroupResponse groupResponse = new GroupResponse();
                groupResponse.setId(device.getGroup().getId() != null ? String.valueOf(device.getGroup().getId()) : null);
                groupResponse.setName(device.getGroup().getName());
                deviceResponse.setGroup(groupResponse);

                ProviderResponse providerResponse = new ProviderResponse();
                providerResponse.setId(device.getProvider().getId() != null ? String.valueOf(device.getProvider().getId()) : null);
                providerResponse.setName(device.getProvider().getName());
                deviceResponse.setProvider(providerResponse);

                List<ImageResponse> images = new ArrayList<>();
                if (device.getImages() != null) {
                    images = device.getImages().stream().map(image ->
                            new ImageResponse(String.valueOf(image.getId()), image.getName())
                    ).collect(Collectors.toList());
                }
                deviceResponse.setImages(images);

                List<SpecificationResponse> specifications = new ArrayList<>();
                if (device.getSpecifications() != null) {
                    specifications = device.getSpecifications().stream().map(specification ->
                            new SpecificationResponse(specification.getId(), specification.getName(), specification.getValue())
                    ).collect(Collectors.toList());
                }
                deviceResponse.setSpecifications(specifications);

            }

            deviceResponse.setDateBuy(dateFormat.format(device.getDateBuy()));
            deviceResponse.setDateMaintenance(dateFormat.format(device.getDateMaintenance()));
            deviceResponse.setDescription(device.getDescription());


            return deviceResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public static ReqResponse convertRequestToReqResponse(Request request) {
        try {
            ReqResponse reqResponse = new ReqResponse();
            reqResponse.setRequestId(request.getId());
            reqResponse.setTitle(request.getTitle());
            reqResponse.setDescriptions(request.getDescription());
            reqResponse.setType(request.getRequestType());
            reqResponse.setStatus(request.getStatus());
            if (request.getCreatedDate() != null) reqResponse.setCreatedDate(dateFormat.format(request.getCreatedDate()));
            if (request.getApprovedDate() != null) reqResponse.setApprovedDate(dateFormat.format(request.getApprovedDate()));
            UserResponse user = new UserResponse();
            user.setUserName(request.getCreatedBy().getUserName());
            user.setEmail(request.getCreatedBy().getEmail());
            user.setGender(request.getCreatedBy().getGender());
            user.setId(request.getCreatedBy().getId());
            user.setAvatarUrl(request.getCreatedBy().getAvatarUrl());
            user.setRole(new RoleResponse(request.getCreatedBy().getRole().getId(), request.getCreatedBy().getRole().getName()));
            reqResponse.setCreatedBy(user);
            return reqResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // parse string to date if field is date
    public static Object parseValue(String field, String value, SimpleDateFormat dateFormat) throws ParseException {
        if (field.startsWith("date")) {
            return dateFormat.parse(value);
        }
        return value;
    }
}
