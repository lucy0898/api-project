package com.platform.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体
 * 表名 nideshop_user_account_log
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2018-11-22 14:06:41
 */
public class UserAccountLogVo implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    private Long id;
    //
    private Long userId;
    //类型 1 waste
    private Integer logType;
    //
    private Date addtime;
    //环节，描述
    private String desc;
    //
    private Integer orderId;
    //add,sub
    private String moneyType;
    private BigDecimal moneyValue;

    /**
     * 设置：主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取：主键
     */
    public Long getId() {
        return id;
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
     * 设置：类型 1 waste
     */
    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    /**
     * 获取：类型 1 waste
     */
    public Integer getLogType() {
        return logType;
    }
    /**
     * 设置：
     */
    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    /**
     * 获取：
     */
    public Date getAddtime() {
        return addtime;
    }
    /**
     * 设置：环节，描述
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 获取：环节，描述
     */
    public String getDesc() {
        return desc;
    }
    /**
     * 设置：
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取：
     */
    public Integer getOrderId() {
        return orderId;
    }
    /**
     * 设置：add,sub
     */
    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    /**
     * 获取：add,sub
     */
    public String getMoneyType() {
        return moneyType;
    }

    public BigDecimal getMoneyValue() {
        return moneyValue;
    }

    public void setMoneyValue(BigDecimal moneyValue) {
        this.moneyValue = moneyValue;
    }
}
