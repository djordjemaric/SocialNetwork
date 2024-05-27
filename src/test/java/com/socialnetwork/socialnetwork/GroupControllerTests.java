package com.socialnetwork.socialnetwork;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.socialnetwork.socialnetwork.controller.GroupController;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestStatus;
import com.socialnetwork.socialnetwork.dto.post.PostDTO;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.Comment;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.Post;
import com.socialnetwork.socialnetwork.entity.User;
import com.socialnetwork.socialnetwork.service.GroupService;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GroupControllerTests {

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private CreateGroupDTO createGroupDTO;
    private ResolvedGroupRequestDTO resolvedGroupRequestDTO;
    private PreviewUserDTO previewUserDTO;
    private GroupDTO expectedGroupDTO;
    private List<GroupDTO> expectedGroupDTOS;
    private List<PostDTO> expectedPostDTOS;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
        createGroupDTO = new CreateGroupDTO("Group1", true);
        expectedGroupDTO = new GroupDTO("Group1", "admin@admin.com", true, 1);
        expectedGroupDTOS = Arrays.asList(
                new GroupDTO("Group1", "admin1@admin.com", true, 1),
                new GroupDTO("Group2", "admin2@admin.com", true, 2)
        );
        // Create mock Users
        User user1 = new User(1, "user1@example.com", "user1Sub");
        User user2 = new User(2, "user2@example.com", "user2Sub");

        // Create mock Group
        Group group = new Group(1, "Group1", user1, true, null);
        // Create mock Comments
        Comment comment1 = new Comment(1, "Comment text 1", LocalDateTime.now(), user1, Collections.emptyList(), null);
        Comment comment2 = new Comment(2, "Comment text 2", LocalDateTime.now(), user2, Collections.emptyList(), null);

        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);

        // Create mock Posts
        Post post1 = new Post(1, true, "Post text 1", "imgUrl1", user1, group, comments);
        Post post2 = new Post(2, true, "Post text 2", "imgUrl2", user2, group, comments);


        // Set posts for group
        group.setPosts(Arrays.asList(post1, post2));

        // Create PostDTO objects
        expectedPostDTOS = Arrays.asList(
                new PostDTO(1, "Post text 1", "imgUrl1", "user1@example.com", "Group1", List.of(comment1)),
                new PostDTO(2, "Post text 2", "imgUrl2", "user2@example.com", "Group1", List.of(comment2))
        );
        previewUserDTO = new PreviewUserDTO(1, "user@example.com");
        resolvedGroupRequestDTO = new ResolvedGroupRequestDTO(1, previewUserDTO, expectedGroupDTO, ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_ACCEPTED);

    }

    @Test
    void createGroup_success() throws Exception {

        when(groupService.createGroup(createGroupDTO)).thenReturn(expectedGroupDTO);

        String content = objectWriter.writeValueAsString(createGroupDTO);


        //pravimo request
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);


        //izvrsavanje requesta
        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    GroupDTO actualGroupDTO = objectMapper.readValue(responseBody, GroupDTO.class);
                    assertEquals(expectedGroupDTO, actualGroupDTO);
                });

    }

    @Test
    void leaveGroup_success() throws Exception {

        Integer idGroup = 1;

        doNothing().when(groupService).leaveGroup(idGroup);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/groups/{idGroup}/leave", idGroup)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(groupService).leaveGroup(idGroup);

    }

    @Test
    void removeFromGroup_success() throws Exception {

        Integer idGroup = 1;
        Integer idUser = 3;

        doNothing().when(groupService).removeMember(idGroup, idUser);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/groups/{idGroup}/members/{idUser}",
                        idGroup, idUser).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(groupService).removeMember(idGroup, idUser);

    }



    @Test
    void findGroupsByName_success() throws Exception {
        String groupName = "Group";

        when(groupService.findByName(groupName)).thenReturn(expectedGroupDTOS);

        //pravimo request
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/api/groups")
                .param("name", groupName)
                .accept(MediaType.APPLICATION_JSON);


        //izvrsavanje requesta
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    List<GroupDTO> actualGroupDTOS = objectMapper.readValue(responseBody, new TypeReference<>() {
                    });
                    assertEquals(expectedGroupDTOS, actualGroupDTOS);
                });
    }

    @Test
    void findGroupsByName_throwsException() throws Exception {
        String groupName = "Test Group";
        // Mock the method to throw the exception
        doThrow(new FunctionArgumentException("Group search failed"))
                .when(groupService).findByName(anyString());

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/api/groups")
                .param("name", groupName)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertEquals("Group search failed", objectMapper.readTree(responseBody).get("message").asText());
                });
    }

    @Test
    void deleteGroup_success() throws Exception {
        Integer idGroup = 1;

        doNothing().when(groupService).deleteGroup(idGroup);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/api/groups/{idGroup}", idGroup);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

        verify(groupService).deleteGroup(idGroup);
    }


    @Test
    void deleteGroup_throwsException() throws Exception {//ne radi
        Integer idGroup = 1;

        // Throw an exception when deleteGroup is called with idGroup
        doThrow(new FunctionArgumentException("Invalid group ID")).when(groupService).deleteGroup(idGroup);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete("/api/groups/{idGroup}", idGroup)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest()) // Expecting a 400 Bad Request status
                .andExpect(jsonPath("$.message").value("Invalid group ID")); // Verify the error message

        verify(groupService).deleteGroup(idGroup);
    }



    @Test
    void createRequestToJoinGroup_success() throws Exception {
        Integer id = 1;
        when(groupService.createRequestToJoinGroup(id)).thenReturn(resolvedGroupRequestDTO);

        //pravimo request
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/groups/{id}/join", id)
                .accept(MediaType.APPLICATION_JSON);


        //izvrsavanje requesta
        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    ResolvedGroupRequestDTO actualResolvedGroupRequestDTO = objectMapper.readValue(responseBody, ResolvedGroupRequestDTO.class);
                    assertEquals(resolvedGroupRequestDTO, actualResolvedGroupRequestDTO);
                });
    }

    @Test
    void getPostsByGroupId_success() throws Exception {
        Integer id = 1;
        when(groupService.getAllPostsByGroupId(id)).thenReturn(expectedPostDTOS);

        //pravimo request
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/api/groups/{id}/posts", id)
                .accept(MediaType.APPLICATION_JSON);


        //izvrsavanje requesta
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    List<PostDTO> actualPostDTOS = objectMapper.readValue(responseBody, new TypeReference<>() {
                    });
                    assertEquals(expectedPostDTOS, actualPostDTOS);
                });
    }



}
