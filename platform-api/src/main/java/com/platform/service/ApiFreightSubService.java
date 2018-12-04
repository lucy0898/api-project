package com.platform.service;


import com.platform.dao.ApiFreightSubMapper;
import com.platform.entity.FreightSubVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ApiFreightSubService  {


    @Autowired
    private ApiFreightSubMapper apiFreightSubMapper;


    public List<FreightSubVo> queryList(Map<String,Object> map){
        return apiFreightSubMapper.queryList(map);
    }

    public List<FreightSubVo> queryListForCun(Map<String,Object> map){
        List<FreightSubVo> list = null;
        Map<Integer,List> result = new HashMap<>();
        Map<String,Object> map2 = new LinkedHashMap<>();
        Map<String,Object> map1 = new LinkedHashMap<>();
        map1.put("provinceId",map.get("provinceId"));
        map1.put("freightId",map.get("freightId"));
        map.forEach((k,v)->{
            if(!k.equals("cityId")){
                map2.put(k,v);
            }
        });
        ExecutorService  executors = Executors.newCachedThreadPool();
        for (int i=0;i<3;i++){
            final int num = i;
            executors.execute(()->{
                switch (num){
                    case 0:result.put(0,apiFreightSubMapper.queryList(map));break;
                    case 1:result.put(1,apiFreightSubMapper.queryList(map2));break;
                    case 2:result.put(3,apiFreightSubMapper.queryList(map1));   break;
                }
            });
        }
        executors.shutdown();
        try {
            while (!executors.awaitTermination(1, TimeUnit.SECONDS)) {
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         /* list = apiFreightSubMapper.queryList(map);
        if (list!=null&&list.size()==1){
            return list;
        }

        map.remove("districtId");
        list = apiFreightSubMapper.queryList(map);
        if (list!=null&&list.size()==1){
            return list;
        }
        map.remove("cityId");
        list = apiFreightSubMapper.queryList(map);
        if (list!=null&&list.size()==1){
            return list;
        }*/
        for (int i=0;i<3;i++){
            if(result.get(i)!=null&&result.get(i).size()==1){
                list = result.get(i);
                break;
            }
        }
        return list;

    }


}
