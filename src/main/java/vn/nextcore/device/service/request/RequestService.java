package vn.nextcore.device.service.request;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.dto.req.ApproveRequest;
import vn.nextcore.device.dto.req.DataRequest;
import vn.nextcore.device.dto.req.ReqTypesRequest;
import vn.nextcore.device.dto.resp.ListRequestResponse;
import vn.nextcore.device.dto.resp.ReqResponse;
import vn.nextcore.device.entity.*;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.enums.StatusRequest;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.GroupRepository;
import vn.nextcore.device.repository.ProjectRepository;
import vn.nextcore.device.repository.RequestRepository;
import vn.nextcore.device.repository.UserRepository;
import vn.nextcore.device.repository.criteria.request.IRequestCriteriaRepository;
import vn.nextcore.device.security.jwt.JwtUtil;
import vn.nextcore.device.util.ParseUtils;

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
            for(ReqTypesRequest typesRequest : dataRequest.getRequestGroups()) {
                RequestGroup newReqGroup = new RequestGroup();
                Group group = groupRepository.findGroupById(typesRequest.getGroupId());
                if (group == null) {
                    throw new HandlerException(ErrorCodeEnum.ER125.getCode(), ErrorCodeEnum.ER125.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
                }
                newReqGroup.setGroup(group);
                newReqGroup.setQuantity(typesRequest.getQuantity());
                newReqGroup.setRequest(newRequest);
                requestGroups.add(newReqGroup);
            }
            newRequest.setRequestGroups(requestGroups);
            newRequest.setCreatedDate(new Date());
            newRequest.setStatus(StatusRequest.REQUEST_PENDING.getStatus());
            newRequest.setRequestType(dataRequest.getRequestType());
            requestRepository.save(newRequest);
            return new ReqResponse(newRequest.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ListRequestResponse getRequests(String title, String createdDate, String approvedDate, String status, String type, Long createdBy, String sortCreatedDate, String sortApprovedDate, Integer offset, Integer limit) {
        try {
            if (createdBy != null) {
                if (!userRepository.existsById(createdBy))  {
                    throw new HandlerException(ErrorCodeEnum.ER025.getCode(), ErrorCodeEnum.ER025.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
                }
            }

            ListRequestResponse result = requestCriteriaRepository.listRequestsCriteria(title, createdDate, approvedDate, status, type, createdBy, sortCreatedDate, sortApprovedDate, offset, limit);
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
            Request request = requestRepository.findRequestById(Long.valueOf(id));
            if (request == null) {
                throw new HandlerException(ErrorCodeEnum.ER135.getCode(), ErrorCodeEnum.ER135.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            ReqResponse result = ParseUtils.convertRequestToReqResponse(request, "details");
            return result;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
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
            return new ReqResponse(request1.getId());
        }  catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), handlerException.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
