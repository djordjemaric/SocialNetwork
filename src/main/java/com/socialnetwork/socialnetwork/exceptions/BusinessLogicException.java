package com.socialnetwork.socialnetwork.exceptions;

import com.socialnetwork.socialnetwork.enums.ErrorCode;

public class BusinessLogicException extends Exception{
    private final ErrorCode errorCode;

    public BusinessLogicException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
