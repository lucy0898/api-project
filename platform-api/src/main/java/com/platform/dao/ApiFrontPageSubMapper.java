package com.platform.dao;

import com.platform.entity.FrontPageSubVo;
import com.platform.entity.GoodsVo;

import java.util.List;
import java.util.Map;

public interface ApiFrontPageSubMapper extends BaseDao<FrontPageSubVo> {
    /**
     * 根据主键查询实体
     *
     * @param id 主键
     * @return 实体
     */
    FrontPageSubVo queryObject(Integer id);


    /**
     * 分页统计总数
     *
     * @param map 参数
     * @return 总数
     */
    int queryTotal(Map<String, Object> map);

    /**
     * 保存实体
     *
     * @param brand 实体
     * @return 保存条数
     */
    int save(FrontPageSubVo brand);

    /**
     * 根据主键更新实体
     *
     * @param brand 实体
     * @return 更新条数
     */
    int update(FrontPageSubVo brand);

    /**
     * 根据主键删除
     *
     * @param id
     * @return 删除条数
     */
    int delete(Integer id);

    /**
     * 根据主键批量删除
     *
     * @param ids
     * @return 删除条数
     */
    int deleteBatch(Integer[] ids);

    /***
     * 根据栏目ID查询所有的商品信息
     *
     */

    List<GoodsVo> queryByFid(Map<String,Object> map);

    /**
     * 根据栏目ID查询所有的商品总数
     * */
    int queryCountByFid(Integer fid);

    /**
     * 检查是否已经关联过商品
     * */
    int checkAvailabelInsert(Map<String,Object> map);
}
