package com.socialnetwork.socialnetwork.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    private final ErrorCode errorCode;

    public ResourceNotFoundException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
