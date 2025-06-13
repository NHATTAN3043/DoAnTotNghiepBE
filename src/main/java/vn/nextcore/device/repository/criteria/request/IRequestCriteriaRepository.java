package vn.nextcore.device.repository.criteria.request;

import vn.nextcore.device.dto.req.FilterRequest;
import vn.nextcore.device.dto.resp.ListDeviceResponse;
import vn.nextcore.device.dto.resp.ListRequestResponse;

import java.util.Date;
import java.util.List;

public interface IRequestCriteriaRepository {
    ListRequestResponse listRequestsCriteria(
            String title,
            String status,
            String exceptStatus,
            String type,
            Long createdBy,
            String sortCreatedDate,
            String sortApprovedDate,
            Integer offset,
            Integer limit,
            List<FilterRequest> dateFilters
    );
}
