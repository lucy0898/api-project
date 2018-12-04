package com.platform.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体
 * 表名 nideshop_member_card
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2018-11-20 13:17:30
 */
public class MemberCardVo implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Integer id;
    //实际价格
    private BigDecimal actualPrice;
    //原价
    private BigDecimal origPrice;
    //描述
    private String description;
    //名字
    private String name;

    /**
     * 设置：
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取：
     */
    public Integer getId() {
        return id;
    }
    /**
     * 设置：实际价格
     */
    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }

    /**
     * 获取：实际价格
     */
    public BigDecimal getActualPrice() {
        return actualPrice;
    }
    /**
     * 设置：原价
     */
    public void setOrigPrice(BigDecimal origPrice) {
        this.origPrice = origPrice;
    }

    /**
     * 获取：原价
     */
    public BigDecimal getOrigPrice() {
        return origPrice;
    }
    /**
     * 设置：描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取：描述
     */
    public String getDescription() {
        return description;
    }
    /**
     * 设置：名字
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取：名字
     */
    public String getName() {
        return name;
    }
}
