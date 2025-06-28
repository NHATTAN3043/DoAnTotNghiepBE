package vn.nextcore.device.service.request;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.dto.req.ApproveRequest;
import vn.nextcore.device.dto.req.DataRequest;
import vn.nextcore.device.dto.req.FilterRequest;
import vn.nextcore.device.dto.req.ReqTypesRequest;
import vn.nextcore.device.dto.resp.ListRequestResponse;
import vn.nextcore.device.dto.resp.ReqGroupResponse;
import vn.nextcore.device.dto.resp.ReqResponse;
import vn.nextcore.device.entity.*;
import vn.nextcore.device.entity.key.UserRequestKey;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.enums.Status;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.*;
import vn.nextcore.device.repository.criteria.request.IRequestCriteriaRepository;
import vn.nextcore.device.security.jwt.JwtUtil;
import vn.nextcore.device.service.notification.INotificationService;
import vn.nextcore.device.util.CheckerUtils;
import vn.nextcore.device.util.JsonUtils;
import vn.nextcore.device.util.ParseUtils;
import vn.nextcore.device.validation.HandlerValidateParams;

import java.util.*;

@Service
public class RequestService implements IRequestService {
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IRequestCriteriaRepository requestCriteriaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteDeviceRepository noteDeviceRepository;

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RequestVisibleRepository requestVisibleRepository;

    private String BACK_OFFICE = "Back Office";

    private String EMPLOYEE = "Employee";

    private String MANAGER = "Manager";

    private List<String> allowedFields = Arrays.asList("approvedDate", "createdDate");

    private Long managerId = 3L;

    private String MANAGER_DETAIL_REQUEST_PATH = "/next-device/manager/request/";
    private String EMPLOYEE_DETAIL_REQUEST_PATH = "/next-device/employee/my-request/";
    private String BACKOFFICE_DETAIL_REQUEST_PATH = "/next-device/back-office/request/";


    @Override
    @Transactional
    public ReqResponse createRequest(HttpServletRequest request, DataRequest dataRequest) {
        Request newRequest = new Request();
        try {
            // add createBy
            User user = jwtUtil.extraUserFromRequest(request);
            newRequest.setCreatedBy(user);
            newRequest.setTitle(dataRequest.getTitle());
            newRequest.setDescription(dataRequest.getDescriptions());

            if (dataRequest.getProjectId() != null) {
                Project project = projectRepository.findProjectById(dataRequest.getProjectId());
                if (project == null) {
                    throw new HandlerException(ErrorCodeEnum.ER123.getCode(), ErrorCodeEnum.ER123.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
                }
                newRequest.setProject(project);
            }

            Set<RequestGroup> requestGroups = new HashSet<>();
            if (dataRequest.getRequestGroups() != null && !dataRequest.getRequestGroups().isEmpty()) {
                for (ReqTypesRequest typesRequest : dataRequest.getRequestGroups()) {
                    RequestGroup newReqGroup = new RequestGroup();
                    Group group = groupRepository.findGroupById(typesRequest.getGroupId());
                    if (group == null) {
                        throw new HandlerException(ErrorCodeEnum.ER125.getCode(), ErrorCodeEnum.ER125.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
                    }
                    newReqGroup.setGroup(group);
                    newReqGroup.setQuantity(typesRequest.getQuantity());
                    newReqGroup.setRequest(newRequest);
                    newReqGroup.setStatus("waiting");
                    requestGroups.add(newReqGroup);
                }
                newRequest.setRequestGroups(requestGroups);
            }

            newRequest.setCreatedDate(new Date());
            if (managerId.equals(user.getRole().getId())) {
                newRequest.setStatus(Status.REQUEST_APPROVED.getStatus());
            } else {
                newRequest.setStatus(Status.REQUEST_PENDING.getStatus());
            }
            newRequest.setRequestType(dataRequest.getRequestType());

            requestRepository.save(newRequest);

            List<User> listAdmin = userRepository.findAllByRoleIdAndDeletedAtIsNull(Long.parseLong("3"));
            List<String> tokens = getTokensFromUserAndSaveNotification(listAdmin, "Yêu cầu mới", user.getUserName() + " đã gửi một yêu cầu", user, MANAGER_DETAIL_REQUEST_PATH + newRequest.getId());
            String batchResponse = notificationService.sendNotification(tokens, "Yêu cầu mới", user.getUserName() + " đã gửi một yêu cầu", "addRequest", "new", MANAGER_DETAIL_REQUEST_PATH + newRequest.getId());
            return new ReqResponse(newRequest.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<String> getTokensFromUserAndSaveNotification(List<User> users, String title, String content, User createdBy, String path) {
        try {
            List<String> tokens = new ArrayList<>();
            for (User user : users) {
                List<DeviceTokens> userTokens = user.getUserDeviceTokens();
                Notifications notifications = new Notifications();
                notifications.setContent(content);
                notifications.setTitle(title);
                notifications.setCreatedBy(createdBy);
                notifications.setUser(user);
                notifications.setCreatedAt(new Date());
                notifications.setPath(path);

                notificationRepository.save(notifications);
                for (DeviceTokens tokenRecord : userTokens) {
                    tokens.add(tokenRecord.getToken());
                }
            }
            return tokens;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ListRequestResponse getRequests(HttpServletRequest request, Boolean isGetMyRequest, String title, String status, String type, Long createdBy, String sortCreatedDate, String sortApprovedDate, Integer offset, Integer limit, String dateFilter) {
        ListRequestResponse result = new ListRequestResponse();
        try {
            if (createdBy != null) {
                if (!userRepository.existsById(createdBy)) {
                    throw new HandlerException(ErrorCodeEnum.ER025.getCode(), ErrorCodeEnum.ER025.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
                }
            }
            List<FilterRequest> filters = validateDateFilters(dateFilter);

            User user = jwtUtil.extraUserFromRequest(request);
            if (isGetMyRequest) {
                result = requestCriteriaRepository.listRequestsCriteria(title, status, null, type, user.getId(), sortCreatedDate, sortApprovedDate, offset, limit, filters, user);
                return result;
            }

            if (EMPLOYEE.equals(user.getRole().getName())) {
                result = requestCriteriaRepository.listRequestsCriteria(title, status, null, type, user.getId(), sortCreatedDate, sortApprovedDate, offset, limit, filters, user);
                return result;
            }
            if (MANAGER.equals(user.getRole().getName())) {
                result = requestCriteriaRepository.listRequestsCriteria(title, status, null, type, createdBy, sortCreatedDate, sortApprovedDate, offset, limit, filters, user);
                return result;
            }
            if (BACK_OFFICE.equals(user.getRole().getName())) {
                result = requestCriteriaRepository.listRequestsCriteria(title, status, Status.REQUEST_PENDING.getStatus(), type, createdBy, sortCreatedDate, sortApprovedDate, offset, limit, filters, user);
                return result;
            }
            return result;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ReqResponse getReqDetail(String id) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            Request request = requestRepository.findRequestById(Long.valueOf(id));
            if (request == null) {
                throw new HandlerException(ErrorCodeEnum.ER135.getCode(), ErrorCodeEnum.ER135.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            ReqResponse result = ParseUtils.convertRequestToReqResponse(request, "details");
            for (ReqGroupResponse groupRes : result.getGroupRequest()) {
                Integer resQuantity = noteDeviceRepository.countDeviceResByGroupIdAndRequestIdAndStatus(groupRes.getGroupId(), result.getRequestId(), "allocate");
                groupRes.setResQuantity(resQuantity);
                Integer recallQuantity = noteDeviceRepository.countDeviceResByGroupIdAndRequestIdAndStatus(groupRes.getGroupId(), result.getRequestId(), "retrieve");
                groupRes.setRecalledQuantity(recallQuantity);
            }

            return result;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ReqResponse approveRequest(HttpServletRequest request, ApproveRequest data) {
        try {
            User user = jwtUtil.extraUserFromRequest(request);
            Request request1 = requestRepository.findRequestById(data.getId());

            if (request1 == null) {
                throw new HandlerException(ErrorCodeEnum.ER135.getCode(), ErrorCodeEnum.ER135.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }

            request1.setStatus(data.getStatus());
            request1.setApprover(user);
            request1.setApprovedDate(new Date());
            requestRepository.save(request1);

            boolean isApproved = Status.REQUEST_APPROVED.getStatus().equals(data.getStatus());

            if (request1.getCreatedBy().getRole().getId() == 2L) {
                List<User> userRequest = new ArrayList<>();
                userRequest.add(request1.getCreatedBy());
                List<String> userTokens = getTokensFromUserAndSaveNotification(userRequest, "Phê duyệt yêu cầu", isApproved ? user.getUserName() + " đã phê duyệt yêu cầu của bạn" : user.getUserName() + " đã từ chối yêu cầu của bạn", user, EMPLOYEE_DETAIL_REQUEST_PATH + data.getId());
                String res = notificationService.sendNotification(userTokens,
                        "Phê duyệt yêu cầu",
                        isApproved ? user.getUserName() + " đã phê duyệt yêu cầu của bạn" : user.getUserName() + " đã từ chối yêu cầu của bạn",
                        "approvedRequest", "new",
                        EMPLOYEE_DETAIL_REQUEST_PATH + data.getId());
            }

            if (isApproved == true) {
                List<User> listBackOffice = userRepository.findAllByRoleIdAndDeletedAtIsNull(Long.parseLong("1"));
                List<String> tokens = getTokensFromUserAndSaveNotification(listBackOffice, "Phê duyệt yêu cầu", "Một yêu cầu đã được phê duyệt", user, BACKOFFICE_DETAIL_REQUEST_PATH + data.getId());
                String batchResponse = notificationService.sendNotification(tokens,
                        "Phê duyệt yêu cầu", "Một yêu cầu đã được phê duyệt",
                        "approvedRequest", "new",
                        BACKOFFICE_DETAIL_REQUEST_PATH + data.getId());
            }

            return new ReqResponse(request1.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ReqResponse updateRequest(String id, HttpServletRequest request, DataRequest data) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            User user = jwtUtil.extraUserFromRequest(request);
            Request requestExists = requestRepository.findRequestById(Long.parseLong(id));
            if (requestExists == null) {
                throw new HandlerException(ErrorCodeEnum.ER135.getCode(), ErrorCodeEnum.ER135.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }

            if (!Status.REQUEST_PENDING.getStatus().equals(requestExists.getStatus()) && !Status.REQUEST_PROGRESS.getStatus().equals(requestExists.getStatus())) {
                throw new HandlerException(ErrorCodeEnum.ER159.getCode(), ErrorCodeEnum.ER159.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }

            if (!user.getId().equals(requestExists.getCreatedBy().getId()) && user.getRole().getId() == 2L) {
                throw new HandlerException(ErrorCodeEnum.ER160.getCode(), ErrorCodeEnum.ER160.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.FORBIDDEN);
            }


            if (requestExists.getDeliveryNotes() != null && !requestExists.getDeliveryNotes().isEmpty()) {
                boolean hasNotConfirmed = requestExists.getDeliveryNotes()
                        .stream()
                        .anyMatch(note -> !Boolean.TRUE.equals(note.getIsConfirm()));
                if (hasNotConfirmed) {
                    throw new HandlerException(ErrorCodeEnum.ER161.getCode(), ErrorCodeEnum.ER161.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
                }
            }

            if (data.getProjectId() != null) {
                Project project = projectRepository.findProjectById(data.getProjectId());
                if (project == null) {
                    throw new HandlerException(ErrorCodeEnum.ER123.getCode(), ErrorCodeEnum.ER123.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
                }
                requestExists.setProject(project);
            }

            if (data.getTitle() != null) {
                requestExists.setTitle(data.getTitle());
            }

            if (data.getDescriptions() != null) {
                requestExists.setDescription(data.getDescriptions());
            }

            if (data.getRequestType() != null) {
                requestExists.setRequestType(data.getRequestType());
            }
            if (data.getStatus() != null) {
                requestExists.setStatus(data.getStatus());
            }
            requestExists.setUpdatedAt(new Date());

            Set<RequestGroup> requestGroups = new HashSet<>();
            if (data.getRequestGroups() != null && !data.getRequestGroups().isEmpty()) {
                for (ReqTypesRequest typesRequest : data.getRequestGroups()) {
                    RequestGroup newReqGroup = new RequestGroup();
                    Group group = groupRepository.findGroupById(typesRequest.getGroupId());
                    if (group == null) {
                        throw new HandlerException(ErrorCodeEnum.ER125.getCode(), ErrorCodeEnum.ER125.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
                    }
                    newReqGroup.setGroup(group);
                    newReqGroup.setQuantity(typesRequest.getQuantity());
                    newReqGroup.setRequest(requestExists);
                    newReqGroup.setStatus("waiting");
                    requestGroups.add(newReqGroup);
                }
                requestExists.getRequestGroups().clear();
                requestExists.getRequestGroups().addAll(requestGroups);
            }

            requestRepository.save(requestExists);

            if (Status.REQUEST_DONE.getStatus().equals(data.getStatus())) {
                List<String> tokens = getTokensFromUserAndSaveNotification(Arrays.asList(requestExists.getCreatedBy()), "Yêu cầu hoàn thành", user.getUserName() + " đã hoàn thành yêu cầu của bạn", user, EMPLOYEE_DETAIL_REQUEST_PATH + requestExists.getId());
                String batchResponse = notificationService.sendNotification(tokens, "Yêu cầu hoàn thành", user.getUserName() + " đã hoàn thành yêu cầu của bạn", "doneRequest", "done", EMPLOYEE_DETAIL_REQUEST_PATH + requestExists.getId());
            }
            return new ReqResponse(requestExists.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ReqResponse updateRequestHidden(String id, HttpServletRequest request) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            User user = jwtUtil.extraUserFromRequest(request);
            Request requestExists = requestRepository.findRequestById(Long.parseLong(id));
            if (requestExists == null) {
                throw new HandlerException(ErrorCodeEnum.ER135.getCode(), ErrorCodeEnum.ER135.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            Optional<UserRequestVisibility> optionalVisibility = requestVisibleRepository.findByUserIdAndRequestId(user.getId(), Long.parseLong(id));

            if (optionalVisibility.isPresent()) {
                UserRequestVisibility visibility = optionalVisibility.get();
                visibility.setHidden(true);
                requestVisibleRepository.save(visibility);
            } else {
                UserRequestVisibility visibility = new UserRequestVisibility();

                UserRequestKey key = new UserRequestKey(user.getId(), Long.parseLong(id));
                visibility.setId(key);
                visibility.setUser(user);
                visibility.setRequest(requestExists);
                visibility.setHidden(true);

                requestVisibleRepository.save(visibility);
            }

            return new ReqResponse(requestExists.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<FilterRequest> validateDateFilters(String filterEncode) {
        if (filterEncode == null || filterEncode.isEmpty()) {
            return new ArrayList<>();
        }

        // decode filters json
        List<FilterRequest> filters = JsonUtils.decodeAndList(filterEncode);

        for (FilterRequest filter : filters) {
            CheckerUtils.validateFilterField(filter, allowedFields);
            CheckerUtils.validateDateFilter(filter);
        }

        return filters;
    }

}
