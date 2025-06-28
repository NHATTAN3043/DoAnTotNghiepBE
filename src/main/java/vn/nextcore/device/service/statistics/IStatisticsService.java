package vn.nextcore.device.service.statistics;

import vn.nextcore.device.dto.resp.QuantityStatisticsResponse;
import vn.nextcore.device.dto.resp.UserResponse;
import vn.nextcore.device.dto.resp.YearlyStatisticsResponse;

import java.util.List;

public interface IStatisticsService {
    QuantityStatisticsResponse overviewStatistics();

    YearlyStatisticsResponse actionStatistics(String year);

    List<UserResponse> deviceUsingUser(String userName);
}
