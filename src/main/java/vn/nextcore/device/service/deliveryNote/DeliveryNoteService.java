package vn.nextcore.device.service.deliveryNote;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.dto.req.DeliveryNoteRequest;
import vn.nextcore.device.dto.req.NoteDeviceRequest;
import vn.nextcore.device.dto.resp.DeliveryNoteResponse;
import vn.nextcore.device.entity.*;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.DeliveryNoteRepository;
import vn.nextcore.device.repository.DeviceRepository;
import vn.nextcore.device.repository.ProviderRepository;
import vn.nextcore.device.repository.RequestRepository;
import vn.nextcore.device.security.jwt.JwtUtil;
import vn.nextcore.device.service.request.IRequestService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DeliveryNoteService implements IDeliveryNoteService {
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeliveryNoteRepository deliveryNoteRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public DeliveryNoteResponse createDeliveryNote(HttpServletRequest httpRequest, DeliveryNoteRequest request) {
        try {
            DeliveryNote deliveryNote = new DeliveryNote();
            deliveryNote.setDeliveryDate(new Date());
            deliveryNote.setCreatedAt(new Date());
            deliveryNote.setTypeNote(request.getTypeNote());
            deliveryNote.setDescription(request.getDescription());
            deliveryNote.setIsConfirm(false);

            Request requestExists = requestRepository.findRequestById(request.getRequestId());
            if (requestExists == null) {
                throw new HandlerException(ErrorCodeEnum.ER135.getCode(), ErrorCodeEnum.ER135.getMessage(), PathEnum.DELIVERY_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            deliveryNote.setRequest(requestExists);

            // add createBy
            User user = jwtUtil.extraUserFromRequest(httpRequest);
            deliveryNote.setCreatedBy(user);

            if (request.getProviderId() != null) {
                Provider provider = providerRepository.findProviderById(request.getProviderId());
                deliveryNote.setProvider(provider);
            }

            List<NoteDevice> noteDevices = new ArrayList<>();
            for (NoteDeviceRequest note : request.getNoteDeviceRequests()) {
                NoteDevice noteDevice = new NoteDevice();
                noteDevice.setDescriptionDevice(note.getDescriptionDevice());
                Device device = deviceRepository.findDeviceById(note.getDeviceId());

                if ("allocate".equals(request.getTypeNote())) {
                    device.setUsingBy(requestExists.getCreatedBy());
                    // change status device
                    device.setStatus("active");
                }

                if ("retrieve".equals(request.getTypeNote())) {
                    device.setUsingBy(null);
                    // change status device
                    device.setStatus("stock");
                }

                if ("maintenance".equals(request.getTypeNote())) {
                    device.setUsingBy(null);
                    device.setStatus("maintenance");
                }

                noteDevice.setDevice(device);
                noteDevice.setDeliveryNote(deliveryNote);
                noteDevices.add(noteDevice);
            }
            deliveryNote.setNoteDevices(noteDevices);
            deliveryNoteRepository.save(deliveryNote);

            return new DeliveryNoteResponse(deliveryNote.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DELIVERY_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DELIVERY_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
