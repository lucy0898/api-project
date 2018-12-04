package com.platform.dao;

import com.platform.entity.AddressVo;

import java.util.Map;

/**
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-11 09:14:25
 */
public interface ApiAddressMapper extends BaseDao<AddressVo> {

    void updateAddresDefault(Map<String,Object> valuesMap);
}
