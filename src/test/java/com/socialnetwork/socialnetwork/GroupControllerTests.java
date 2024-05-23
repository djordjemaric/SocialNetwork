package com.socialnetwork.socialnetwork;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.socialnetwork.socialnetwork.controller.GroupController;
import com.socialnetwork.socialnetwork.dto.group.GroupDTO;
import com.socialnetwork.socialnetwork.dto.group.CreateGroupDTO;
import com.socialnetwork.socialnetwork.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
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
    private GroupDTO expectedGroupDTO;

    @BeforeEach
    public void setUp(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
        createGroupDTO = new CreateGroupDTO("Group1", true);
        expectedGroupDTO = new GroupDTO("Group1", "admin@admin.com", true, 1);
    }

    @Test
    void createGroup_success() throws Exception {

        Mockito.when(groupService.createGroup(createGroupDTO)).thenReturn(expectedGroupDTO);

        String content = objectWriter.writeValueAsString(createGroupDTO);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

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

        doNothing().when(groupService).leaveGroup(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/groups/{idGroup}/leave", idGroup)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(groupService).leaveGroup(1);
        verify(groupService, times(1)).leaveGroup(idGroup);

    }

    @Test
    void removeFromGroup_success() throws Exception {

        Integer idGroup = 1;
        Integer idUser = 3;

        doNothing().when(groupService).removeMember(1, 3);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/groups/{idGroup}/members/{idUser}",
                                idGroup, idUser).contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());

        verify(groupService).removeMember(1, 3);
        verify(groupService, times(1)).removeMember(idGroup, idUser);

    }

}
