package com.socialnetwork.socialnetwork.exceptions;

public class ResourceNotFoundException extends SocialNetworkException{

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
