package vn.nextcore.device.service.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.nextcore.device.dto.resp.*;
import vn.nextcore.device.entity.Group;
import vn.nextcore.device.entity.User;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.enums.Status;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.DeviceRepository;
import vn.nextcore.device.repository.GroupRepository;
import vn.nextcore.device.repository.NoteDeviceRepository;
import vn.nextcore.device.repository.UserRepository;
import vn.nextcore.device.util.ParseUtils;
import vn.nextcore.device.validation.HandlerValidateParams;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService implements IStatisticsService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private NoteDeviceRepository noteDeviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public QuantityStatisticsResponse overviewStatistics() {
        QuantityStatisticsResponse result = new QuantityStatisticsResponse();
        try {
            Integer totalDevice = deviceRepository.countDeviceByDeletedAtIsNull();
            Integer usedDevice = deviceRepository.countDeviceByStatusAndDeletedAtIsNull(Status.DEVICE_ACTIVE.getStatus());
            Integer stockDevice = deviceRepository.countDeviceByStatusAndDeletedAtIsNull(Status.DEVICE_STOCK.getStatus());
            Integer maintenance = deviceRepository.countDeviceByStatusAndDeletedAtIsNull(Status.DEVICE_MAINTENANCE.getStatus());
            Integer scrap = deviceRepository.countDeviceByStatusAndDeletedAtIsNull(Status.DEVICE_SCRAP.getStatus());
            List<Group> groupList = groupRepository.findAll();

            for (Group group : groupList) {
                Integer stockQuantity = deviceRepository.countDeviceByGroupIdAndStatusAndDeletedAtIsNull(group.getId(), Status.DEVICE_STOCK.getStatus());
                Integer activeQuantity = deviceRepository.countDeviceByGroupIdAndStatusAndDeletedAtIsNull(group.getId(), Status.DEVICE_ACTIVE.getStatus());
                Integer maintenanceQuantity = deviceRepository.countDeviceByGroupIdAndStatusAndDeletedAtIsNull(group.getId(), Status.DEVICE_MAINTENANCE.getStatus());
                Integer scraped = deviceRepository.countDeviceByGroupIdAndStatusAndDeletedAtIsNull(group.getId(), Status.DEVICE_MAINTENANCE.getStatus());

                GroupResponse groupResponse = ParseUtils.convertGroupToGroupResponse(group, activeQuantity, stockQuantity, maintenanceQuantity, scraped);
                result.getGroupStatistics().add(groupResponse);
            }

            result.setTotalDevice(totalDevice);
            result.setTotalUsed(usedDevice);
            result.setTotalStock(stockDevice);
            result.setTotalMaintenance(maintenance);
            result.setTotalScrap(scrap);
            return result;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.STATISTICS_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.STATISTICS_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public YearlyStatisticsResponse actionStatistics(String year) {
        YearlyStatisticsResponse result = new YearlyStatisticsResponse();
        try {
            HandlerValidateParams.validateYear(year, ErrorCodeEnum.ER142);
            int totalMonth = 12;
            String currentYear = String.valueOf(LocalDate.now().getYear());
            if (currentYear.equals(year)) {
                totalMonth = LocalDate.now().getMonthValue(); // set current month
            }

            Integer totalAllocate = noteDeviceRepository.countDeviceByActionAndYear(Status.ACTION_ALLOCATE.getStatus(), year);
            Integer totalRetrieve = noteDeviceRepository.countDeviceByActionAndYear(Status.ACTION_RETRIEVE.getStatus(), year);
            Integer totalMaintenance = noteDeviceRepository.countDeviceByActionAndYear(Status.ACTION_MAINTENANCE.getStatus(), year);

            for (int i = 1; i <= totalMonth; i++) {
                Integer allocateQ = noteDeviceRepository.countDeviceByActionAndMonthAndYear(Status.ACTION_ALLOCATE.getStatus(), i, year);
                Integer retrieveQ = noteDeviceRepository.countDeviceByActionAndMonthAndYear(Status.ACTION_RETRIEVE.getStatus(), i, year);
                Integer mainQ = noteDeviceRepository.countDeviceByActionAndMonthAndYear(Status.ACTION_MAINTENANCE.getStatus(), i, year);

                MonthlyStatisticsResponse month = new MonthlyStatisticsResponse();
                month.setMonth(i);
                month.setAllocateQuantity(allocateQ);
                month.setRetrieveQuantity(retrieveQ);
                month.setMaintenanceQuantity(mainQ);
                result.getMonthlyStatistics().add(month);
            }

            result.setYear(year);
            result.setTotalAllocate(totalAllocate);
            result.setTotalRetrieve(totalRetrieve);
            result.setTotalMaintenance(totalMaintenance);
            return result;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.STATISTICS_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.STATISTICS_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<UserResponse> deviceUsingUser(String userName) {
        List<UserResponse> userResponseList = new ArrayList<>();
        try {
            List<User> users = userRepository.searchByTenIgnoreCaseAndAccent(userName);
            for (User user : users) {
                UserResponse userResponse = ParseUtils.convertUserToUserResponse(user);
                userResponseList.add(userResponse);
            }
            return userResponseList;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.STATISTICS_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.STATISTICS_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
