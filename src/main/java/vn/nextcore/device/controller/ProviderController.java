package vn.nextcore.device.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.ErrorResponse;
import vn.nextcore.device.dto.resp.ProviderResponse;
import vn.nextcore.device.dto.resp.SpecificationResponse;
import vn.nextcore.device.service.provider.IProviderService;
import vn.nextcore.device.service.specification.ISpecificationService;

import java.util.List;

@RestController
@RequestMapping("api/provider")
public class ProviderController {
    @Autowired
    private IProviderService providerService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DataResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<List<ProviderResponse>> getAllSpecifications() {
        List<ProviderResponse> result = providerService.getAllProvider();
        return new DataResponse<>(result);
    }
}
