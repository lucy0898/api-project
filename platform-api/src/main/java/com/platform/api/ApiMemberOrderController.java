package com.platform.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.platform.annotation.LoginUser;
import com.platform.entity.UserVo;
import com.platform.service.ApiMemberOrderService;
import com.platform.util.ApiBaseAction;
import io.swagger.annotations.Api;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.entity.MemberOrderVo;
import com.platform.utils.PageUtils;
import com.platform.utils.Query;

/**
 * Controller
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2018-11-20 13:17:29
 */
@Api(tags = "会员订单")
@RestController
@RequestMapping("/api/memberorder")
public class ApiMemberOrderController extends ApiBaseAction {
    @Autowired
    private ApiMemberOrderService memberOrderService;

    /**
     * 查看列表
     */
    @RequestMapping("/list")
    public Object list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);

        List<MemberOrderVo> memberOrderList = memberOrderService.queryList(query);
        int total = memberOrderService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(memberOrderList, total, query.getLimit(), query.getPage());
        Map<String,Object> res= new HashMap<>();
        res.put("page", pageUtil);
        return toResponsSuccess(res);
    }

    /**
     * 查看信息
     */
    @RequestMapping("/info/{id}")
    public Object info(@PathVariable("id") Integer id) {
        MemberOrderVo memberOrder = memberOrderService.queryObject(id);
        Map<String,Object> res= new HashMap<>();
        res.put("detail", memberOrder);
        return toResponsSuccess(res);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Object save(@LoginUser UserVo loginUser) {
        Map resultObj = null;
        resultObj = memberOrderService.save(getJsonRequest(),loginUser);
        if (null != resultObj) {
            return toResponsObject(MapUtils.getInteger(resultObj, "errno"), MapUtils.getString(resultObj, "errmsg"), resultObj.get("data"));
        }
        return toResponsFail("提交失败");
    }




    /**
     * 查看所有列表
     */
    @RequestMapping("/queryAll")
    public Object queryAll(@RequestParam Map<String, Object> params) {

        List<MemberOrderVo> list = memberOrderService.queryList(params);
        Map<String,Object> res= new HashMap<>();
        res.put("list", list);
        return toResponsSuccess(res);
    }
}
