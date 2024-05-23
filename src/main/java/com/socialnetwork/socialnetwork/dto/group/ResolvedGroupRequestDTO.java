package com.socialnetwork.socialnetwork.dto.group;

import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.User;

public record ResolvedGroupRequestDTO(Integer id, PreviewUserDTO user, GroupDTO group, ResolvedGroupRequestStatus title) {
}
