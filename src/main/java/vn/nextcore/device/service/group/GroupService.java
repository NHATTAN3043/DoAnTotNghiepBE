package vn.nextcore.device.service.group;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.dto.req.GroupRequest;
import vn.nextcore.device.dto.resp.DeviceResponse;
import vn.nextcore.device.dto.resp.GroupResponse;
import vn.nextcore.device.entity.Device;
import vn.nextcore.device.entity.Group;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.DeviceRepository;
import vn.nextcore.device.repository.GroupRepository;
import vn.nextcore.device.enums.ErrorCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.nextcore.device.util.ParseUtils;
import vn.nextcore.device.validation.HandlerValidateParams;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService implements IGroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    @Transactional
    public GroupResponse createGroupDevice(GroupRequest req) {
        try {
            return handleInfoGroup(req, new Group());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.GROUP_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.GROUP_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public GroupResponse updateGroupDevice(String id, GroupRequest req) {
        if (!groupRepository.existsById(Long.valueOf(id))) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage());
        }
        try {
            Group groupExists = groupRepository.findGroupById(Long.valueOf(id));
            return handleInfoGroup(req, groupExists);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER002.getCode(), ErrorCodeEnum.ER002.getMessage());
        }
    }

    private GroupResponse handleInfoGroup(GroupRequest req, Group group) {
        group.setName(req.getName());
        group.setQuantity(0);
        groupRepository.save(group);

        return ParseUtils.convertGroupToGroupResponse(group);
    }

    @Override
    public GroupResponse findOneGroup(String id) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            GroupResponse result = new GroupResponse();
            Group group = groupRepository.findGroupById(Long.valueOf(id));
            if (group != null) {
                Group groupExists = groupRepository.findGroupById(Long.valueOf(id));
                result.setId(groupExists.getId());
                result.setName(groupExists.getName());
                result.setQuantity(groupExists.getQuantity());
            } else {
                throw new HandlerException(ErrorCodeEnum.ER151.getCode(), ErrorCodeEnum.ER151.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return result;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.GROUP_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.GROUP_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteGroupById(String id) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER149);
            Group group = groupRepository.findGroupById(Long.valueOf(id));
            if (group == null) {
                throw new HandlerException(ErrorCodeEnum.ER151.getCode(), ErrorCodeEnum.ER151.getMessage(), HttpStatus.BAD_REQUEST);
            }
            if (deviceRepository.existsDeviceByGroupIdAndDeletedAtIsNull(Long.parseLong(id))) {
                throw new HandlerException(ErrorCodeEnum.ER147.getCode(), ErrorCodeEnum.ER147.getMessage(), PathEnum.PROVIDER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            groupRepository.deleteById(Long.valueOf(id));
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.GROUP_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.GROUP_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<GroupResponse> getAllGroup(Boolean isSelectList) {
        List<GroupResponse> results = new ArrayList<>();
        try {
            List<Group> groupList = groupRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            for (Group group : groupList) {
                GroupResponse item = new GroupResponse();
                item.setId(group.getId());
                item.setName(group.getName());
                item.setQuantity(group.getQuantity());
                if (!group.getDevices().isEmpty() && isSelectList == false) {
                    for (Device device : group.getDevices()) {
                        DeviceResponse deviceResponse = ParseUtils.convertDeviceToDeviceRes(device, "list");
                        item.getDevices().add(deviceResponse);
                    }
                }

                results.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.GROUP_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (results.isEmpty())
            throw new HandlerException(ErrorCodeEnum.ER044.getCode(), ErrorCodeEnum.ER044.getMessage(), PathEnum.GROUP_PATH.getPath(), HttpStatus.BAD_REQUEST);

        return results;
    }
}
