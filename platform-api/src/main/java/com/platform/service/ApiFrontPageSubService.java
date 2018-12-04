package com.platform.service;

import com.platform.dao.ApiFrontPageSubMapper;
import com.platform.entity.FrontPageSubVo;
import com.platform.entity.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ApiFrontPageSubService  {
    @Autowired
    private ApiFrontPageSubMapper apiFrontPageSubMapper;

     public FrontPageSubVo queryObject(Integer id) {
        return apiFrontPageSubMapper.queryObject(id);
    }


    public int queryTotal(Map<String, Object> map) {
        return apiFrontPageSubMapper.queryTotal(map);
    }


    public int save(FrontPageSubVo address) {
        return apiFrontPageSubMapper.save(address);
    }


    public int update(FrontPageSubVo address) {
        return apiFrontPageSubMapper.update(address);
    }


    public int delete(Integer id) {
        return apiFrontPageSubMapper.delete(id);
    }


    public int deleteBatch(Integer[] ids) {
        return apiFrontPageSubMapper.deleteBatch(ids);
    }


    public List<GoodsVo> queryByFid (Map<String,Object> map){ return  apiFrontPageSubMapper.queryByFid(map);}


    public int queryCountByFid(Integer fid) {return apiFrontPageSubMapper.queryCountByFid(fid);}


    public int checkAvailabelInsert(Map<String,Object> map) {return  apiFrontPageSubMapper.checkAvailabelInsert(map);}
}
