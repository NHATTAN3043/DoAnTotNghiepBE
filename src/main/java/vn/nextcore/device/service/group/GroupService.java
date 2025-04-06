package vn.nextcore.device.service.group;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import vn.nextcore.device.dto.req.GroupRequest;
import vn.nextcore.device.dto.resp.GroupResponse;
import vn.nextcore.device.entity.Group;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.GroupRepository;
import vn.nextcore.device.enums.ErrorCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService implements IGroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Override
    public GroupResponse createGroupDevice(GroupRequest req) {
        try {
            return handleInfoGroup(req, new Group());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER001.getCode(), ErrorCodeEnum.ER001.getMessage());
        }
    }

    @Override
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
        GroupResponse result = new GroupResponse();

        group.setName(req.getName());
        group.setQuantity(0);
        groupRepository.save(group);

        result.setId(String.valueOf(group.getId()));
        result.setName(group.getName());
        result.setQuantity(group.getQuantity());

        return result;
    }

    @Override
    public GroupResponse findOneGroup(String id) {
        GroupResponse result = new GroupResponse();
        if (groupRepository.existsById(Long.valueOf(id))) {
            Group groupExists = groupRepository.findGroupById(Long.valueOf(id));
            result.setId(String.valueOf(groupExists.getId()));
            result.setName(groupExists.getName());
            result.setQuantity(groupExists.getQuantity());
        } else {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage());
        }
        return result;
    }

    @Override
    public void deleteGroupById(String id) {
        if (!groupRepository.existsById(Long.valueOf(id))) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage());
        }
        try {
            groupRepository.deleteById(Long.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER003.getCode(), ErrorCodeEnum.ER003.getMessage());
        }
    }

    @Override
    public List<GroupResponse> getAllGroup() {
        List<GroupResponse> results = new ArrayList<>();
        try {
            List<Group> groupList = groupRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            for (Group group : groupList) {
                GroupResponse item = new GroupResponse();
                item.setId(String.valueOf(group.getId()));
                item.setName(group.getName());
                item.setQuantity(group.getQuantity());

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
