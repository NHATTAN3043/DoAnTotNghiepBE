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
import vn.nextcore.device.enums.Status;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.DeliveryNoteRepository;
import vn.nextcore.device.repository.DeviceRepository;
import vn.nextcore.device.repository.ProviderRepository;
import vn.nextcore.device.repository.RequestRepository;
import vn.nextcore.device.security.jwt.JwtUtil;
import vn.nextcore.device.service.notification.INotificationService;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    private INotificationService notificationService;

    @Autowired
    private JwtUtil jwtUtil;
    private static final String ALLOCATE = "allocate";
    private static final String RETRIEVE = "retrieve";
    private static final String MAINTENANCE = "maintenance";
    private static final String BROKEN = "broken";
    private static final String STOCK = "stock";
    private static final String ACTIVE = "active";
    private static final String GOOD = "good";
    private String EMPLOYEE_DETAIL_REQUEST_PATH = "/next-device/employee/my-request/";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


    @Override
    @Transactional
    public DeliveryNoteResponse createDeliveryNote(HttpServletRequest httpRequest, DeliveryNoteRequest request) {
        try {
            DeliveryNote deliveryNote = new DeliveryNote();
            deliveryNote.setDeliveryDate(new Date());
            deliveryNote.setCreatedAt(new Date());
            deliveryNote.setTypeNote(request.getTypeNote());
            deliveryNote.setDescription(request.getDescription());
            deliveryNote.setIsConfirm(null);

            Request requestExists = requestRepository.findRequestById(request.getRequestId());
            if (requestExists == null) {
                throw new HandlerException(ErrorCodeEnum.ER135.getCode(), ErrorCodeEnum.ER135.getMessage(), PathEnum.DELIVERY_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            requestExists.setStatus(Status.REQUEST_PROGRESS.getStatus());
            deliveryNote.setRequest(requestExists);

            if (request.getProviderId() != null && MAINTENANCE.equals(request.getTypeNote())) {
                Provider provider = providerRepository.findProviderByIdAndDeletedAtIsNull(request.getProviderId());
                if (provider == null) {
                    throw new HandlerException(ErrorCodeEnum.ER126.getCode(), ErrorCodeEnum.ER126.getMessage(), PathEnum.DELIVERY_PATH.getPath(), HttpStatus.BAD_REQUEST);
                }
                deliveryNote.setProvider(provider);
            }

            // add createBy
            User user = jwtUtil.extraUserFromRequest(httpRequest);
            deliveryNote.setCreatedBy(user);

            if (request.getProviderId() != null) {
                Provider provider = providerRepository.findProviderByIdAndDeletedAtIsNull(request.getProviderId());
                deliveryNote.setProvider(provider);
            }

            List<NoteDevice> noteDevices = new ArrayList<>();
            for (NoteDeviceRequest note : request.getNoteDeviceRequests()) {
                NoteDevice noteDevice = new NoteDevice();
                noteDevice.setDescriptionDevice(note.getDescriptionDevice());
                if (request.getAppointmentDate() != null) {
                    noteDevice.setDateNote(dateFormat.parse(request.getAppointmentDate()));
                }
                Device device = deviceRepository.findDeviceById(note.getDeviceId());

                if (ALLOCATE.equals(request.getTypeNote())) {
                    device.setUsingBy(requestExists.getCreatedBy());
                    device.setStatus(ACTIVE);
                }

                if (RETRIEVE.equals(request.getTypeNote())) {
                    device.setUsingBy(null);
                    // change status device
                    device.setStatus(STOCK);
                }

                if (MAINTENANCE.equals(request.getTypeNote())) {
                    device.setUsingBy(user);
                    device.setStatus(MAINTENANCE);
                }

                if (BROKEN.equals(note.getDescriptionDevice())) {
                    device.setIsBroken(true);
                }

                if (GOOD.equals(note.getDescriptionDevice())) {
                    device.setIsBroken(false);
                }

                noteDevice.setDevice(device);
                noteDevice.setDeliveryNote(deliveryNote);
                noteDevices.add(noteDevice);
            }
            deliveryNote.setNoteDevices(noteDevices);
            deliveryNoteRepository.save(deliveryNote);
            // save notification
            Notifications notifications = new Notifications();
            notifications.setTitle("Xử lý yêu cầu");
            notifications.setContent(user.getUserName() + " đã xử lý yêu cầu của bạn");
            notifications.setCreatedBy(user);
            notifications.setUser(deliveryNote.getRequest().getCreatedBy());
            notifications.setCreatedAt(new Date());
            notifications.setPath("/");

            List<String> tokens = deliveryNote.getRequest().getCreatedBy()
                    .getUserDeviceTokens()
                    .stream()
                    .map(DeviceTokens::getToken )
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            String batchResponse = notificationService.sendNotification(tokens,
                    "Xử lý yêu cầu", user.getUserName() + " đã xử lý yêu cầu của bạn",
                    "addDeliveryNote", "new", EMPLOYEE_DETAIL_REQUEST_PATH + deliveryNote.getRequest().getId());

            return new DeliveryNoteResponse(deliveryNote.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DELIVERY_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DELIVERY_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DeliveryNoteResponse confirmDeliveryNote(HttpServletRequest request, DeliveryNoteRequest data) {
        try {
            User user = jwtUtil.extraUserFromRequest(request);
            DeliveryNote deliveryNote = deliveryNoteRepository.findDeliveryNoteById(data.getId());
            if (deliveryNote == null) {
                throw new HandlerException(ErrorCodeEnum.ER102.getCode(), ErrorCodeEnum.ER102.getMessage(), PathEnum.DELIVERY_PATH.getPath(), HttpStatus.UNAUTHORIZED);
            }

            if (!user.getId().equals(deliveryNote.getRequest().getCreatedBy().getId()) ) {
                throw new HandlerException(ErrorCodeEnum.ER136.getCode(), ErrorCodeEnum.ER136.getMessage(), PathEnum.DELIVERY_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }

            for (NoteDevice noteDevice : deliveryNote.getNoteDevices()) {
                if (Status.ACTION_RETRIEVE.getStatus().equals(noteDevice.getDeliveryNote().getTypeNote())) {
                    noteDevice.getDevice().setStatus(STOCK);
                } else if (Status.ACTION_ALLOCATE.getStatus().equals(noteDevice.getDeliveryNote().getTypeNote())) {
                    noteDevice.getDevice().setStatus(ACTIVE);
                } else {
                    noteDevice.getDevice().setStatus(MAINTENANCE);
                }
            }
            deliveryNote.setIsConfirm(data.getIsConfirm());
            deliveryNoteRepository.save(deliveryNote);
            return new DeliveryNoteResponse(deliveryNote.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DELIVERY_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DELIVERY_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
