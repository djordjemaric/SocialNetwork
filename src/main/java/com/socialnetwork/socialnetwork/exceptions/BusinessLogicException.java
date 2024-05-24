package com.socialnetwork.socialnetwork.exceptions;

public class BusinessLogicException extends RuntimeException{
    private final ErrorCode errorCode;

    public BusinessLogicException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
