package com.socialnetwork.socialnetwork.dto.group;

import com.socialnetwork.socialnetwork.entity.Group;
import com.socialnetwork.socialnetwork.entity.User;

public record GroupRequest_MemberDTO(Integer id,
                                     User user,
                                     Group group,
                                     String title
) {
}
