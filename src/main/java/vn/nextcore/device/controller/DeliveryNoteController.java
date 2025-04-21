package vn.nextcore.device.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.req.ApproveRequest;
import vn.nextcore.device.dto.req.DeliveryNoteRequest;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.DeliveryNoteResponse;
import vn.nextcore.device.dto.resp.ReqResponse;
import vn.nextcore.device.service.deliveryNote.IDeliveryNoteService;

@RestController
@RequestMapping("/api/delivery")
@Validated
public class DeliveryNoteController {

    @Autowired
    private IDeliveryNoteService deliveryNoteService;

    @PostMapping
    public DataResponse<DeliveryNoteResponse> createDeliveryNote(HttpServletRequest httpRequest, @RequestBody DeliveryNoteRequest request) {
        DeliveryNoteResponse result = deliveryNoteService.createDeliveryNote(httpRequest, request);
        return new DataResponse<>(result);
    }

    @PutMapping
    public DataResponse<DeliveryNoteResponse> confirmDeliveryNote(HttpServletRequest request, @RequestBody DeliveryNoteRequest data) {
        DeliveryNoteResponse result = deliveryNoteService.confirmDeliveryNote(request, data);
        return new DataResponse<>(result);
    }
}
