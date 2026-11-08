package com.example.moneyflowapi.telegram.exception;

import com.example.moneyflowapi.telegram.message.BotText;

public class BotTransactionException extends RuntimeException{
    private final transient BotText text;
    private final transient Object[] args;

    public BotTransactionException(BotText text, Object... args) {
        super(text.name());
        this.text = text;
        this.args = args;
    }

    public BotText text() {
        return text;
    }

    public Object[] args() {
        return args;
    }
}
