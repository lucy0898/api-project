package com.platform.enums;

/**
 * 支付
 * 付款状态 支付状态;0未付款;1付款中;2已付款;3 付款取消;4退款
 */
public enum PayEnum {
    UNPAID(0,"未付款"),
    INPAYMENT(1,"付款中"),
    PAID(2,"已付款"),
    CANCELPAY(3,"取消付款"),
    REFUED(4,"退款");

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

    private PayEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }

}
