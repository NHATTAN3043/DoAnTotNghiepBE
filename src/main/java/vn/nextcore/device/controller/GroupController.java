package vn.nextcore.device.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import vn.nextcore.device.dto.req.GroupRequest;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.ErrorResponse;
import vn.nextcore.device.dto.resp.GroupResponse;
import vn.nextcore.device.service.group.IGroupService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group")
public class GroupController {
    @Autowired
    private IGroupService groupService;

    @Operation(summary = "Create Group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroupDevice(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = GroupRequest.class)))
            @RequestBody GroupRequest req) {
        return groupService.createGroupDevice(req);
    }

    @Operation(summary = "Update Group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GroupResponse updateGroup(
            @Parameter(description = "Id of group is number", required = true, example = "1")
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = GroupRequest.class)))
            @PathVariable(name = "id") String groupId,
            @RequestBody GroupRequest req) {
        return groupService.updateGroupDevice(groupId, req);
    }

    @Operation(summary = "List Group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DataResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<List<GroupResponse>> groupList() {
        List<GroupResponse> result = groupService.getAllGroup();
        return new DataResponse<>(result);
    }

    @Operation(summary = "Find Group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public GroupResponse getOneGroup(
            @Parameter(description = "Id of group is number", required = true, example = "1")
            @PathVariable(name = "id") String groupId) {
        return groupService.findOneGroup(groupId);
    }

    @Operation(summary = "Delete Group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGroup(
            @Parameter(description = "Id of group is number", required = true, example = "1")
            @PathVariable(name = "id") String groupId) {
        groupService.deleteGroupById(groupId);
    }
}
