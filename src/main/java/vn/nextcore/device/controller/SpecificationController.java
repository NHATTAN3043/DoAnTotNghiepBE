package vn.nextcore.device.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.resp.*;
import vn.nextcore.device.service.specification.ISpecificationService;
import java.util.List;

@RestController
@RequestMapping("/api/specification")
public class SpecificationController {
    @Autowired
    private ISpecificationService specificationService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DataResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<List<SpecificationResponse>> getAllSpecifications() {
        List<SpecificationResponse> result = specificationService.getAllSpecification();
        return new DataResponse<>(result);
    }
}
