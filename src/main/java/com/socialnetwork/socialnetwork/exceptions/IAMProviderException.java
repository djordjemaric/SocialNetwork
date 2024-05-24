package com.socialnetwork.socialnetwork.exceptions;

public class IAMProviderException extends RuntimeException{
    private final ErrorCode errorCode;

    public IAMProviderException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
