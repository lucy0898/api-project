package com.platform.service;


import com.platform.dao.ApiFrontPageMapper;
import com.platform.entity.FrontPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ApiFrontPageService {
    @Autowired
    private ApiFrontPageMapper frontPageMapper;

    public FrontPageVo queryObject(Integer id) {return frontPageMapper.queryObject(id);}

    public List<FrontPageVo> queryList(Map<String,Object> map){return frontPageMapper.queryList(map);}

    public int queryTotal() {return frontPageMapper.queryTotal();}

    public int queryFirst(){return frontPageMapper.queryFirst();}

    public int queryMacrosByValue(String value){return frontPageMapper.queryMacrosByValue(value);}


}
