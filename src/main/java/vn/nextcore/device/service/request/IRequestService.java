package vn.nextcore.device.service.request;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import vn.nextcore.device.dto.req.DataRequest;
import vn.nextcore.device.dto.resp.ListRequestResponse;
import vn.nextcore.device.dto.resp.ReqResponse;
import vn.nextcore.device.entity.Request;

import java.util.Date;
import java.util.List;

public interface IRequestService {
    ReqResponse createRequest(HttpServletRequest request, DataRequest dataRequest);

    ListRequestResponse getRequests(
            String title,
            String createdDate,
            String approvedDate,
            String status,
            String type,
            Long createdBy,
            String sortCreatedDate,
            String sortApprovedDate,
            Integer offset,
            Integer limit);
}
