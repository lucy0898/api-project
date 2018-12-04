package com.platform.enums;

/*
订单状态
1xx 表示订单取消和删除等状态 0订单创建成功等待付款，　101订单已取消，　102订单已删除
2xx 表示订单支付状态　201订单已付款，等待发货
3xx 表示订单物流相关状态　300订单已发货， 301用户确认收货
4xx 表示订单退换货相关的状态 400 退款审核中 401 没有发货，退款　402 已收货，退款退货
*/
public enum OrderEnum {
    AWAITINGFORPAY(0,"待支付"),
    ORDERCANCELLED(101,"订单已取消"),
    ORDERDELETED(102,"订单已删除"),
    PAIDUNDELIEVERED(201,"待发货"),
    DELIEVERED(300,"待收货"),
    RECEIVEDCONFIRM(301,"确认收货"),
    REFUNDING(400,"退款审核中"),
    UNDELEIVEREDREFOUND(401,"未发货退款"),
    REFOUNDALL(402,"退货退款");


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

    private OrderEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }
}
