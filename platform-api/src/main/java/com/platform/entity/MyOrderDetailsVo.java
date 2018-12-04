package com.platform.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 我的订单列表对象
 * */
public class MyOrderDetailsVo implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键ID
    private Integer id;
    //订单号
    private String orderSn;
    private Integer order_status;
    //规格
    private String specification;
    //订单总额
    private BigDecimal actualPrice;
    //订单商品名称
    private String goodsName;
    //订单商品封面
    private String listPicUrl;
    //订单商品数量
    private Integer goodsNumber;
    //订单商品价格
    private BigDecimal orderPrice;
    //订单快递公司名称
    private String shippingName;
    //订单快递编号
    private String shippingNo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public BigDecimal getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getListPicUrl() {
        return listPicUrl;
    }

    public void setListPicUrl(String listPicUrl) {
        this.listPicUrl = listPicUrl;
    }

    public Integer getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(Integer goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }


    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }
    public Integer getOrder_status() {
        return order_status;
    }

    public void setOrder_status(Integer order_status) {
        this.order_status = order_status;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getShippingNo() {
        return shippingNo;
    }

    public void setShippingNo(String shippingCode) {
        this.shippingNo = shippingCode;
    }

}
