package com.platform.api;

import com.platform.annotation.IgnoreAuth;
import com.platform.annotation.LoginUser;
import com.platform.dao.ApiOrderAfterSalesMapper;
import com.platform.entity.*;
import com.platform.enums.OrderEnum;
import com.platform.enums.RefundEnum;
import com.platform.service.*;
import com.platform.util.ApiBaseAction;
import com.platform.util.ApiPageUtils;
import com.platform.utils.wechat.WechatRefundApiResult;
import com.platform.utils.wechat.WechatUtil;
import com.platform.utils.Query;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "订单相关")
@RestController
@RequestMapping("/api/order")
public class ApiOrderController extends ApiBaseAction {
    @Autowired
    private ApiOrderService orderService;
    @Autowired
    private ApiOrderGoodsService orderGoodsService;
    @Autowired
    private ApiKdniaoService apiKdniaoService;
    @Autowired
    private APiUserAccountLogService userAccountLogService;
    @Autowired
    private ApiUserService userService;
    @Autowired
    private ApiOrderAfterSalesMapper apiOrderAfterSalesMapper;

    /**
     */
    @ApiOperation(value = "订单首页")
    @IgnoreAuth
    @PostMapping("index")
    public Object index() {
        //
        return toResponsSuccess("");
    }

    /**
     * 获取订单列表
     */
    @ApiOperation(value = "获取订单列表")
    @PostMapping("list")
    public Object list(@LoginUser UserVo loginUser,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size,Integer orderStatus) {
        //
        Map params = new HashMap();
        params.put("user_id", loginUser.getUserId());
        params.put("page", page);
        params.put("limit", size);
        params.put("sidx", "id");
        params.put("order", "desc");
        if(orderStatus!=null){
            params.put("order_status", orderStatus);
        }
        //查询列表数据
        Query query = new Query(params);
        List<OrderVo> orderEntityList = orderService.queryList(query);
        int total = orderService.queryTotal(query);
        ApiPageUtils pageUtil = new ApiPageUtils(orderEntityList, total, query.getLimit(), query.getPage());
        //
        for (OrderVo item : orderEntityList) {
            Map orderGoodsParam = new HashMap();
            orderGoodsParam.put("order_id", item.getId());
            //订单的商品
            List<OrderGoodsVo> goodsList = orderGoodsService.queryList(orderGoodsParam);
            Integer goodsCount = 0;
            for (OrderGoodsVo orderGoodsEntity : goodsList) {
                goodsCount += orderGoodsEntity.getNumber();
                item.setGoodsCount(goodsCount);
            }
        }
        pageUtil.setData(getOrderVoLists(orderEntityList));
        return toResponsSuccess(pageUtil);
    }
    /*优化接口调用封装方法*/
    private Map<String,Object> getOrderInfo(OrderVo vo){
        Map<String,Object> map = new HashMap<>();
        map.put("id",vo.getId());
        map.put("order_sn",vo.getOrder_sn());
        map.put("user_id",vo.getUser_id());
        map.put("order_status",vo.getOrder_status());
        map.put("consignee",vo.getConsignee());
        map.put("address",vo.getAddress());
        map.put("mobile",vo.getMobile());
        map.put("actual_price",vo.getActual_price());
        map.put("goods_price",vo.getGoods_price());
        map.put("add_time",vo.getAdd_time());
        map.put("freight_price",vo.getFreight_price());
        Map<String,Object> handOption = new HashMap<>();
        handOption.put("pay",vo.getHandleOption().get("pay"));
        handOption.put("confirm",vo.getHandleOption().get("confirm"));
        map.put("handleOption",handOption);
        map.put("full_region",vo.getFull_region());

        return map;
    }


    /*优化接口调用封装方法二*/
    private List<Map<String,Object>> getOrderGoods(List<OrderGoodsVo> orderGoodsVoList){
        List<Map<String,Object>> lists = new ArrayList<>();
        for(OrderGoodsVo orderGoodsVo : orderGoodsVoList){
            Map<String,Object> map = new HashMap<>();
            map.put("id",orderGoodsVo.getId());
            map.put("goods_name",orderGoodsVo.getGoods_name());
            map.put("number",orderGoodsVo.getNumber());
//            map.put("market_price",orderGoodsVo.getMarket_price());
//            map.put("retail_price",orderGoodsVo.getRetail_price());
            map.put("goods_specification_name_value",orderGoodsVo.getGoods_specifition_name_value());
            map.put("list_pic_url",orderGoodsVo.getList_pic_url());
            map.put("order_price",orderGoodsVo.getOrder_price());
            lists.add(map);
        }
        return lists;
    }

    /*优化接口调用封装方法三*/
    private List<Map<String,Object>> getOrderVoLists(List<OrderVo> orderVoList){
        List<Map<String,Object>> lists = new ArrayList<>();
        for(OrderVo vo : orderVoList){
            Map<String,Object> map = new HashMap<>();
            map.put("id",vo.getId());
            map.put("order_sn",vo.getOrder_sn());
            map.put("user_id",vo.getUser_id());
            map.put("order_status",vo.getOrder_status());
            map.put("actual_price",vo.getActual_price());
            map.put("shipping_name",vo.getShipping_name());
            map.put("shipping_no",vo.getShipping_no());
            map.put("order_status_text",vo.getOrder_status_text());
            map.put("handleOption",vo.getHandleOption());
            lists.add(map);
        }
        return lists;
    }

    /*优化接口调用封装方法四*/
    private List<Map<String,Object>> getOrderVoToLists(List<OrderVo> orderList){
        List<Map<String,Object>> lists = new ArrayList<>();
        orderList.forEach(vo->{
            Map<String,Object> map = new HashMap<>();
            map.put("id",vo.getId());
            map.put("order_sn",vo.getOrder_sn());//订单编号
            map.put("actual_price",vo.getActual_price());//订单总额
            map.put("goods_name",vo.getGoodsName());//商品名称
            map.put("list_pic_url",vo.getListPicUrl());//商品封面
            map.put("number",vo.getNumber());//商品数量
            map.put("unit_order_price",vo.getUnit_price());//商品成交价格
            lists.add(map);
        });
        return lists;
    }
    /**
     * 获取订单详情
     */
    @ApiOperation(value = "获取订单详情")
    @PostMapping("detail")
    public Object detail(Integer orderId) {
        Map resultObj = new HashMap();
        //
        OrderVo orderInfo = orderService.queryObject(orderId);
        if (null == orderInfo) {
            return toResponsObject(400, "订单不存在", "");
        }
        Map orderGoodsParam = new HashMap();
        orderGoodsParam.put("order_id", orderId);
        //订单的商品
        List<OrderGoodsVo> orderGoods = orderGoodsService.queryList(orderGoodsParam);
        //订单最后支付时间
        if (orderInfo.getOrder_status() == 0) {
            // if (moment().subtract(60, 'minutes') < moment(orderInfo.add_time)) {
//            orderInfo.final_pay_time = moment("001234", "Hmmss").format("mm:ss")
            // } else {
            //     //超过时间不支付，更新订单状态为取消
            // }
        }

        //订单可操作的选择,删除，支付，收货，评论，退换货
        Map handleOption = orderInfo.getHandleOption();
        //
        resultObj.put("orderInfo", getOrderInfo(orderInfo));
        resultObj.put("orderGoods", getOrderGoods(orderGoods));
        //resultObj.put("handleOption", handleOption);
        if (!StringUtils.isEmpty(orderInfo.getShipping_code()) && !StringUtils.isEmpty(orderInfo.getShipping_no())) {
            // 快递
            List Traces = apiKdniaoService.getOrderTracesByJson(orderInfo.getShipping_code(), orderInfo.getShipping_no());
            resultObj.put("shippingList", Traces);
        }
        return toResponsSuccess(resultObj);
    }

    @ApiOperation(value = "修改订单")
    @PostMapping("updateSuccess")
    public Object updateSuccess(Integer orderId) {
        OrderVo orderInfo = orderService.queryObject(orderId);
        if (orderInfo == null) {
            return toResponsFail("订单不存在");
        } else if (orderInfo.getOrder_status() != 0) {
            return toResponsFail("订单状态不正确orderStatus" + orderInfo.getOrder_status() + "payStatus" + orderInfo.getPay_status());
        }

        orderInfo.setId(orderId);
        orderInfo.setPay_status(2);
        orderInfo.setOrder_status(201);
        orderInfo.setShipping_status(0);
        orderInfo.setPay_time(new Date());
        int num = orderService.update(orderInfo);
        if (num > 0) {
            return toResponsMsgSuccess("修改订单成功");
        } else {
            return toResponsFail("修改订单失败");
        }
    }

    /**
     * 获取订单列表
     */
    @ApiOperation(value = "订单提交")
    @PostMapping("submit")
    public Object submit(@LoginUser UserVo loginUser) {
        Map resultObj = null;
        try {
//            resultObj = orderService.submit(getJsonRequest(), loginUser);
            resultObj = orderService.submit(getJsonRequest(), loginUser);
            if (null != resultObj) {
                return toResponsObject(MapUtils.getInteger(resultObj, "errno"), MapUtils.getString(resultObj, "errmsg"), resultObj.get("data"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultObj = new HashMap();
            resultObj.put("errno", 1);
            resultObj.put("errmsg", "库存不足，提交订单失败！");
            return resultObj;
        }
        return toResponsFail("提交失败");
    }

    /**
     * 获取订单列表
     */
    @ApiOperation(value = "取消订单")
    @PostMapping("cancelOrder")
    public Object cancelOrder(Integer orderId) {
        try {
            OrderVo orderVo = orderService.queryObject(orderId);
            if (orderVo.getOrder_status() == 300) {
                return toResponsFail("已发货，不能取消");
            } else if (orderVo.getOrder_status() == 301) {
                return toResponsFail("已收货，不能取消");
            }
            // 需要退款
            if (orderVo.getPay_status() == 2) {
                WechatRefundApiResult result = WechatUtil.wxRefund(orderVo.getId().toString(),
                        0.01, 0.01);
                if (result.getResult_code().equals("SUCCESS")) {
                    if (orderVo.getOrder_status() == 201) {
                        orderVo.setOrder_status(401);
                    } else if (orderVo.getOrder_status() == 300) {
                        orderVo.setOrder_status(402);
                    }
                    orderVo.setPay_status(4);
                    orderService.update(orderVo);
                    return toResponsMsgSuccess("取消成功");
                } else {
                    return toResponsObject(400, "取消成失败", "");
                }
            } else {
                orderVo.setOrder_status(101);
                orderService.update(orderVo);
                return toResponsSuccess("取消成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toResponsFail("提交失败");
    }

    /**
     * 确认收货
     */
    @ApiOperation(value = "确认收货")
    @PostMapping("confirmOrder")
    public Object confirmOrder(@LoginUser UserVo loginUser,Integer orderId) {
        try {
            OrderVo orderVo = orderService.queryObject(orderId);
            orderVo.setOrder_status(301);
            orderVo.setShipping_status(2);
            orderVo.setConfirm_time(new Date());
            orderService.update(orderVo);
            if(loginUser.getIs_member()==0){
                UserAccountLogVo log = new UserAccountLogVo();
                log.setAddtime(new Date());
                log.setDesc("非会员购买订单");
                log.setOrderId(orderVo.getId());
                log.setLogType(1);
                log.setMoneyType("add");
                Map<String,Object> map = new HashMap<>();
                map.put("orderId",orderVo.getId());
                List<OrderGoodsVo> list = orderGoodsService.queryList(map);
                BigDecimal waste = new BigDecimal(0.00);
                list.forEach((o)->{
                    waste.add(o.getMarket_price().subtract(o.getRetail_price()).multiply(new BigDecimal(o.getNumber())));
                });
                UserVo newUser = new UserVo();
                newUser.setUserId(loginUser.getUserId());
                newUser.setWaste_money(waste);
                log.setMoneyValue(waste);
                userService.update(newUser);
                userAccountLogService.save(log);
            }
            return toResponsSuccess("确认收货成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toResponsFail("提交失败");
    }

    /**
     * 根据订单状态获取订单总数
     */
    @ApiOperation(value = "根据状态获取订单数")
    @PostMapping("get_order_count")
    public Object getOrderCount(@LoginUser UserVo userVo,@RequestParam(value = "order_type") int type) {
        try {
            List<Map<String,Object>> lists = new ArrayList<>();
            if(type>=0&&type<999){
                Map<String,Object> map = new HashMap<>();
                map.put("user_id",userVo.getUserId());
                map.put("order_status",type);
                Map<String,Object> resultMap = new HashMap<>();
                int total =  orderService.queryTotal(map);
                resultMap.put("total",total);
                lists.add(resultMap);
            }
            else if(type==999){
                Map<String,Object> map = new HashMap<>();
                map.put("user_id",userVo.getUserId());
                int total = apiOrderAfterSalesMapper.queryTotal(map);
                Map<String,Object> resultMap = new HashMap<>();
                resultMap.put("total",total);
                lists.add(resultMap);
            }
            else{
                int[] arrays = {OrderEnum.AWAITINGFORPAY.getStatus(),OrderEnum.PAIDUNDELIEVERED.getStatus(),OrderEnum.DELIEVERED.getStatus(),OrderEnum.REFOUNDALL.getStatus()};
                String[] stringList ={"unpay","undeliever","deliever","refound"};
                for(int i=0;i<arrays.length;i++){
                    int t = arrays[i];
                    String name = stringList[i];
                    Map<String,Object> map = new HashMap<>();
                    map.put("user_id",userVo.getUserId());
                    map.put("order_status",t);
                    Map<String,Object> resultMap = new HashMap<>();
                    resultMap.put(name,orderService.queryTotal(map));
                    lists.add(resultMap);
                }
            }
            return toResponsSuccess(lists);

        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }

    /**
     * 根据订单状态获取订单列表
     */
    @ApiOperation(value = "根据状态获取订单列表")
    @PostMapping("get_order_list")
    public Object getOrderList(@LoginUser UserVo userVo,@RequestParam(value = "order_type") int type,@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size) {
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("user_id",userVo.getUserId());
            map.put("userId",userVo.getUserId());
            ///999未退款列表状态标识
            if(type>=0&&type<999){
                map.put("order_status",type);
            }
            int total = type==999?apiOrderAfterSalesMapper.queryTotal(map):orderService.queryTotal(map);
            Integer offset = (page-1)*size;
            map.put("offset", offset);
            map.put("limit", size);
            List<Map<String,Object>> resultMapList = null;
            if(type==999){
                List<OrderAfterSalesVo> orderAfterSalesVoLists = apiOrderAfterSalesMapper.queryList(map);
                resultMapList = setRefundOrderTreeMap(orderAfterSalesVoLists);
            }
            else{
                List<MyOrderDetailsVo> orderVoList  =  orderService.queryMyList(map);
                resultMapList = setTreeMap(orderVoList);
            }
            Map<String,Object> pageMap = new HashMap<>();
            pageMap.put("currentPage",page);
            int totalPage = (int)Math.ceil(total/size);
            totalPage=totalPage==0?1:totalPage;
            pageMap.put("totalPages",totalPage);
            pageMap.put("list",resultMapList);
            return toResponsSuccess(pageMap);

        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }


    /**
     * 退款申请
     */
    @ApiOperation(value = "退款申请")
    @PostMapping("order_refund")
    public Object orderRefund(@LoginUser UserVo userVo, @RequestParam(value = "orderId") Integer orderId,@RequestParam(value = "desc") String desc) {
        try {
            OrderVo orderVo =  orderService.queryObject(orderId);
            if(orderVo==null){
                return toResponsFail("订单不存在");
            }
            OrderAfterSalesVo orderAfterSalesVo = new OrderAfterSalesVo();
            Integer userId= Integer.valueOf(userVo.getUserId().toString());

            orderAfterSalesVo.setId(0);
            orderAfterSalesVo.setOrderId(orderId);
            orderAfterSalesVo.setUserId(userId);
            orderAfterSalesVo.setOrderSn(orderVo.getOrder_sn());
            orderAfterSalesVo.setCreateOrderTime(orderVo.getAdd_time());
            orderAfterSalesVo.setRefundDesc(desc);
            orderAfterSalesVo.setIsGoodsReceived(0);
            orderAfterSalesVo.setOrderPrice(orderVo.getActual_price());
            orderAfterSalesVo.setRefundPrice(orderVo.getActual_price());
            orderAfterSalesVo.setRefundStatus(RefundEnum.NOREFUND.getStatus());
            orderAfterSalesVo.setIsGoodsReceived(0);
            orderAfterSalesVo.setSellerId(orderVo.getSellerId());
            orderAfterSalesVo.setRefundTime(new Date());
            apiOrderAfterSalesMapper.save(orderAfterSalesVo);

            orderVo.setOrder_status(OrderEnum.REFUNDING.getStatus());
            orderService.update(orderVo);
            return toResponsSuccess("提交成功");

        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }

    /**
     * 我的退款申请列表
     */
    @ApiOperation(value = "我的退款申请列表")
    @PostMapping("refund_list")
    public Object refundList(@LoginUser UserVo userVo, @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("user_id",userVo.getUserId());
            int total = apiOrderAfterSalesMapper.queryTotal(map);
            Integer userId= Integer.valueOf(userVo.getUserId().toString());
            Integer offset = (page-1)*size;
            map.put("offset", offset);
            map.put("limit", size);
            List<OrderAfterSalesVo> orderAfterSalesVoLists = apiOrderAfterSalesMapper.queryList(map);
            Map<String,Object> pageMap = new HashMap<>();
            pageMap.put("currentPage",page);
            int totalPage = (int)Math.ceil(total/size);
            totalPage=totalPage==0?1:totalPage;
            pageMap.put("totalPages",totalPage);
            pageMap.put("list",setRefundOrderTreeMap(orderAfterSalesVoLists));
            return toResponsSuccess(pageMap);

        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }

    /**
     * 按照异常批次号对已开单数据进行分组
     * @param billingList
     * @return
     * @throws Exception
     */
    private Map<String, List<Map<String,Object>>> groupCode(List<MyOrderDetailsVo> billingList) throws Exception{
        Map<String, List<Map<String,Object>>> resultMap = new HashMap<String, List<Map<String,Object>>>();
        try{
            for(MyOrderDetailsVo tmExcpNew : billingList){
                Map<String,Object> subMap = new HashMap<>();
                if(resultMap.containsKey(tmExcpNew.getOrderSn())){//map中异常批次已存在，将该数据存放到同一个key（key存放的是异常批次）的map中
                    subMap = getTreeMap(tmExcpNew);
                    resultMap.get(tmExcpNew.getOrderSn()).add(subMap);
                }else{//map中不存在，新建key，用来存放数据
                    List<Map<String,Object>> tmpList = new ArrayList<Map<String,Object>>();
                    subMap = getTreeMap(tmExcpNew);
                    tmpList.add(subMap);
                    resultMap.put(tmExcpNew.getOrderSn(), tmpList);
                }
            }

        }catch(Exception e){
            throw new Exception("按照异常批次号对已开单数据进行分组时出现异常", e);
        }

        return resultMap;
    }
    private Map<String,Object> getTreeMap(MyOrderDetailsVo tmExcpNew){
        Map<String,Object> subMap =new HashMap<>();
        subMap.put("goodsName",tmExcpNew.getGoodsName());
        subMap.put("listPicUrl",tmExcpNew.getListPicUrl());
        subMap.put("goodsNumber",tmExcpNew.getGoodsNumber());
        subMap.put("orderPrice",tmExcpNew.getOrderPrice());
        subMap.put("actualPrice",tmExcpNew.getActualPrice());
        return  subMap;

    }

    /**
     * 获取我的退款订单列表树状数据
     * @param orderVoList
     * @return
     * @throws Exception
     */
    private List<Map<String,Object>> setRefundOrderTreeMap(List<OrderAfterSalesVo> orderVoList){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<Integer,String> map = getRefundStatusMap();
        orderVoList.forEach(o->{
            Map<String,Object> resultmap = new HashMap<>();
            resultmap.put("id",o.getId());
            resultmap.put("orderSn",o.getOrderSn());
            resultmap.put("actualPrice",o.getOrderPrice());
            resultmap.put("orderStatus",o.getRefundStatus());
            resultmap.put("orderStatusName", map.get(o.getRefundStatus()));
            resultmap.put("shippingName","");
            resultmap.put("shippingNo","");
            Map<String,Object> searchMap = new HashMap<>();
            searchMap.put("order_id",o.getOrderId());
            List<Map<String,Object>> sublist = new ArrayList<>();
            List<MyOrderDetailsVo> sublists = orderService.queryMySubList(searchMap);
            sublists.forEach(j->{
                Map<String,Object> sutmap = new HashMap<>();
                sutmap.put("listPicUrl",j.getListPicUrl());
                sutmap.put("goodsNumber",j.getGoodsNumber());
                sutmap.put("orderPrice",j.getOrderPrice());
                sutmap.put("goodsName",j.getGoodsName());
                sutmap.put("specification",j.getSpecification());
                sublist.add(sutmap);
            });
            resultmap.put("sublist",sublist);
            list.add(resultmap);
        });
        return list;
    }

    /**
     * 获取订单列表树状数据
     * @param orderVoList
     * @return
     * @throws Exception
     */
    private List<Map<String,Object>> setTreeMap(List<MyOrderDetailsVo> orderVoList){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<Integer,String> map = getOrderStatusMap();
        orderVoList.forEach(o->{
            Map<String,Object> resultmap = new HashMap<>();
            resultmap.put("id",o.getId());
            resultmap.put("orderSn",o.getOrderSn());
            resultmap.put("actualPrice",o.getActualPrice());
            resultmap.put("orderStatus",o.getOrder_status());
            resultmap.put("orderStatusName", map.get(o.getOrder_status()));
            resultmap.put("shippingName",o.getShippingName());
            resultmap.put("shippingNo",o.getShippingNo());
            Map<String,Object> searchMap = new HashMap<>();
            searchMap.put("order_id",o.getId());
            List<Map<String,Object>> sublist = new ArrayList<>();
            List<MyOrderDetailsVo> sublists = orderService.queryMySubList(searchMap);
            sublists.forEach(j->{
                Map<String,Object> subtmap = new HashMap<>();
                subtmap.put("goodsName",j.getGoodsName());
                subtmap.put("specification",j.getSpecification());
                subtmap.put("listPicUrl",j.getListPicUrl());
                subtmap.put("goodsNumber",j.getGoodsNumber());
                subtmap.put("orderPrice",j.getOrderPrice());
                sublist.add(subtmap);
            });
            resultmap.put("sublist",sublist);
            list.add(resultmap);
        });
        return list;
    }

    private Map<Integer,String> getOrderStatusMap(){
        Map<Integer,String> map = new HashMap<>();
        map.put(OrderEnum.AWAITINGFORPAY.getStatus(),"待支付");
        map.put(OrderEnum.PAIDUNDELIEVERED.getStatus(),"待发货");
        map.put(OrderEnum.DELIEVERED.getStatus(),"待收货");
        map.put(OrderEnum.REFUNDING.getStatus(),"退款审核中");
        return  map;
    }

    private Map<Integer,String> getRefundStatusMap(){
        Map<Integer,String> map = new HashMap<>();
        map.put(RefundEnum.NOREFUND.getStatus(),"未退款");
        map.put(RefundEnum.REFUNDING.getStatus(),"退款中");
        map.put(RefundEnum.REFUND.getStatus(),"已退款");
        return  map;
    }


}