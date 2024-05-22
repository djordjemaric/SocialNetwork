package com.socialnetwork.socialnetwork;

import com.socialnetwork.socialnetwork.dto.GroupDto;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDto;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    private CreateGroupDto createGroupDto;
    private GroupDto expectedGroupDto;

    @BeforeEach
    void setUp() {
        admin = new User(1, "admin@admin.com", "");
        user = new User(2, "user@user.com", "");
        group = new Group(1, "Group1", admin, true, null);
        groupMember = new GroupMember(1, user, group);
        createGroupDto = new CreateGroupDto("Group1", true);
        expectedGroupDto = new GroupDto("Group1", "admin@admin.com", true, 1);
    }

    @Test
    void createGroup_groupNameExists_throwsException() {
        Mockito.when(jwtService.getUser()).thenReturn(admin);
        Mockito.when(groupRepository.existsByName(createGroupDto.name())).thenReturn(true);

        assertThrows(FunctionArgumentException.class, () -> groupService.createGroup(createGroupDto),
                "Group with that name already exists");
    }

    @Test
    void createGroup_success() {
        Mockito.when(jwtService.getUser()).thenReturn(admin);
        Mockito.when(groupRepository.existsByName(createGroupDto.name())).thenReturn(false);
        Mockito.when(groupMapper.dtoToEntity(admin, createGroupDto)).thenReturn(group);
        Mockito.when(groupRepository.save(Mockito.any(Group.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(groupMemberRepository.save(Mockito.any(GroupMember.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(groupMapper.entityToGroupDto(group)).thenReturn(expectedGroupDto);

        GroupDto result = groupService.createGroup(createGroupDto);

        assertEquals(expectedGroupDto, result);

    }

    @Test
    void leaveGroup_asAdmin_throwsException() {
        Mockito.when(jwtService.getUser()).thenReturn(admin);
        Mockito.when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        assertThrows(FunctionArgumentException.class, () -> groupService.leaveGroup(group.getId()), "Admin can't leave the group");
    }

    @Test
    void leaveGroup_notMember_throwsException() {
        Mockito.when(jwtService.getUser()).thenReturn(user);
        Mockito.when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        Mockito.when(groupMemberRepository.findByMember(user)).thenReturn(Optional.empty());

        assertThrows(FunctionArgumentException.class, () -> groupService.leaveGroup(group.getId()), "User is not member of group");
    }

    @Test
    void leaveGroup_success() {
        Mockito.when(jwtService.getUser()).thenReturn(user);
        Mockito.when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        Mockito.when(groupMemberRepository.findByMember(user)).thenReturn(Optional.of(groupMember));

        groupService.leaveGroup(group.getId());

        Mockito.verify(groupMemberRepository).delete(groupMember);
    }

    @Test
    void removeMember_groupNotExists_throwsException() {
        Mockito.when(jwtService.getUser()).thenReturn(admin);
        Mockito.when(groupRepository.existsByAdminIdAndGroupId(admin.getId(), group.getId())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> groupService.removeMember(group.getId(), user.getId()));
    }

    @Test
    void removeMember_adminSelfRemoval_throwsException() {
        Mockito.when(jwtService.getUser()).thenReturn(admin);
        Mockito.when(groupRepository.existsByAdminIdAndGroupId(admin.getId(), group.getId())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> groupService.removeMember(group.getId(), admin.getId()));
    }

    @Test
    void removeMember_userNotInGroup_throwsException() {
        Mockito.when(jwtService.getUser()).thenReturn(admin);
        Mockito.when(groupRepository.existsByAdminIdAndGroupId(admin.getId(), group.getId())).thenReturn(true);
        Mockito.when(groupMemberRepository.existsByUserIdAndGroupId(user.getId(), group.getId())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> groupService.removeMember(group.getId(), user.getId()));
    }

    @Test
    void removeMember_success() {
        Mockito.when(jwtService.getUser()).thenReturn(admin);
        Mockito.when(groupRepository.existsByAdminIdAndGroupId(admin.getId(), group.getId())).thenReturn(true);
        Mockito.when(groupMemberRepository.existsByUserIdAndGroupId(user.getId(), group.getId())).thenReturn(true);

        groupService.removeMember(group.getId(), user.getId());

        Mockito.verify(groupMemberRepository).deleteGroupMemberByGroupIdAndMemberId(group.getId(), user.getId());
    }
}