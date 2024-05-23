package com.socialnetwork.socialnetwork.mapper;

import com.socialnetwork.socialnetwork.dto.user.PreviewUserDTO;
import com.socialnetwork.socialnetwork.entity.GroupRequest;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public PreviewUserDTO requestToPreviewUserDTO(GroupRequest request) {
        return new PreviewUserDTO(request.getUser().getId(), request.getUser().getEmail());
    }
}
