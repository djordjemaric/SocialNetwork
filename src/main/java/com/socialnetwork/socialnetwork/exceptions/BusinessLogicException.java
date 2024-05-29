package com.socialnetwork.socialnetwork.exceptions;

public class BusinessLogicException extends SocialNetworkException{

    public BusinessLogicException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
