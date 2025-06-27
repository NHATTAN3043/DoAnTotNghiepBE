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
                if (!device.getImages().isEmpty()) {
                    deviceResponse.setImage(device.getImages().get(0).getName());
                }
            }

            if (METHOD_DETAILS.equals(method)) {
                deviceResponse.setPriceSell(device.getPriceSell() != null ? String.valueOf(device.getPriceSell()) : "");
                deviceResponse.setDateSell(device.getDateSell() != null ? dateFormat.format(device.getDateSell()) : "");

                if (device.getUsingBy() != null) {
                    deviceResponse.setUsingBy(convertUserToUserResponse(device.getUsingBy()));
                }

                deviceResponse.setGroup(convertGroupToGroupResponse(device.getGroup()));

                deviceResponse.setProvider(convertProviderToProviderResponse(device.getProvider(), true));

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

                for (NoteDevice noteDevice : sortedNoteDevices) {
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
            deviceResponse.setPriceBuy(device.getPriceBuy() != null ? String.valueOf(device.getPriceBuy().longValue()) : "");
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
                            deliveryNoteResponse.setProvider(convertProviderToProviderResponse(deliveryNote.getProvider(), false));
                        }
                        List<NoteDeviceResponse> noteDeviceResponses = new ArrayList<>();
                        for (NoteDevice noteDevice : deliveryNote.getNoteDevices()) {
                            NoteDeviceResponse noteDeviceResponse = new NoteDeviceResponse();
                            noteDeviceResponse.setNoteDeviceId(noteDevice.getId());
                            noteDeviceResponse.setDescriptionDevice(noteDevice.getDescriptionDevice());
                            noteDeviceResponse.setPriceMaintenance(noteDevice.getPriceDevice());
                            if (noteDevice.getDateNote() != null) {
                                noteDeviceResponse.setAppointmentDate(dateFormat.format(noteDevice.getDateNote()));
                            }

                            DeviceResponse deviceResponse = new DeviceResponse();
                            deviceResponse.setDeviceId(noteDevice.getDevice().getId());
                            deviceResponse.setName(noteDevice.getDevice().getName());
                            deviceResponse.setGroupName(noteDevice.getDevice().getGroup().getName());
                            List<Image> images = noteDevice.getDevice().getImages();
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

                if (request.getProject() != null) {
                    ProjectResponse projectResponse = new ProjectResponse();
                    projectResponse.setId(request.getProject().getId().toString());
                    projectResponse.setName(request.getProject().getProjectName());
                    reqResponse.setProject(projectResponse);
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
                if (deliveryNote.getRequest() != null) {
                    deliveryNoteResponse.setAssignee(convertUserToUserResponse(deliveryNote.getRequest().getCreatedBy()));
                }
                deliveryNoteResponse.setProvider(convertProviderToProviderResponse(deliveryNote.getProvider(), false));
            }

            return deliveryNoteResponse;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static NotificationResponse convertNotificationToNotificationResponse(Notifications notifications) {
        try {
            NotificationResponse notificationResponse = new NotificationResponse();

            if (notifications != null) {
                notificationResponse.setId(notifications.getId());
                notificationResponse.setTitle(notifications.getTitle());
                notificationResponse.setContent(notifications.getContent());
                notificationResponse.setRead(notifications.getRead());
                notificationResponse.setPath(notifications.getPath());
                if (notifications.getCreatedAt() != null) {
                    notificationResponse.setCreatedAt(timeFormat.format(notifications.getCreatedAt()));
                }
                notificationResponse.setCreatedBy(convertUserToUserResponse(notifications.getCreatedBy()));
            }
            return notificationResponse;
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
                if (user.getUserProjects() != null) {
                    for (UserProject project : user.getUserProjects()) {
                        ProjectResponse projectResponse = convertProjectToProjectResponse(project);
                        userResponse.getProjects().add(projectResponse);
                    }
                 }
            }

            return userResponse;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static ProjectResponse convertProjectToProjectResponse(UserProject userProject) {
        try {
            ProjectResponse projectResponse = new ProjectResponse();
            if (userProject != null) {
                projectResponse.setId(userProject.getProject().getId().toString());
                projectResponse.setName(userProject.getProject().getProjectName());
                if (userProject.getDateOfJoin() != null) {
                    projectResponse.setDateJoin(dateFormat.format(userProject.getDateOfJoin()));
                }
            }
            return projectResponse;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static GroupResponse convertGroupToGroupResponse(Group group) {
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

    public static DepartmentResponse convertDepartmentToDepartmentResponse(Department department) {
        try {
            DepartmentResponse departmentResponse = new DepartmentResponse();

            if (department != null) {
                departmentResponse.setId(department.getId());
                departmentResponse.setName(department.getName());
                if (department.getUsers() != null) {
                    for (User user : department.getUsers()) {
                        UserResponse userResponse = convertUserToUserResponse(user);
                        departmentResponse.getUsers().add(userResponse);
                    }
                }
            }

            return departmentResponse;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static GroupResponse convertGroupToGroupResponse(Group group, Integer activeQuantity, Integer stockQuantity, Integer maintenanceQuantity) {
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

    public static ProviderResponse convertProviderToProviderResponse(Provider provider, Boolean isSelectList) {
        try {
            ProviderResponse providerResponse = new ProviderResponse();
            if (provider != null) {
                providerResponse.setId(provider.getId());
                providerResponse.setName(provider.getName());
                providerResponse.setAddress(provider.getAddress());
                providerResponse.setPhoneNumber(provider.getPhoneNumber());
                if (!provider.getDevices().isEmpty() && isSelectList == false) {
                    for (Device device : provider.getDevices()) {
                        DeviceResponse deviceResponse = convertDeviceToDeviceRes(device, METHOD_LIST);
                        providerResponse.getDevices().add(deviceResponse);
                    }
                }
            }

            return providerResponse;
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // parse string to date if field is date
    public static Object parseValue(String field, String value, SimpleDateFormat dateFormat) throws ParseException {
        if (field.toLowerCase().contains("date")) {
            if (value == null || value.trim().isEmpty()) return null;
            return dateFormat.parse(value);
        }
        return value;
    }
}
