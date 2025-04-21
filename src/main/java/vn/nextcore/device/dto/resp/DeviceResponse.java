package vn.nextcore.device.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DeviceResponse {
    private Long deviceId;

    private String name;

    private String priceBuy;

    private String priceSell;

    private String status;

    private String description;

    private String groupName;

    private String providerName;

    private String dateBuy;

    private String dateSell;

    private String dateMaintenance;

    private GroupResponse group;

    private ProviderResponse provider;

    private List<ImageResponse> images = null;

    private List<SpecificationResponse> specifications;

    private String image;
}
