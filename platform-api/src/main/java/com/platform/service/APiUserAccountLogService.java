package com.platform.service;

import com.platform.dao.ApiUserAccountLogMapper;
import com.platform.entity.UserAccountLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;



/**
 * Service实现类
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2018-11-22 14:06:41
 */
@Service
public class APiUserAccountLogService  {
    @Autowired
    private ApiUserAccountLogMapper userAccountLogMapper;

    public UserAccountLogVo queryObject(Long id) {
        return userAccountLogMapper.queryObject(id);
    }


    public List<UserAccountLogVo> queryList(Map<String, Object> map) {
        return userAccountLogMapper.queryList(map);
    }

    public int queryTotal(Map<String, Object> map) {
        return userAccountLogMapper.queryTotal(map);
    }

    public int save(UserAccountLogVo userAccountLog) {
        return userAccountLogMapper.save(userAccountLog);
    }

    public int update(UserAccountLogVo userAccountLog) {
        return userAccountLogMapper.update(userAccountLog);
    }

    public int delete(Long id) {
        return userAccountLogMapper.delete(id);
    }

    public int deleteBatch(Long[] ids) {
        return userAccountLogMapper.deleteBatch(ids);
    }
}
