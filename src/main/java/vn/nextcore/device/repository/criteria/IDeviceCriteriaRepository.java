package vn.nextcore.device.repository.criteria;

import vn.nextcore.device.dto.req.FilterRequest;
import vn.nextcore.device.dto.resp.DeviceResponse;
import vn.nextcore.device.dto.resp.ListDeviceResponse;

import java.util.List;

public interface IDeviceCriteriaRepository {
    ListDeviceResponse listDeviceCriteria(
            String status,
            String sortDateBuy,
            String sortDateMaintenance,
            Integer offset,
            Integer limit,
            List<FilterRequest> filters
    );
}
