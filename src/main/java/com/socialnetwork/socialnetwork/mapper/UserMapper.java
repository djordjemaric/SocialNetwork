package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public PreviewUserDTO requestToPreviewUserDTO(User user) {
        return new PreviewUserDTO(user.getId(), user.getEmail());
    }
}
