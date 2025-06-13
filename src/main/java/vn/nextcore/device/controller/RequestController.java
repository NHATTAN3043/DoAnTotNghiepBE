package vn.nextcore.device.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.req.ApproveRequest;
import vn.nextcore.device.dto.req.DataRequest;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.ListRequestResponse;
import vn.nextcore.device.dto.resp.ReqResponse;
import vn.nextcore.device.service.request.IRequestService;

@RestController
@RequestMapping("/api/request")
@Validated
public class RequestController {
    @Autowired
    private IRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReqResponse createRequest(
            HttpServletRequest request,
            @RequestBody DataRequest dataRequest
    ) {
        return requestService.createRequest(request, dataRequest);
    }

    @GetMapping
    public DataResponse<ListRequestResponse> getRequest(
            HttpServletRequest request,
            @RequestParam(required = false) String dateFilter,
            @RequestParam(required = false, defaultValue = "false") Boolean isGetMyRequest,
            @RequestParam(required = false) String title,
            @RequestParam(required = false)
            @Pattern(regexp = "pending|approved|done|progress|rejected|", flags = Pattern.Flag.CASE_INSENSITIVE, message = "ER129")
            String status,
            @RequestParam(required = false)
            @Pattern(regexp = "1|2|3|4|5", message = "ER134")
            String type,
            @RequestParam(required = false)
            Long createdBy,
            @RequestParam(defaultValue = "0")
            @Min(value = 0,message = "ER130")
            int offset,
            @RequestParam(defaultValue = "10")
            @Min(value = 1,message = "ER131")
            int limit,
            @RequestParam(required = false)
            @Pattern(regexp = "^$|asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE,
                    message = "ER132")
            String sortCreatedDate,
            @RequestParam(required = false)
            @Pattern(regexp = "^$|asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE,
                    message = "ER133")
            String sortApprovedDate
    ) {
        ListRequestResponse result = requestService.getRequests(request, isGetMyRequest, title, status, type, createdBy, sortCreatedDate, sortApprovedDate, offset, limit, dateFilter);
        return new DataResponse<>(result);
    }

    @GetMapping("/{id}")
    public DataResponse<ReqResponse> getRequestDetail(@PathVariable(name = "id") String requestId) {
        ReqResponse result = requestService.getReqDetail(requestId);
        return new DataResponse<>(result);
    }

    @PutMapping
    public DataResponse<ReqResponse> approvedRequest(HttpServletRequest request, @RequestBody ApproveRequest data) {
        ReqResponse result = requestService.approveRequest(request, data);
        return new DataResponse<>(result);
    }
 }
