package com.socialnetwork.socialnetwork.dto.group;

import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;

public record GroupRequestDTO(PreviewUserDTO userDTO, GroupDTO groupDTO, Integer idRequest) {
}
