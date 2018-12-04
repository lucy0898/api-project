package com.platform.service;


import com.platform.dao.ApiShippingMapper;
import com.platform.entity.ShippingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ApiShippingService {
    @Autowired
    private ApiShippingMapper apiShippingMapper;

    /**
     * 根据主键查询实体
     *
     * @param id 主键
     * @return 实体
     */
    public ShippingVo queryObject(Integer id)  {
        return apiShippingMapper.queryObject(id);
    }

    /**
     * 分页查询
     *
     * @param map 参数
     * @return list
     */
    public List<ShippingVo> queryList(Map<String, Object> map){ return  apiShippingMapper.queryList(map);}

    /**
     * 分页统计总数
     *
     * @param map 参数
     * @return 总数
     */
    public int queryTotal(Map<String, Object> map){return apiShippingMapper.queryTotal(map);}

    /**
     * 保存实体
     *
     * @param shipping 实体
     * @return 保存条数
     */
   public int save(ShippingVo shipping){return  apiShippingMapper.save(shipping);}

    /**
     * 根据主键更新实体
     *
     * @param shipping 实体
     * @return 更新条数
     */
    public int update(ShippingVo shipping){return  apiShippingMapper.update(shipping);}

    /**
     * 根据主键删除
     *
     * @param id
     * @return 删除条数
     */
    public int delete(Integer id){return apiShippingMapper.delete(id);}

    /**
     * 根据主键批量删除
     *
     * @param ids
     * @return 删除条数
     */
    public int deleteBatch(Integer[] ids){return apiShippingMapper.deleteBatch(ids);}
}
