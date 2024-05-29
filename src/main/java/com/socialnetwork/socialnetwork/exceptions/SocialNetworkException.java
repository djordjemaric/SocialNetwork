package com.socialnetwork.socialnetwork.exceptions;

public class SocialNetworkException extends Exception{
    private final ErrorCode errorCode;

    public SocialNetworkException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
