package com.socialnetwork.socialnetwork;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.socialnetwork.socialnetwork.controller.GroupController;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestDTO;
import com.socialnetwork.socialnetwork.dto.group.ResolvedGroupRequestStatus;
import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.service.GroupService;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private GroupService groupService;
    @InjectMocks
    private GroupController groupController;

    private CreateGroupDTO createGroupDTO;
    private ResolvedGroupRequestDTO resolvedGroupRequestDTO;
    private GroupDTO expectedGroupDTO;
    private List<GroupDTO> expectedGroupDTOS;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
        createGroupDTO = new CreateGroupDTO("Group1", true);
        expectedGroupDTO = new GroupDTO("Group1", "admin@admin.com", true, 1);
        expectedGroupDTOS = Arrays.asList(new GroupDTO("Group1", "admin1@admin.com", true, 1), new GroupDTO("Group2", "admin2@admin.com", true, 2));
        PreviewUserDTO previewUserDTO = new PreviewUserDTO(1, "user@example.com");
        resolvedGroupRequestDTO = new ResolvedGroupRequestDTO(1, previewUserDTO, expectedGroupDTO, ResolvedGroupRequestStatus.REQUEST_TO_JOIN_GROUP_ACCEPTED);

    }

    @Test
    void createGroup_success() throws Exception {
        when(groupService.createGroup(createGroupDTO)).thenReturn(expectedGroupDTO);

        String content = objectWriter.writeValueAsString(createGroupDTO);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/groups").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(content);

        mockMvc.perform(mockRequest).andExpect(status().isCreated()).andExpect(result -> {
            String responseBody = result.getResponse().getContentAsString();
            GroupDTO actualGroupDTO = objectMapper.readValue(responseBody, GroupDTO.class);
            assertEquals(expectedGroupDTO, actualGroupDTO);
        });

    }

    @Test
    void leaveGroup_success() throws Exception {

        Integer idGroup = 1;

        doNothing().when(groupService).leaveGroup(idGroup);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/groups/{idGroup}/leave", idGroup).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        verify(groupService).leaveGroup(idGroup);

    }

    @Test
    void removeFromGroup_success() throws Exception {

        Integer idGroup = 1;
        Integer idUser = 3;

        doNothing().when(groupService).removeMember(idGroup, idUser);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/groups/{idGroup}/members/{idUser}", idGroup, idUser).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        verify(groupService).removeMember(idGroup, idUser);

    }

    @Test
    void findGroupsByName_success() throws Exception {
        String groupName = "Group";

        when(groupService.findByName(groupName)).thenReturn(expectedGroupDTOS);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/api/groups").param("name", groupName).accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest).andExpect(status().isOk()).andExpect(result -> {
            String responseBody = result.getResponse().getContentAsString();
            List<GroupDTO> actualGroupDTOS = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            assertEquals(expectedGroupDTOS, actualGroupDTOS);
        });
    }

    @Test
    void deleteGroup_success() throws Exception {
        Integer idGroup = 1;

        doNothing().when(groupService).deleteGroup(idGroup);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/api/groups/{idGroup}", idGroup);

        mockMvc.perform(mockRequest).andExpect(status().isOk());

        verify(groupService).deleteGroup(idGroup);
    }

    @Test
    void createRequestToJoinGroup_success() throws Exception {
        Integer id = 1;
        when(groupService.createRequestToJoinGroup(id)).thenReturn(resolvedGroupRequestDTO);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/groups/{id}/join", id).accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest).andExpect(status().isCreated()).andExpect(result -> {
            String responseBody = result.getResponse().getContentAsString();
            ResolvedGroupRequestDTO actualResolvedGroupRequestDTO = objectMapper.readValue(responseBody, ResolvedGroupRequestDTO.class);
            assertEquals(resolvedGroupRequestDTO, actualResolvedGroupRequestDTO);
        });
    }

}
