package vn.nextcore.device.service.request;

import jakarta.servlet.http.HttpServletRequest;
import vn.nextcore.device.dto.req.ApproveRequest;
import vn.nextcore.device.dto.req.DataRequest;
import vn.nextcore.device.dto.resp.ListRequestResponse;
import vn.nextcore.device.dto.resp.ReqResponse;


public interface IRequestService {
    ReqResponse createRequest(HttpServletRequest request, DataRequest dataRequest);

    ListRequestResponse getRequests(
            HttpServletRequest request,
            Boolean isGetMyRequest,
            String title,
            String status,
            String type,
            Long createdBy,
            String sortCreatedDate,
            String sortApprovedDate,
            Integer offset,
            Integer limit,
            String allParams);

    ReqResponse getReqDetail(String id);

    ReqResponse approveRequest(HttpServletRequest request , ApproveRequest data);

    ReqResponse updateRequest(String id, HttpServletRequest request , DataRequest data);

}
