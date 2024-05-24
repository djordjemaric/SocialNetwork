package com.socialnetwork.socialnetwork;

import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.service.GroupService;
import com.socialnetwork.socialnetwork.service.JwtService;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTests {

    @Mock
    private JwtService jwtService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @InjectMocks
    private GroupService groupService;

    private User admin;
    private User user;
    private Group group;
    private Group group2;
    private GroupMember groupMember;
    private CreateGroupDTO createGroupDTO;
    private GroupDTO expectedGroupDTO;
    private GroupDTO expectedGroupDTO2;

    @Captor
    private ArgumentCaptor<Group> groupCaptor;
    @Captor
    private ArgumentCaptor<GroupMember> groupMemberCaptor;
    @Captor
    private ArgumentCaptor<Integer> idCaptor;

    @BeforeEach
    void setUp() {
        admin = new User(1, "admin@admin.com", "");
        user = new User(2, "user@user.com", "");
        group = new Group(1, "Group1", admin, true, null);
        group2 = new Group(2, "Group2", admin, false, null);
        groupMember = new GroupMember(1, user, group);
        createGroupDTO = new CreateGroupDTO("Group1", true);
        expectedGroupDTO = new GroupDTO("Group1", "admin@admin.com", true, 1);
        expectedGroupDTO2 = new GroupDTO("Group2", "admin@admin.com", false, 2);
    }

    @Test
    void createGroup_groupNameExists_throwsException() {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsByName(createGroupDTO.name())).thenReturn(true);

        assertThrows(FunctionArgumentException.class, () -> groupService.createGroup(createGroupDTO),
                "Group with that name already exists");

        verify(jwtService).getUser();
        verify(groupRepository).existsByName(createGroupDTO.name());
    }

    @Test
    void findGroupByName_success() {
        String name = "Group";

        List<Group> groups = Arrays.asList(group, group2);

        when(groupRepository.findAllByNameStartingWith(name)).thenReturn(groups);

        List<GroupDTO> result = groupService.findByName(name);

        verify(groupRepository).findAllByNameStartingWith(name);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedGroupDTO, result.get(0));
        assertEquals(expectedGroupDTO2, result.get(1));
    }

    @Test
    void deleteGroup_success() {
        when(jwtService.getUser()).thenReturn(admin);

        doNothing().when(groupRepository).deleteById(group.getId());
        when(groupRepository.existsByIdAndAdminId(group.getId(),admin.getId())).thenReturn(true);

        // Verify that no exceptions are thrown
        assertDoesNotThrow(() -> groupService.deleteGroup(group.getId()));

        // Verify that deleteById method is called
        verify(groupRepository).deleteById(group.getId());

        // Verify that existsByIdAndAdminId method is called with the correct arguments
        verify(groupRepository).existsByIdAndAdminId(group.getId(), admin.getId());

        // Verify that getUser method is called
        verify(jwtService).getUser();
    }
    @Test
    void deleteGroup_throwsException() {
        when(jwtService.getUser()).thenReturn(admin);

        when(groupRepository.existsByIdAndAdminId(group.getId(),admin.getId())).thenReturn(false);

        assertThrows(FunctionArgumentException.class, () -> groupService.deleteGroup(group.getId()),
                "There is no group with given id or id of admin");


        // Verify that existsByIdAndAdminId method is called with the correct arguments
        verify(groupRepository).existsByIdAndAdminId(group.getId(), admin.getId());

        // Verify that getUser method is called
        verify(jwtService).getUser();
    }

    @Test
    void createRequestToJoinGroup_success() {
        when(jwtService.getUser()).thenReturn(admin);

        doNothing().when(groupRepository).deleteById(group.getId());
        when(groupRepository.existsByIdAndAdminId(group.getId(),admin.getId())).thenReturn(true);

        // Verify that no exceptions are thrown
        assertDoesNotThrow(() -> groupService.deleteGroup(group.getId()));

        // Verify that deleteById method is called
        verify(groupRepository).deleteById(group.getId());

        // Verify that existsByIdAndAdminId method is called with the correct arguments
        verify(groupRepository).existsByIdAndAdminId(group.getId(), admin.getId());

        // Verify that getUser method is called
        verify(jwtService).getUser();
    }

    @Test
    void createGroup_success() {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsByName(createGroupDTO.name())).thenReturn(false);
        when(groupMapper.dtoToEntity(admin, createGroupDTO)).thenReturn(group);
        when(groupRepository.save(groupCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(groupMemberRepository.save(groupMemberCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(groupMapper.entityToGroupDto(group)).thenReturn(expectedGroupDTO);

        GroupDTO result = groupService.createGroup(createGroupDTO);

        assertEquals(expectedGroupDTO, result);

        verify(jwtService).getUser();
        verify(groupRepository).existsByName(createGroupDTO.name());
        verify(groupMapper).dtoToEntity(admin, createGroupDTO);
        verify(groupRepository).save(groupCaptor.getValue());
        verify(groupMemberRepository).save(groupMemberCaptor.getValue());
        verify(groupMapper).entityToGroupDto(group);

        assertEquals(group, groupCaptor.getValue());
        assertEquals(admin, groupMemberCaptor.getValue().getMember());
        assertEquals(group, groupMemberCaptor.getValue().getGroup());

    }

    @Test
    void leaveGroup_asAdmin_throwsException() {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        assertThrows(FunctionArgumentException.class, () -> groupService.leaveGroup(group.getId()),
                "Admin can't leave the group");

        verify(jwtService).getUser();
        verify(groupRepository).findById(group.getId());
    }

    @Test
    void leaveGroup_notMember_throwsException() {
        when(jwtService.getUser()).thenReturn(user);
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.findByMember(user)).thenReturn(Optional.empty());

        assertThrows(FunctionArgumentException.class, () -> groupService.leaveGroup(group.getId()),
                "User is not member of group");

        verify(jwtService).getUser();
        verify(groupRepository).findById(group.getId());
        verify(groupMemberRepository).findByMember(user);
    }

    @Test
    void leaveGroup_success() {
        when(jwtService.getUser()).thenReturn(user);
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.findByMember(user)).thenReturn(Optional.of(groupMember));

        groupService.leaveGroup(group.getId());

        verify(jwtService).getUser();
        verify(groupRepository).findById(idCaptor.capture());
        assertEquals(group.getId(), idCaptor.getValue());
        verify(groupMemberRepository).findByMember(user);
        verify(groupMemberRepository).delete(groupMember);
    }

    @Test
    void removeMember_groupNotExists_throwsException() {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsByAdminIdAndGroupId(admin.getId(), group.getId())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> groupService.removeMember(group.getId(), user.getId()),
                "Group with that admin and group id does not exist.");

        verify(jwtService).getUser();
        verify(groupRepository).existsByAdminIdAndGroupId(idCaptor.capture(), idCaptor.capture());
        assertEquals(admin.getId(), idCaptor.getAllValues().get(0));
        assertEquals(group.getId(), idCaptor.getAllValues().get(1));
    }

    @Test
    void removeMember_adminSelfRemoval_throwsException() {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsByAdminIdAndGroupId(admin.getId(), group.getId())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> groupService.removeMember(group.getId(), admin.getId()),
                "Admin can not remove himself from the group!");

        verify(jwtService).getUser();
        verify(groupRepository).existsByAdminIdAndGroupId(admin.getId(), group.getId());
    }

    @Test
    void removeMember_userNotInGroup_throwsException() {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsByAdminIdAndGroupId(admin.getId(), group.getId())).thenReturn(true);
        when(groupMemberRepository.existsByUserIdAndGroupId(user.getId(), group.getId())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> groupService.removeMember(group.getId(), user.getId()),
                "User with that id is not in the group.");

        verify(jwtService).getUser();
        verify(groupRepository).existsByAdminIdAndGroupId(admin.getId(), group.getId());
        verify(groupMemberRepository).existsByUserIdAndGroupId(idCaptor.capture(), idCaptor.capture());
        assertEquals(user.getId(), idCaptor.getAllValues().get(0));
        assertEquals(group.getId(), idCaptor.getAllValues().get(1));
    }

    @Test
    void removeMember_success() {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsByAdminIdAndGroupId(admin.getId(), group.getId())).thenReturn(true);
        when(groupMemberRepository.existsByUserIdAndGroupId(user.getId(), group.getId())).thenReturn(true);

        groupService.removeMember(group.getId(), user.getId());

        verify(jwtService).getUser();
        verify(groupRepository).existsByAdminIdAndGroupId(admin.getId(), group.getId());
        verify(groupMemberRepository).existsByUserIdAndGroupId(user.getId(), group.getId());
        verify(groupMemberRepository).deleteGroupMemberByGroupIdAndMemberId(idCaptor.capture(), idCaptor.capture());
        assertEquals(group.getId(), idCaptor.getAllValues().get(0));
        assertEquals(user.getId(), idCaptor.getAllValues().get(1));
    }

}