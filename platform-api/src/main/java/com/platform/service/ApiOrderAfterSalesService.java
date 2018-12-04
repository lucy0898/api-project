package com.platform.service;

import com.platform.dao.ApiOrderAfterSalesMapper;
import com.platform.entity.OrderAfterSalesVo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ApiOrderAfterSalesService {
    protected Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ApiOrderAfterSalesMapper apiOrderAfterSalesMapper;

    public OrderAfterSalesVo queryObject(Integer id) {
        return apiOrderAfterSalesMapper.queryObject(id);
    }


    public List<OrderAfterSalesVo> queryList(Map<String, Object> map) {
        return apiOrderAfterSalesMapper.queryList(map);
    }


    public int queryTotal(Map<String, Object> map) {
        return apiOrderAfterSalesMapper.queryTotal(map);
    }


    public void save(OrderAfterSalesVo order) {
        apiOrderAfterSalesMapper.save(order);
    }


    public int update(OrderAfterSalesVo order) {
        return apiOrderAfterSalesMapper.update(order);
    }

}
