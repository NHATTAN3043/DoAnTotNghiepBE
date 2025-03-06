package vn.nextcore.device.service.group;

import vn.nextcore.device.dto.req.GroupRequest;
import vn.nextcore.device.dto.resp.GroupResponse;

import java.util.List;

public interface IGroupService {
    GroupResponse createGroupDevice(GroupRequest req);

    GroupResponse updateGroupDevice(String id, GroupRequest req);

    GroupResponse findOneGroup(String id);

    void deleteGroupById(String id);

    List<GroupResponse> getAllGroup();
}
