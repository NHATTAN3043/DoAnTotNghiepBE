package vn.nextcore.device.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.req.DataRequest;
import vn.nextcore.device.dto.resp.ListRequestResponse;
import vn.nextcore.device.dto.resp.ReqResponse;
import vn.nextcore.device.entity.Request;
import vn.nextcore.device.service.request.IRequestService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/request")
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
    public ListRequestResponse getRequest(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String createdDate,
            @RequestParam(required = false) String approvedDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long createdBy,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "desc") String sortCreatedDate,
            @RequestParam(defaultValue = "desc") String sortApprovedDate
    ) {
        return requestService.getRequests(title, createdDate, approvedDate, status, type, createdBy, sortCreatedDate, sortApprovedDate, offset, limit);
    }
}
