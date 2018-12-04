package com.platform.entity;

import java.math.BigDecimal;
import java.util.Date;

public class FreightVo {
    private static final long serialVersionUID = 1L;

    private Integer id;
    //模板名称
    private String name;
    //模板类型
    private Integer category;
    //默认邮费单位上限数（重量：Kg，件数：件，体积：m³）
    private BigDecimal maxunitLimit;
    //默认邮费单位金额数
    private BigDecimal maxamountLimit;
    //超过默认邮费单位数
    private BigDecimal perunitPlus;
    //超过默认邮费单位增加金额数
    private BigDecimal peramountPlus;
    //是否自定义金额 0:否，1：是
    private Integer isDefined;
    //满包邮金额
    private BigDecimal definedAmount;

    //是否可用 0:可用 1:禁用
    private Integer enabled;
    //创建人ID
    private Integer creator;
    //创建时间
    private Date createTime;
    //修改人ID
    private Integer editor;
    //修改时间
    private Date editTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public BigDecimal getMaxunitLimit() {
        return maxunitLimit;
    }

    public void setMaxunitLimit(BigDecimal maxunitLimit) {
        this.maxunitLimit = maxunitLimit;
    }

    public BigDecimal getMaxamountLimit() {
        return maxamountLimit;
    }

    public void setMaxamountLimit(BigDecimal maxamountLimit) {
        this.maxamountLimit = maxamountLimit;
    }

    public BigDecimal getPerunitPlus() {
        return perunitPlus;
    }

    public void setPerunitPlus(BigDecimal perunitPlus) {
        this.perunitPlus = perunitPlus;
    }

    public BigDecimal getPeramountPlus() {
        return peramountPlus;
    }

    public void setPeramountPlus(BigDecimal peramountPlus) {
        this.peramountPlus = peramountPlus;
    }

    public Integer getIsDefined() {
        return isDefined;
    }

    public void setIsDefined(Integer isDefined) {
        this.isDefined = isDefined;
    }

    public BigDecimal getDefinedAmount() {
        return definedAmount;
    }

    public void setDefinedAmount(BigDecimal definedAmount) {
        this.definedAmount = definedAmount;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getEditor() {
        return editor;
    }

    public void setEditor(Integer editor) {
        this.editor = editor;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

}
