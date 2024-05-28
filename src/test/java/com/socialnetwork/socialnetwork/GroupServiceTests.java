package com.socialnetwork.socialnetwork;

import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestStatus;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.GroupMember;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.exceptions.BusinessLogicException;
import com.socialnetwork.socialnetwork.exceptions.ResourceNotFoundException;
import com.socialnetwork.socialnetwork.mapper.GroupMapper;
import com.socialnetwork.socialnetwork.mapper.PostMapper;
import com.socialnetwork.socialnetwork.repository.GroupMemberRepository;
import com.socialnetwork.socialnetwork.repository.GroupRepository;
import com.socialnetwork.socialnetwork.repository.GroupRequestRepository;
import com.socialnetwork.socialnetwork.repository.PostRepository;
import com.socialnetwork.socialnetwork.service.GroupService;
import com.socialnetwork.socialnetwork.service.JwtService;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTests {

    @Mock
    private JwtService jwtService;

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupRequestRepository groupRequestRepository;

    @Mock
    private GroupMapper groupMapper;
    @Mock
    private PostMapper postMapper;

    @Mock
    private GroupMemberRepository groupMemberRepository;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private GroupService groupService;

    private User admin;
    private User user;
    private Group group;
    private Post post;
    private Post post2;
    private List<Post> posts;
    private Group group2;
    private GroupMember groupMember;
    private CreateGroupDTO createGroupDTO;
    private GroupDTO expectedGroupDTO;
    private PostDTO expectedPostDTO;
    private GroupDTO expectedGroupDTO2;

    @Captor
    private ArgumentCaptor<Group> groupCaptor;
    @Captor
    private ArgumentCaptor<List<Post>> postsCaptor;
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
        post = new Post(1,true,"NEKI TEXT","LALAL",admin,group,null);
        post2 = new Post(2,false,"NEKI TEXT2","LALAL2",admin,group,null);
        posts = List.of(post,post2);
        groupMember = new GroupMember(1, user, group);
        createGroupDTO = new CreateGroupDTO("Group1", true);
        expectedGroupDTO = new GroupDTO("Group1", "admin@admin.com", true, 1);
        expectedGroupDTO2 = new GroupDTO("Group2", "admin@admin.com", false, 2);
    }

    @Test
    void createGroup_groupNameExists_throwsException() throws ResourceNotFoundException{
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsByName(createGroupDTO.name())).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> groupService.createGroup(createGroupDTO),
                "Group with that name already exists");

        verify(jwtService).getUser();
        verify(groupRepository).existsByName(createGroupDTO.name());
    }

    @Test
    void createGroup_success() throws BusinessLogicException, ResourceNotFoundException {
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

        assertThrows(BusinessLogicException.class, () -> groupService.leaveGroup(group.getId()),
                "Admin can't leave the group");

        verify(jwtService).getUser();
        verify(groupRepository).findById(group.getId());
    }

    @Test
    void leaveGroup_notMember_throwsException() throws ResourceNotFoundException{
        when(jwtService.getUser()).thenReturn(user);
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupMemberRepository.findByMemberAndGroup(user, group)).thenReturn(Optional.empty());

        assertThrows(BusinessLogicException.class, () -> groupService.leaveGroup(group.getId()),
                "User is not member of group");

        verify(jwtService).getUser();
        verify(groupRepository).findById(group.getId());
        verify(groupMemberRepository).findByMemberAndGroup(user, group);
    }

    @Test
    void leaveGroup_success() throws BusinessLogicException, ResourceNotFoundException {
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

        assertThrows(AccessDeniedException.class, () -> groupService.removeMember(group.getId(), user.getId()),
                "You are not an admin of that group");

        verify(jwtService).getUser();
        verify(groupRepository).existsByAdminIdAndGroupId(idCaptor.capture(), idCaptor.capture());
        assertEquals(admin.getId(), idCaptor.getAllValues().get(0));
        assertEquals(group.getId(), idCaptor.getAllValues().get(1));
    }

    @Test
    void removeMember_adminSelfRemoval_throwsException() throws ResourceNotFoundException {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsByAdminIdAndGroupId(admin.getId(), group.getId())).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> groupService.removeMember(group.getId(), admin.getId()),
                "Admin can not remove himself from the group!");

        verify(jwtService).getUser();
        verify(groupRepository).existsByAdminIdAndGroupId(admin.getId(), group.getId());
    }

    @Test
    void removeMember_userNotInGroup_throwsException() throws ResourceNotFoundException {
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsByAdminIdAndGroupId(admin.getId(), group.getId())).thenReturn(true);
        when(groupMemberRepository.existsByUserIdAndGroupId(user.getId(), group.getId())).thenReturn(false);

        assertThrows(BusinessLogicException.class, () -> groupService.removeMember(group.getId(), user.getId()),
                "User with that id is not in the group.");

        verify(jwtService).getUser();
        verify(groupRepository).existsByAdminIdAndGroupId(admin.getId(), group.getId());
        verify(groupMemberRepository).existsByUserIdAndGroupId(idCaptor.capture(), idCaptor.capture());
        assertEquals(user.getId(), idCaptor.getAllValues().get(0));
        assertEquals(group.getId(), idCaptor.getAllValues().get(1));
    }

    @Test
    void removeMember_success() throws BusinessLogicException, ResourceNotFoundException {
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



    @Test
    void findGroupByName_success() { //radii
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
    void getAllPostsByGroupId_success() throws ResourceNotFoundException, BusinessLogicException {//radii
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsById(group.getId())).thenReturn(true);
        when(groupMemberRepository.existsByUserIdAndGroupId(admin.getId(), group.getId())).thenReturn(true);
        when(postRepository.findAllByGroup_Id(group.getId())).thenReturn(posts);

        PostDTO expectedPostDTO1 = new PostDTO(post.getId(), post.getText(), post.getImgS3Key(), post.getOwner().getEmail(), post.getGroup().getName(), null);
        PostDTO expectedPostDTO2 = new PostDTO(post2.getId(), post2.getText(), post2.getImgS3Key(), post2.getOwner().getEmail(), post2.getGroup().getName(), null);

        when(postMapper.postToPostDTO(post)).thenReturn(expectedPostDTO1);
        when(postMapper.postToPostDTO(post2)).thenReturn(expectedPostDTO2);

        List<PostDTO> expectedPostDTOS = List.of(expectedPostDTO1, expectedPostDTO2);

        List<PostDTO> postDTOS = groupService.getAllPostsByGroupId(group.getId());

        verify(jwtService).getUser();
        verify(groupRepository).existsById(group.getId());
        verify(groupMemberRepository).existsByUserIdAndGroupId(admin.getId(), group.getId());
        verify(postRepository).findAllByGroup_Id(group.getId());

        assertEquals(expectedPostDTOS, postDTOS);
    }

    @Test
    void getAllPostsByGroupId_noGroup_throwsException() throws ResourceNotFoundException { //radii
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsById(group.getId())).thenReturn(false);

        FunctionArgumentException exception = assertThrows(FunctionArgumentException.class, () -> {
            groupService.getAllPostsByGroupId(group.getId());
        });

        assertEquals("Group with that id does not exists", exception.getMessage());

        verify(jwtService).getUser();
        verify(groupRepository).existsById(group.getId());
    }

    @Test
    void getAllPostsByGroupId_notMember_throwsException() throws ResourceNotFoundException {//radii
        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.existsById(group.getId())).thenReturn(true);
        when(groupMemberRepository.existsByUserIdAndGroupId(admin.getId(), group.getId())).thenReturn(false);

        FunctionArgumentException exception = assertThrows(FunctionArgumentException.class, () -> {
            groupService.getAllPostsByGroupId(group.getId());
        });

        assertEquals("User is not member of give group!", exception.getMessage());

        verify(jwtService).getUser();
        verify(groupRepository).existsById(group.getId());
        verify(groupMemberRepository).existsByUserIdAndGroupId(admin.getId(), group.getId());
    }

    @Test
    void deleteGroup_success() throws ResourceNotFoundException { //radii
        when(jwtService.getUser()).thenReturn(admin);

        when(groupRepository.existsByIdAndAdminId(group.getId(),admin.getId())).thenReturn(true);

        doNothing().when(groupRepository).deleteById(group.getId());

        assertDoesNotThrow(() -> groupService.deleteGroup(group.getId()));

        verify(groupRepository).deleteById(group.getId());

        verify(groupRepository).existsByIdAndAdminId(group.getId(), admin.getId());

        verify(jwtService).getUser();
    }
    @Test
    void deleteGroup_throwsException() throws ResourceNotFoundException { //radii
        when(jwtService.getUser()).thenReturn(admin);

        when(groupRepository.existsByIdAndAdminId(group.getId(),admin.getId())).thenReturn(false);

        assertThrows(FunctionArgumentException.class, () -> groupService.deleteGroup(group.getId()),
                "There is no group with given id or id of admin");


        verify(groupRepository).existsByIdAndAdminId(group.getId(), admin.getId());

        verify(jwtService).getUser();
    }


    @Test
    void createRequestToJoinGroup_success() throws ResourceNotFoundException, BusinessLogicException {//radii

        when(jwtService.getUser()).thenReturn(admin);
        when(groupRepository.findById(anyInt())).thenReturn(Optional.of(group));
        when(groupRequestRepository.existsByUserIdAndGroupId(anyInt(), anyInt())).thenReturn(false);
        when(groupMemberRepository.existsByUserIdAndGroupId(anyInt(), anyInt())).thenReturn(false);
        when(groupMemberRepository.save(groupMemberCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Invoke the method with a group ID (assuming 1 is the group ID)
        ResolvedGroupRequestDTO result = groupService.createRequestToJoinGroup(1);

        // Verify that the getUser method was called
        verify(jwtService).getUser();

        // Verify that the findById method was called with the correct argument
        verify(groupRepository).findById(1);

        // Verify that existsByUserIdAndGroupId was called with the correct arguments
        verify(groupRequestRepository).existsByUserIdAndGroupId(admin.getId(), 1);
        verify(groupMemberRepository).existsByUserIdAndGroupId(admin.getId(), 1);

        // Verify that save method was called on groupMemberRepository with the correct argument
        ArgumentCaptor<GroupMember> groupMemberCaptor = ArgumentCaptor.forClass(GroupMember.class);
        verify(groupMemberRepository).save(groupMemberCaptor.capture());
        GroupMember savedGroupMember = groupMemberCaptor.getValue();

        // Assert that the saved group member has the expected properties
        assertNotNull(savedGroupMember);
        assertEquals(admin.getId(), savedGroupMember.getMember().getId());
        assertEquals(group.getId(), savedGroupMember.getGroup().getId());

        // Add more assertions based on the behavior of the group (public or private)
        if (group.isPublic()) {
            // For a public group, verify the returned ResolvedGroupRequestDTO
            assertNotNull(result);
            assertEquals(group.getId(), result.group().idGroup());
            assertEquals(ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_ACCEPTED, result.status());
        } else {
            // For a private group, verify that a group request was created
            assertNotNull(result);
            assertEquals(admin.getId(), result.user().id());
            assertEquals(group.getId(), result.group().idGroup());
            assertEquals(ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_CREATED, result.status());
        }
    }




}