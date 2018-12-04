package com.platform.enums;

/**
 *  发货状态
 *  商品配送情况;0未发货,1已发货,2已收货,4退货
 */
public enum ShippingEnum {
    UNSHIPPED(0,"未发货"),
    SHIPPED(1,"已发货"),
    RECEIVED(2,"已收货"),
    REJECTED(4,"退货");


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

    private ShippingEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }
}
