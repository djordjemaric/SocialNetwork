package com.socialnetwork.socialnetwork.dto.post;

public record CreateReplyDTO(String text,
                               Integer idComm,
                               Integer idCommOwner) {}