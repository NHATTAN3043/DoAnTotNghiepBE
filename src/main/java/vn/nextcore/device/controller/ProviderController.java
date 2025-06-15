package vn.nextcore.device.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.req.ProviderRequest;
import vn.nextcore.device.dto.resp.*;
import vn.nextcore.device.service.provider.IProviderService;
import vn.nextcore.device.service.specification.ISpecificationService;

import java.util.List;

@RestController
@RequestMapping("/api/provider")
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
    public DataResponse<List<ProviderResponse>> getAllProviders(
            @RequestParam(required = false) String providerName,
            @RequestParam(required = false, defaultValue = "true") Boolean isSelectList
    ) {
        List<ProviderResponse> result = providerService.getAllProvider(providerName, isSelectList);
        return new DataResponse<>(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<ProviderResponse> createProvider(@RequestBody ProviderRequest req) {
        ProviderResponse result = providerService.createProvider(req);
        return new DataResponse<>(result);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<ProviderResponse> updateProvider(
            @PathVariable("id") String providerId,
            @RequestBody ProviderRequest req) {
        ProviderResponse result = providerService.updateProvider(providerId, req);
        return new DataResponse<>(result);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<ProviderResponse> getDetailProvider(@PathVariable("id") String id) {
        ProviderResponse result = providerService.getDetailProvider(id);
        return new DataResponse<>(result);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    public DataResponse<ProviderResponse> disableProvider(
            @PathVariable("id") String id) {
        ProviderResponse result = providerService.deleteProvider(id);
        return new DataResponse<>(result);
    }
}
