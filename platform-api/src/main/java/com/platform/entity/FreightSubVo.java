package com.platform.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class FreightSubVo  {

    private Integer id;
    //运费模板主表ID
    private Integer freightId;
    //省、直辖市ID
    private Integer provinceId;
    //市ID
    private Integer cityId;
    //区、县ID
    private Integer areaId;
    //特殊地区邮费单位上限数（重量：Kg，件数：件，体积：m³）
    private BigDecimal submaxunitLimit;
    //特殊地区邮费单位金额数
    private BigDecimal submaxamountLimit;
    //超过特殊地区邮费单位数
    private BigDecimal subperunitPlus;
    //超过特殊地区邮费单位增加金额数
    private BigDecimal subperamountPlus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFreightId() {
        return freightId;
    }

    public void setFreightId(Integer freightId) {
        this.freightId = freightId;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public BigDecimal getSubmaxunitLimit() {
        return submaxunitLimit;
    }

    public void setSubmaxunitLimit(BigDecimal submaxunitLimit) {
        this.submaxunitLimit = submaxunitLimit;
    }

    public BigDecimal getSubmaxamountLimit() {
        return submaxamountLimit;
    }

    public void setSubmaxamountLimit(BigDecimal submaxamountLimit) {
        this.submaxamountLimit = submaxamountLimit;
    }

    public BigDecimal getSubperunitPlus() {
        return subperunitPlus;
    }

    public void setSubperunitPlus(BigDecimal subperunitPlus) {
        this.subperunitPlus = subperunitPlus;
    }

    public BigDecimal getSubperamountPlus() {
        return subperamountPlus;
    }

    public void setSubperamountPlus(BigDecimal subperamountPlus) {
        this.subperamountPlus = subperamountPlus;
    }


}
