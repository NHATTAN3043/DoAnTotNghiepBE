package vn.nextcore.device.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.QuantityStatisticsResponse;
import vn.nextcore.device.dto.resp.UserResponse;
import vn.nextcore.device.dto.resp.YearlyStatisticsResponse;
import vn.nextcore.device.service.statistics.IStatisticsService;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private IStatisticsService statisticsService;

    @GetMapping("/overview")
    public DataResponse<QuantityStatisticsResponse> overviewStatistics() {
        QuantityStatisticsResponse result = statisticsService.overviewStatistics();
        return new DataResponse<>(result);
    }

    @GetMapping("/action/{year}")
    public DataResponse<YearlyStatisticsResponse> actionStatistics(@PathVariable(name = "year") String year) {
        YearlyStatisticsResponse result = statisticsService.actionStatistics(year);
        return new DataResponse<>(result);
    }

    @GetMapping("/usingDevice")
    public DataResponse<List<UserResponse>> usingDeviceStatistics(@RequestParam(name = "userName", required = false) String userName) {
        List<UserResponse> result = statisticsService.deviceUsingUser(userName);
        return new DataResponse<>(result);
    }
}
