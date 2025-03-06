package vn.nextcore.device.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class FilterRequest {
    private String field;

    private String operator;

    private List<String> values;
}
