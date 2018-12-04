package com.platform.dao;

import com.platform.entity.ProductVo;

import java.util.Map;

/**
 * 
 * 
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-11 09:16:46
 */
public interface ApiProductMapper extends BaseDao<ProductVo> {

    int updateSubNum(Map productParams);
}
