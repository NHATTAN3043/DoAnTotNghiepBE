package vn.nextcore.device.service.deliveryNote;

import jakarta.servlet.http.HttpServletRequest;
import vn.nextcore.device.dto.req.DeliveryNoteRequest;
import vn.nextcore.device.dto.resp.DeliveryNoteResponse;

public interface IDeliveryNoteService {
    DeliveryNoteResponse createDeliveryNote(HttpServletRequest httpRequest, DeliveryNoteRequest request);
}
