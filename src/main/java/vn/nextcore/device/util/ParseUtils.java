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
                providerResponse.setId(device.getProvider().getId() != null ? device.getProvider().getId() : null);
                providerResponse.setName(device.getProvider().getName());
                deviceResponse.setProvider(providerResponse);

                List<ImageResponse> images = new ArrayList<>();
                if (device.getImages() != null) {
                    images = device.getImages().stream().map(image ->
                            new ImageResponse(String.valueOf(image.getId()), image.getName())
                    ).collect(Collectors.toList());
                }
                deviceResponse.setImages(images);
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

            UserResponse user = new UserResponse();
            user.setUserName(request.getCreatedBy().getUserName());
            user.setEmail(request.getCreatedBy().getEmail());
            user.setGender(request.getCreatedBy().getGender());
            user.setPhoneNumber(request.getCreatedBy().getPhoneNumber());
            user.setId(request.getCreatedBy().getId());
            user.setAvatarUrl(request.getCreatedBy().getAvatarUrl());
            user.setRole(new RoleResponse(request.getCreatedBy().getRole().getId(), request.getCreatedBy().getRole().getName()));
            reqResponse.setCreatedBy(user);
            if (request.getApprover() != null) {
                UserResponse approver = new UserResponse();
                approver.setUserName(request.getCreatedBy().getUserName());
                approver.setEmail(request.getCreatedBy().getEmail());
                approver.setGender(request.getCreatedBy().getGender());
                approver.setId(request.getCreatedBy().getId());
                approver.setAvatarUrl(request.getCreatedBy().getAvatarUrl());
                approver.setRole(new RoleResponse(request.getCreatedBy().getRole().getId(), request.getCreatedBy().getRole().getName()));
                reqResponse.setApproveBy(approver);
            }

            // convert extra details
            if ("details".equals(method)) {
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
                        UserResponse createdBy = new UserResponse();
                        createdBy.setId(deliveryNote.getCreatedBy().getId());
                        createdBy.setUserName(deliveryNote.getCreatedBy().getUserName());
                        createdBy.setEmail(deliveryNote.getCreatedBy().getEmail());
                        createdBy.setAvatarUrl(deliveryNote.getCreatedBy().getAvatarUrl());
                        createdBy.setRole(new RoleResponse(deliveryNote.getCreatedBy().getRole().getId(), deliveryNote.getCreatedBy().getRole().getName()));
                        deliveryNoteResponse.setCreatedBy(createdBy);

                        if (deliveryNote.getProvider() != null) {
                            deliveryNoteResponse.setProvider(new ProviderResponse(deliveryNote.getProvider().getId(),
                                    deliveryNote.getProvider().getName(), deliveryNote.getProvider().getAddress(),
                                    deliveryNote.getProvider().getPhoneNumber()));
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

    // parse string to date if field is date
    public static Object parseValue(String field, String value, SimpleDateFormat dateFormat) throws ParseException {
        if (field.startsWith("date")) {
            return dateFormat.parse(value);
        }
        return value;
    }
}
