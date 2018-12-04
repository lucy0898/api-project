package com.platform.dao;

import com.platform.entity.FreightSubVo;
import com.platform.entity.FreightVo;

import java.util.List;
import java.util.Map;

public interface ApiFreightMapper extends BaseDao<FreightVo>{


    /**
     * 获取一条记录
     * */
    FreightVo queryObject(Integer id);



}
