package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class ListDeviceResponse {
    private Integer totalRecords;

    private Integer offset;

    private Integer limit;

    private Integer totalPages;

    private Integer currentPage;

    private List<DeviceResponse> devices = new ArrayList<>();

    public void calculatePagination() {
        if (totalRecords == 0 || offset == null) {
            this.totalPages = 1;
            this.currentPage = 1;
        } else {
            this.totalPages = (int) Math.ceil((double) totalRecords / limit);
            this.currentPage = (offset / limit) + 1;
        }
    }
}
