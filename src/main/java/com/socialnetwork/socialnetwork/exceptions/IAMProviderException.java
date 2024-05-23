package com.socialnetwork.socialnetwork.exceptions;

import com.socialnetwork.socialnetwork.enums.ErrorCode;

public class IAMProviderException extends Exception{
    private final ErrorCode errorCode;

    public IAMProviderException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
