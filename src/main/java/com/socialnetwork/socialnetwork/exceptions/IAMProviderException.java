package com.socialnetwork.socialnetwork.exceptions;

public class IAMProviderException extends SocialNetworkException{

    public IAMProviderException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
