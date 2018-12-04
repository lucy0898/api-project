package com.platform.enums;

import java.io.Serializable;

public enum RefundEnum {

    NOREFUND(900,"未退款"),
    REFUNDING(901,"退款中"),
    REFUND(902,"已退款");

    private int status;
    private String name;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private RefundEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }
}
