package com.platform.dao;

import com.platform.entity.FrontPageVo;

import java.util.List;
import java.util.Map;

public interface ApiFrontPageMapper extends BaseDao<FrontPageVo> {
    /**
     * 分页查询
     *
     * @param map 参数
     * @return list
     */
    List<FrontPageVo> queryList(Map<String,Object> map);

    /**
     * 获取总数
     * */
    int queryTotal();

    /**
     * 获取第一条的id
     * */
    int queryFirst();

    /**
     * 获取一条记录
     * */
    FrontPageVo queryObject(Integer id);

    /**
     * 根据字典值获取ID
     * */
    int queryMacrosByValue(String value);
}
