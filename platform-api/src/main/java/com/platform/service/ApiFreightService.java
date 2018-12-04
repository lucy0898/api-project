package com.platform.service;

import com.platform.dao.ApiFreightMapper;
import com.platform.entity.FreightVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ApiFreightService {
    @Autowired
    private ApiFreightMapper apiFreightMapper;

    public FreightVo queryObject(Integer id) {return apiFreightMapper.queryObject(id);}





}
