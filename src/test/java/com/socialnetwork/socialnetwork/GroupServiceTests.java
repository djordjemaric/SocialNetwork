package com.socialnetwork.socialnetwork;

import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
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

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private GroupMember groupMember;
    private CreateGroupDTO createGroupDTO;
    private GroupDTO expectedGroupDTO;

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
        groupMember = new GroupMember(1, user, group);
        createGroupDTO = new CreateGroupDTO("Group1", true);
        expectedGroupDTO = new GroupDTO("Group1", "admin@admin.com", true, 1);
    }

    @Test
    void createGroup_groupNameExists_throwsException() throws ResourceNotFoundException {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsByName(createGroupDTO.name())).thenReturn(true);

        assertThrows(FunctionArgumentException.class, () -> groupService.createGroup(createGroupDTO),
                "Group with that name already exists");

        verify(jwtService).getUser();
        verify(groupRepository).existsByName(createGroupDTO.name());
    }

    @Test
    void createGroup_success() throws ResourceNotFoundException {
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
    void leaveGroup_asAdmin_throwsException() throws ResourceNotFoundException {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        assertThrows(FunctionArgumentException.class, () -> groupService.leaveGroup(group.getId()),
                "Admin can't leave the group");

        verify(jwtService).getUser();
        verify(groupRepository).findById(group.getId());
    }

    @Test
    void leaveGroup_notMember_throwsException() throws ResourceNotFoundException {
        when(jwtService.getUser()).thenReturn(user);
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.findByMemberAndGroup(user, group)).thenReturn(Optional.empty());

        assertThrows(FunctionArgumentException.class, () -> groupService.leaveGroup(group.getId()),
                "User is not member of group");

        verify(jwtService).getUser();
        verify(groupRepository).findById(group.getId());
        verify(groupMemberRepository).findByMemberAndGroup(user, group);
    }

    @Test
    void leaveGroup_success() throws ResourceNotFoundException {
        when(jwtService.getUser()).thenReturn(user);
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.findByMemberAndGroup(user, group)).thenReturn(Optional.of(groupMember));

        groupService.leaveGroup(group.getId());

        verify(jwtService).getUser();
        verify(groupRepository).findById(idCaptor.capture());
        assertEquals(group.getId(), idCaptor.getValue());
        verify(groupMemberRepository).findByMemberAndGroup(user, group);
        verify(groupMemberRepository).delete(groupMember);
    }

    @Test
    void removeMember_groupNotExists_throwsException() throws ResourceNotFoundException {
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
    void removeMember_adminSelfRemoval_throwsException() throws ResourceNotFoundException {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsByAdminIdAndGroupId(admin.getId(), group.getId())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> groupService.removeMember(group.getId(), admin.getId()),
                "Admin can not remove himself from the group!");

        verify(jwtService).getUser();
        verify(groupRepository).existsByAdminIdAndGroupId(admin.getId(), group.getId());
    }

    @Test
    void removeMember_userNotInGroup_throwsException() throws ResourceNotFoundException {
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
    void removeMember_success() throws ResourceNotFoundException {
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