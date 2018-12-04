package com.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.platform.dao.ApiMemberCardMapper;
import com.platform.entity.MemberCardVo;

/**
 * Service实现类
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2018-11-20 13:17:30
 */
@Service
public class ApiMemberCardService {
    @Autowired
    private ApiMemberCardMapper apiMemberCardMapper;

    public MemberCardVo queryObject(Integer id) {
        return apiMemberCardMapper.queryObject(id);
    }

    public List<MemberCardVo> queryList(Map<String, Object> map) {
        return apiMemberCardMapper.queryList(map);
    }

    public int queryTotal(Map<String, Object> map) {
        return apiMemberCardMapper.queryTotal(map);
    }

    public int save(MemberCardVo memberCard) {
        return apiMemberCardMapper.save(memberCard);
    }

    public int update(MemberCardVo memberCard) {
        return apiMemberCardMapper.update(memberCard);
    }

    public int delete(Integer id) {
        return apiMemberCardMapper.delete(id);
    }

    public int deleteBatch(Integer[] ids) {
        return apiMemberCardMapper.deleteBatch(ids);
    }

    public Date getExpireTime(Date inspire_time, String description) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inspire_time);
        switch (description){
            case "MONTH":calendar.add(Calendar.MONTH,1);break;
            case "SEASON":calendar.add(Calendar.MONTH,3);break;
            case "YEAR":calendar.add(Calendar.YEAR,1);break;
        }
        return calendar.getTime();
    }

    public static void main(String [] args){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        ApiMemberCardService aa = new ApiMemberCardService();
        System.out.println(" "+fmt.format(aa.getExpireTime(new Date(),"MONTH")));
        System.out.println(" "+fmt.format(aa.getExpireTime(new Date(),"SEASON")));
        System.out.println(" "+fmt.format(aa.getExpireTime(new Date(),"YEAR")));
    }
}
