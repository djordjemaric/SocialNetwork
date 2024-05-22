package com.socialnetwork.socialnetwork.dto.group;

import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.User;

public record RequestDTO(User user,
                         Group group) {
}
