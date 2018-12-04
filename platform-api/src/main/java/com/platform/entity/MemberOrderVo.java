package com.platform.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体
 * 表名 nideshop_member_order
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2018-11-20 13:17:29
 */
public class MemberOrderVo implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    private Integer id;
    //
    private String orderNo;
    //
    private Long userId;
    //订单状态
    private Integer orderStatus;
    //订单价格
    private BigDecimal actualPrice;
    //支付状态
    private Integer payStatus;
    //是否开启自动续费
    private Integer autoPay;
    //订单开始时间
    private Date addTime;
    //订单支付时间
    private Date payTime;
    //
    private Date updateTime;

    //付款
    private String payId;

    //商品id
    private Integer cardId;

    /**
     * 设置：主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取：主键
     */
    public Integer getId() {
        return id;
    }
    /**
     * 设置：
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取：
     */
    public String getOrderNo() {
        return orderNo;
    }
    /**
     * 设置：
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取：
     */
    public Long getUserId() {
        return userId;
    }
    /**
     * 设置：订单状态
     */
    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * 获取：订单状态
     */
    public Integer getOrderStatus() {
        return orderStatus;
    }
    /**
     * 设置：订单价格
     */
    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }

    /**
     * 获取：订单价格
     */
    public BigDecimal getActualPrice() {
        return actualPrice;
    }
    /**
     * 设置：支付状态
     */
    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    /**
     * 获取：支付状态
     */
    public Integer getPayStatus() {
        return payStatus;
    }
    /**
     * 设置：是否开启自动续费
     */
    public void setAutoPay(Integer autoPay) {
        this.autoPay = autoPay;
    }

    /**
     * 获取：是否开启自动续费
     */
    public Integer getAutoPay() {
        return autoPay;
    }
    /**
     * 设置：订单开始时间
     */
    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    /**
     * 获取：订单开始时间
     */
    public Date getAddTime() {
        return addTime;
    }
    /**
     * 设置：订单支付时间
     */
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    /**
     * 获取：订单支付时间
     */
    public Date getPayTime() {
        return payTime;
    }
    /**
     * 设置：
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取：
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }
}
