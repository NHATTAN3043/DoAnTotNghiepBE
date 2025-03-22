package vn.nextcore.device.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.nextcore.device.dto.req.DeviceRequest;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.DeviceResponse;
import vn.nextcore.device.dto.resp.ErrorResponse;
import vn.nextcore.device.dto.resp.ListDeviceResponse;
import vn.nextcore.device.service.device.IDeviceService;
import vn.nextcore.device.validation.anotations.AllowedParams;
import vn.nextcore.device.validation.anotations.CheckFieldsCreateDeviceValid;

import java.util.Map;

@RestController
@RequestMapping("/api/device")
@Validated
public class DeviceController {
    @Autowired
    private IDeviceService deviceService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<DeviceResponse> createDevice(
            @CheckFieldsCreateDeviceValid HttpServletRequest request,
            @Valid @ModelAttribute DeviceRequest deviceRequest,
            @RequestParam(value = "specifications", required = false) String specificationJson,
            @RequestParam(value = "files", required = false) MultipartFile[] multipartFiles) {
        DeviceResponse result = deviceService.createDevice(deviceRequest, specificationJson, multipartFiles, request);
        return new DataResponse<>(result);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<DeviceResponse> updateDevice(
            @PathVariable("id") String deviceId,
            @Valid @ModelAttribute DeviceRequest deviceRequest,
            @RequestParam(value = "specifications", required = false) String specificationJson,
            @RequestParam(value = "files", required = false) MultipartFile[] multipartFiles,
            @RequestParam(value = "imagesDelete", required = false) String imagesDelete,
            @RequestParam(value = "specificationsDelete", required = false) String specificationsDelete) {
        DeviceResponse result = deviceService.updateDevice(deviceId, deviceRequest, specificationJson, multipartFiles, imagesDelete, specificationsDelete);
        return new DataResponse<>(result);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public DataResponse<ListDeviceResponse> listDevices(
            @AllowedParams(allowed = {"status", "ordDateBuy", "ordDateMaintenance", "offset", "limit", "filters"})
            @RequestParam Map<String, String> allParams) {
        ListDeviceResponse result = deviceService.getAllDevices(allParams);
        return new DataResponse<>(result);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<DeviceResponse> getInfoDevice(@PathVariable(name = "id") String deviceId) {
        DeviceResponse result = deviceService.getInfoDevice(deviceId);
        return new DataResponse<>(result);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DataResponse.class))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<DeviceResponse> deleteDevice(@PathVariable(name = "id") String deviceId) {
        DeviceResponse result = deviceService.deleteDevice(deviceId);
        return new DataResponse<>(result);
    }
}
