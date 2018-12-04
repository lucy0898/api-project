package com.platform.api;

import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.IgnoreAuth;
import com.platform.annotation.LoginUser;
import com.platform.entity.*;
import com.platform.service.*;
import com.platform.util.ApiBaseAction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.List;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 作者: @author LuX <br>
 * 时间: 2018-11-10 <br>
 * 描述: ApiExpressController <br>
 */
@Api(tags = "快递信息接口")
@RestController
@RequestMapping("/api/express")
public class ApiExpressController extends ApiBaseAction {

    @Autowired
    private ApiKdniaoService apikdniaoService;
    @Autowired
    private ApiShippingService apiShippingService;
    @Autowired
    private ApiOrderService orderService;

    @ApiOperation(value = "查询快递信息接口")
    @PostMapping("get_express_info")
    public Object getExpressInfo(@LoginUser UserVo userVo) {
        try {
            JSONObject expressJson = super.getJsonRequestByParameterMap();
            Integer orderId = expressJson.getInteger("orderId");//获取订单号
            OrderVo orderVo = orderService.queryObject(orderId);
            List resultList =null;
            if(orderVo!=null){
                resultList = apikdniaoService.getOrderTracesByJson(orderVo.getShipping_code(),orderVo.getShipping_no());
            }
            return toResponsSuccess(resultList);
        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }

    @ApiOperation(value = "查询快递编码列表")
    @IgnoreAuth
    @GetMapping("get_express_list")
    public Object getExpressList() {
        try {
            Map<String,Object> query_map = new HashMap<>();
            query_map.put("status",0);//查询未被删除的快递信息
            List<ShippingVo> shippingList = apiShippingService.queryList(query_map);
            return  toResponsSuccess(shippingList);
        }
        catch (Exception ex){
            return toResponsFail(ex.getMessage());
        }
    }


}
