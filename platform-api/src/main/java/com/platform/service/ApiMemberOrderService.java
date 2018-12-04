package com.platform.service;

import com.alibaba.fastjson.JSONObject;
import com.platform.dao.ApiMemberOrderMapper;
import com.platform.entity.MemberCardVo;
import com.platform.entity.OrderVo;
import com.platform.entity.UserVo;
import com.platform.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.platform.entity.MemberOrderVo;

/**
 * Service实现类
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2018-11-20 13:17:29
 */
@Service
public class ApiMemberOrderService {
    @Autowired
    private ApiMemberOrderMapper apiMemberOrderMapper;
    @Autowired
    private ApiMemberCardService apiMemberCardService;


    public MemberOrderVo queryObject(Integer id) {
        return apiMemberOrderMapper.queryObject(id);
    }

    public List<MemberOrderVo> queryList(Map<String, Object> map) {
        return apiMemberOrderMapper.queryList(map);
    }

    public int queryTotal(Map<String, Object> map) {
        return apiMemberOrderMapper.queryTotal(map);
    }

    public Map<String, Object> save(JSONObject jsonParam, UserVo loginUser) {
        Map<String, Object> resultObj = new HashMap();
        MemberOrderVo memberOrder = new MemberOrderVo();

        memberOrder.setCardId((Integer) jsonParam.get("cardId"));
        MemberCardVo cardVo = apiMemberCardService.queryObject(memberOrder.getCardId());
        memberOrder.setPayStatus(0);
        memberOrder.setActualPrice(cardVo.getActualPrice());
        memberOrder.setAddTime(new Date());
        memberOrder.setOrderNo(CommonUtil.generateOrderNumber());
        memberOrder.setUserId(loginUser.getUserId());


        resultObj.put("errno", 0);
        resultObj.put("errmsg", "订单提交成功");
        apiMemberOrderMapper.save(memberOrder);
        if (null == memberOrder.getId()) {
            resultObj.put("errno", 1);
            resultObj.put("errmsg", "订单提交失败");
            return resultObj;
        }
        Map<String, MemberOrderVo> orderInfoMap = new HashMap<String, MemberOrderVo>();
        orderInfoMap.put("orderInfo", memberOrder);
        //
        resultObj.put("data", orderInfoMap);
        return  resultObj;
    }

    public int update(MemberOrderVo memberOrder) {
        return apiMemberOrderMapper.update(memberOrder);
    }

    public int delete(Integer id) {
        return apiMemberOrderMapper.delete(id);
    }

    public int deleteBatch(Integer[] ids) {
        return apiMemberOrderMapper.deleteBatch(ids);
    }



    /**
     * 根据会员身份选择价格，会员使用零售价RetailPrice，非会员使用市场价marketPrice
     * */
    public BigDecimal getMemberPrice(UserVo userVo,BigDecimal marketPrice,BigDecimal retailPrice) {
        BigDecimal resultPrice = userVo.getIs_member()==1?retailPrice:marketPrice;
        return  resultPrice;
    }
}
