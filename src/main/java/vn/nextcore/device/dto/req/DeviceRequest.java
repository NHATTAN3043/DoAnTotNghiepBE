package vn.nextcore.device.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import vn.nextcore.device.validation.anotations.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@CheckDateMaintenanceLaterThanDateBuy(message = "ER035")
public class DeviceRequest {
    @NotBlank(message = "ER008")
    private String name;

    @NotBlank(message = "ER009")
    @Pattern(regexp = "^[0-9]\\d*$", message = "ER010")
    private String priceBuy;

    @Pattern(regexp = "^[0-9]\\d*$", message = "ER011")
    private String priceSell;

    private String description;

    @NotBlank(message = "ER012")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "ER013")
    @CheckDateValid(message = "ER014")
    @Schema(example = "10/07/2024")
    private String dateBuy;

    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "ER015")
    @CheckDateValid(message = "ER016")
    @Schema(example = "10/07/2024")
    private String dateSell;

    @NotBlank(message = "ER017")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "ER018")
    @CheckDateValid(message = "ER019")
    @Schema(example = "10/07/2025")
    private String dateMaintenance;

    @NotBlank(message = "ER020")
    @Pattern(regexp = "^[1-9]\\d*$", message = "ER021")
    @CheckGroupIdExists(message = "ER022")
    @Schema(example = "1")
    private String groupId;

    @NotBlank(message = "ER026")
    @Pattern(regexp = "^[1-9]\\d*$", message = "ER027")
    @CheckProviderIdExists(message = "ER028")
    @Schema(example = "1")
    private String providerId;
}
