package com.platform.dao;

import com.platform.entity.MyOrderDetailsVo;
import com.platform.entity.OrderVo;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-11 09:16:46
 */
public interface ApiOrderMapper extends BaseDao<OrderVo> {
    /**
     * 根据订单状态获取个人订单列表
     * */
    List<MyOrderDetailsVo> queryMyOrderList(Map<String,Object> map);

    List<MyOrderDetailsVo> queryMyList(Map<String,Object> map);

    List<MyOrderDetailsVo> queryMySubList(Map<String,Object> map);
}
