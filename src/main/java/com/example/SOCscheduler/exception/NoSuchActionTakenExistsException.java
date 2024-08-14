package com.example.SOCscheduler.exception;

public class NoSuchActionTakenExistsException extends RuntimeException {


    private String message;

    public NoSuchActionTakenExistsException() {
    }

    public NoSuchActionTakenExistsException(String msg) {
        super(msg);
        this.message = msg;
    }
}
