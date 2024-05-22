package com.socialnetwork.socialnetwork.dto;

import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.User;

public record GroupRequest_MemberDto(Integer id,
                                     User user,
                                     Group group,
                                     String title
) {
}
