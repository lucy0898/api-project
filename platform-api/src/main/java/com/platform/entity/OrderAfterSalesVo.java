package com.platform.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderAfterSalesVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    //订单ID
    private Integer orderId;
    //订单编号
    private String orderSn;
    //下订单用户Id
    private Integer userId;
    //申请退货人
    private String orderUserName;
    //下单时间
    private Date createOrderTime;
    //退款申请时间
    private Date refundTime;
    //退款申请原因描述
    private String refundDesc;
    //订单金额
    private BigDecimal orderPrice;
    //退款金额
    private BigDecimal refundPrice;
    //是否收到退货 0:未收到退货 1:已收到退货
    private Integer isGoodsReceived;
    //订单退款状态 0:未退款 1;退款中 2;已退款
    private Integer refundStatus;
    //商家ID
    private Integer sellerId;
    //点击确认退款操作员ID
    private Integer refundOperator;
    //确认退款操作员确认时间
    private Date refundOperatedTime;
    //点击确认收到退货操作员ID
    private Integer goodsReceivedOperator;
    //点击确认收到退货确认时间
    private Date goodsReceivedOperatedTime;
    //订单退款编号
    private String refundSn;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOrderUserName() {
        return orderUserName;
    }

    public void setOrderUserName(String orderUserName) {
        this.orderUserName = orderUserName;
    }

    public Date getCreateOrderTime() {
        return createOrderTime;
    }

    public void setCreateOrderTime(Date createOrderTime) {
        this.createOrderTime = createOrderTime;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public BigDecimal getRefundPrice() {
        return refundPrice;
    }

    public void setRefundPrice(BigDecimal refundPrice) {
        this.refundPrice = refundPrice;
    }

    public Integer getIsGoodsReceived() {
        return isGoodsReceived;
    }

    public void setIsGoodsReceived(Integer isGoodsReceived) {
        this.isGoodsReceived = isGoodsReceived;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getRefundOperator() {
        return refundOperator;
    }

    public void setRefundOperator(Integer refundOperator) {
        this.refundOperator = refundOperator;
    }

    public Date getRefundOperatedTime() {
        return refundOperatedTime;
    }

    public void setRefundOperatedTime(Date refundOperatedTime) {
        this.refundOperatedTime = refundOperatedTime;
    }

    public Date getGoodsReceivedOperatedTime() {
        return goodsReceivedOperatedTime;
    }

    public void setGoodsReceivedOperatedTime(Date goodsReceivedOperatedTime) {
        this.goodsReceivedOperatedTime = goodsReceivedOperatedTime;
    }

    public Integer getGoodsReceivedOperator() {
        return goodsReceivedOperator;
    }

    public void setGoodsReceivedOperator(Integer goodsReceivedOperator) {
        this.goodsReceivedOperator = goodsReceivedOperator;
    }
    public String getRefundDesc() {
        return refundDesc;
    }

    public void setRefundDesc(String refundDesc) {
        this.refundDesc = refundDesc;
    }


    public String getRefundSn() {
        return refundSn;
    }

    public void setRefundSn(String refundSn) {
        this.refundSn = refundSn;
    }
}
