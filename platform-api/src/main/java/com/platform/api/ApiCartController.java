package com.platform.api;

import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.IgnoreAuth;
import com.platform.annotation.LoginUser;
import com.platform.cache.J2CacheUtils;
import com.platform.dao.ApiCouponMapper;
import com.platform.entity.*;
import com.platform.service.*;
import com.platform.util.ApiBaseAction;
import com.platform.utils.ApiRRException;
import com.platform.utils.ResourceUtil;
import com.qiniu.util.StringUtils;
import freemarker.ext.beans.HashAdapter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "购物车")
@RestController
@RequestMapping("/api/cart")
public class ApiCartController extends ApiBaseAction {
    @Autowired
    private ApiCartService cartService;
    @Autowired
    private ApiGoodsService goodsService;
    @Autowired
    private ApiProductService productService;
    @Autowired
    private ApiGoodsSpecificationService goodsSpecificationService;
    @Autowired
    private ApiAddressService addressService;
    @Autowired
    private ApiCouponService apiCouponService;
    @Autowired
    private ApiCouponMapper apiCouponMapper;
    @Autowired
    private ApiUserService userService;

    @Autowired
    private ApiBrandService brandService;
    @Autowired
    private ApiFreightSubService apiFreightSubService;
    @Autowired
    private ApiFreightService apiFreightService;
    @Autowired
    private ApiMemberOrderService apiMemberOrderService;
    /**
     * 获取购物车中的数据
     */
    @ApiOperation(value = "获取购物车中的数据")
    @PostMapping("getCart")
    public Object getCart(@LoginUser UserVo loginUser) {
        Map<String, Object> resultObj = new HashMap();
        //查询列表数据
        Map param = new HashMap();
        param.put("user_id", loginUser.getUserId());
        List<CartVo> cartList = cartService.queryList(param);
        //获取购物车统计信息
        Integer goodsCount = 0;
        BigDecimal goodsAmount = new BigDecimal(0.00);
        Integer checkedGoodsCount = 0;
        BigDecimal checkedGoodsAmount = new BigDecimal(0.00);

        for (CartVo cartItem : cartList) {
            goodsCount += cartItem.getNumber();
            goodsAmount = goodsAmount.add(cartItem.getRetail_price().multiply(new BigDecimal(cartItem.getNumber())));
            if (null != cartItem.getChecked() && 1 == cartItem.getChecked()) {
                checkedGoodsCount += cartItem.getNumber();
                BigDecimal resultPrice = apiMemberOrderService.getMemberPrice(loginUser,cartItem.getMarket_price(),cartItem.getRetail_price());
                //checkedGoodsAmount = checkedGoodsAmount.add(cartItem.getRetail_price().multiply(new BigDecimal(cartItem.getNumber())));
                checkedGoodsAmount = checkedGoodsAmount.add(resultPrice.multiply(new BigDecimal(cartItem.getNumber())));
            }
        }
        // 获取优惠信息提示
        Map couponParam = new HashMap();
        couponParam.put("enabled", true);
        Integer[] send_types = new Integer[]{0, 7};
        couponParam.put("send_types", send_types);
        List<CouponInfoVo> couponInfoList = new ArrayList();
        List<CouponVo> couponVos = apiCouponService.queryList(couponParam);
        if (null != couponVos && couponVos.size() > 0) {
            CouponInfoVo fullCutVo = new CouponInfoVo();
            BigDecimal fullCutDec = new BigDecimal(0);
            BigDecimal minAmount = new BigDecimal(100000);
            for (CouponVo couponVo : couponVos) {
                BigDecimal difDec = couponVo.getMin_goods_amount().subtract(checkedGoodsAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
                if (couponVo.getSend_type() == 0 && difDec.doubleValue() > 0.0
                        && minAmount.compareTo(couponVo.getMin_goods_amount()) > 0) {
                    fullCutDec = couponVo.getType_money();
                    minAmount = couponVo.getMin_goods_amount();
                    fullCutVo.setType(1);
                    fullCutVo.setMsg(couponVo.getName() + "，还差" + difDec + "元");
                } else if (couponVo.getSend_type() == 0 && difDec.doubleValue() < 0.0 && fullCutDec.compareTo(couponVo.getType_money()) < 0) {
                    fullCutDec = couponVo.getType_money();
                    fullCutVo.setType(0);
                    fullCutVo.setMsg("可使用满减券" + couponVo.getName());
                }
                if (couponVo.getSend_type() == 7 && difDec.doubleValue() > 0.0) {
                    CouponInfoVo cpVo = new CouponInfoVo();
                    cpVo.setMsg("满￥" + couponVo.getMin_amount() + "元免配送费，还差" + difDec + "元");
                    cpVo.setType(1);
                    couponInfoList.add(cpVo);
                } else if (couponVo.getSend_type() == 7) {
                    CouponInfoVo cpVo = new CouponInfoVo();
                    cpVo.setMsg("满￥" + couponVo.getMin_amount() + "元免配送费");
                    couponInfoList.add(cpVo);
                }
            }
            if (!StringUtils.isNullOrEmpty(fullCutVo.getMsg())) {
                couponInfoList.add(fullCutVo);
            }
        }
        //resultObj.put("couponInfoList", couponInfoList);
        HashMap<Integer,Map> brandVoHashMap = new HashMap<>();
        cartList.forEach((vo)->{
            GoodsVo goodsVo = goodsService.queryObject(vo.getGoods_id());
            if(!brandVoHashMap.containsKey(goodsVo.getBrand_id())){
                Map<String,Object> map = new HashMap<>();
                map.put("id",goodsVo.getBrand_id());
                BrandVo brandVo = brandService.queryObject(goodsVo.getBrand_id());
                if(brandVo==null){
                    map.put("title","");
                    map.put("app_list_pic_url","");
                }else {
                    map.put("title",brandVo.getName());
                    map.put("app_list_pic_url",brandVo.getApp_list_pic_url());
                }
                List<CartVo> cartVos = new ArrayList<>();
                cartVos.add(vo);
                map.put("list",cartVos);
                map.put("checked",vo.getChecked());
                brandVoHashMap.put(goodsVo.getBrand_id(),map);
            }else {
                ((List)(brandVoHashMap.get(goodsVo.getBrand_id()).get("list"))).add(vo);
                brandVoHashMap.get(goodsVo.getBrand_id()).put("checked",vo.getChecked());
            }
        });
        List result = new LinkedList();
        brandVoHashMap.entrySet().forEach((entry)->{
            result.add(entry.getValue());
        });
        resultObj.put("cartList", result);
        //
        Map<String, Object> cartTotal = new HashMap();
        cartTotal.put("goodsCount", goodsCount);
        cartTotal.put("goodsAmount", goodsAmount);
        cartTotal.put("checkedGoodsCount", checkedGoodsCount);
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount);

        resultObj.put("cartTotal", cartTotal);
        return resultObj;
    }

    /**
     * 获取购物车中的数据
     */
    @ApiOperation(value = "获取购物车中的数据")
    @PostMapping("getCartOld")
    public Object getCartOld(@LoginUser UserVo loginUser) {
        Map<String, Object> resultObj = new HashMap();
        //查询列表数据
        Map param = new HashMap();
        param.put("user_id", loginUser.getUserId());
        List<CartVo> cartList = cartService.queryList(param);
        //获取购物车统计信息
        Integer goodsCount = 0;
        BigDecimal goodsAmount = new BigDecimal(0.00);
        Integer checkedGoodsCount = 0;
        BigDecimal checkedGoodsAmount = new BigDecimal(0.00);
        for (CartVo cartItem : cartList) {
            goodsCount += cartItem.getNumber();
            goodsAmount = goodsAmount.add(cartItem.getRetail_price().multiply(new BigDecimal(cartItem.getNumber())));
            if (null != cartItem.getChecked() && 1 == cartItem.getChecked()) {
                checkedGoodsCount += cartItem.getNumber();
                /*会员身份判断价格为零售价*/
                BigDecimal resultPrice = apiMemberOrderService.getMemberPrice(loginUser,cartItem.getMarket_price(),cartItem.getRetail_price());
                checkedGoodsAmount = checkedGoodsAmount.add(resultPrice.multiply(new BigDecimal(cartItem.getNumber())));
                //checkedGoodsAmount = checkedGoodsAmount.add(cartItem.getRetail_price().multiply(new BigDecimal(cartItem.getNumber())));
            }
        }
        // 获取优惠信息提示
        Map couponParam = new HashMap();
        couponParam.put("enabled", true);
        Integer[] send_types = new Integer[]{0, 7};
        couponParam.put("send_types", send_types);
        List<CouponInfoVo> couponInfoList = new ArrayList();
        List<CouponVo> couponVos = apiCouponService.queryList(couponParam);
        if (null != couponVos && couponVos.size() > 0) {
            CouponInfoVo fullCutVo = new CouponInfoVo();
            BigDecimal fullCutDec = new BigDecimal(0);
            BigDecimal minAmount = new BigDecimal(100000);
            for (CouponVo couponVo : couponVos) {
                BigDecimal difDec = couponVo.getMin_goods_amount().subtract(checkedGoodsAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
                if (couponVo.getSend_type() == 0 && difDec.doubleValue() > 0.0
                        && minAmount.compareTo(couponVo.getMin_goods_amount()) > 0) {
                    fullCutDec = couponVo.getType_money();
                    minAmount = couponVo.getMin_goods_amount();
                    fullCutVo.setType(1);
                    fullCutVo.setMsg(couponVo.getName() + "，还差" + difDec + "元");
                } else if (couponVo.getSend_type() == 0 && difDec.doubleValue() < 0.0 && fullCutDec.compareTo(couponVo.getType_money()) < 0) {
                    fullCutDec = couponVo.getType_money();
                    fullCutVo.setType(0);
                    fullCutVo.setMsg("可使用满减券" + couponVo.getName());
                }
                if (couponVo.getSend_type() == 7 && difDec.doubleValue() > 0.0) {
                    CouponInfoVo cpVo = new CouponInfoVo();
                    cpVo.setMsg("满￥" + couponVo.getMin_amount() + "元免配送费，还差" + difDec + "元");
                    cpVo.setType(1);
                    couponInfoList.add(cpVo);
                } else if (couponVo.getSend_type() == 7) {
                    CouponInfoVo cpVo = new CouponInfoVo();
                    cpVo.setMsg("满￥" + couponVo.getMin_amount() + "元免配送费");
                    couponInfoList.add(cpVo);
                }
            }
            if (!StringUtils.isNullOrEmpty(fullCutVo.getMsg())) {
                couponInfoList.add(fullCutVo);
            }
        }
        resultObj.put("couponInfoList", couponInfoList);
        resultObj.put("cartList", cartList);
        //
        Map<String, Object> cartTotal = new HashMap();
        cartTotal.put("goodsCount", goodsCount);
        cartTotal.put("goodsAmount", goodsAmount);
        cartTotal.put("checkedGoodsCount", checkedGoodsCount);
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount);
        //
        resultObj.put("cartTotal", cartTotal);
        return resultObj;
    }

    /**
     * 获取购物车信息，所有对购物车的增删改操作，都要重新返回购物车的信息
     */
    @ApiOperation(value = "获取购物车信息")
    @PostMapping("index")
    public Object index(@LoginUser UserVo loginUser) {
        Map<String,Object> resultMap =getIndexMaps((Map<String, Object>)getCart(loginUser));
        return toResponsSuccess(resultMap);
    }
    /*优化接口调用封装方法*/
    private Map<String,Object> getIndexMaps(Map<String,Object> getcartMap){
        Map<String,Object> newcartmaps = new HashMap<>();
        Map<String,Object> cartmaps = (Map<String,Object>)getcartMap.get("cartTotal");
        newcartmaps.put("checkedGoodsCount",cartmaps.get("checkedGoodsCount"));
        newcartmaps.put("checkedGoodsAmount",cartmaps.get("checkedGoodsAmount"));
        Map<String,Object> parentMap = new HashMap<>();
        parentMap.put("cartTotal",newcartmaps);
        parentMap.put("cartList",getcartMap.get("cartList"));
        return parentMap;
    }



    private String[] getSpecificationIdsArray(String ids) {
        String[] idsArray = null;
        if (org.apache.commons.lang.StringUtils.isNotEmpty(ids)) {
            String[] tempArray = ids.split("_");
            if (null != tempArray && tempArray.length > 0) {
                idsArray = tempArray;
            }
        }
        return idsArray;
    }

    /**
     * 添加商品到购物车
     */
    @ApiOperation(value = "添加商品到购物车")
    @PostMapping("add")
    public Object add(@LoginUser UserVo loginUser) {
        JSONObject jsonParam = getJsonRequest();
        Integer goodsId = jsonParam.getInteger("goodsId");
        Integer productId = jsonParam.getInteger("productId");
        Integer number = jsonParam.getInteger("number");
        //判断商品是否可以购买
        GoodsVo goodsInfo = goodsService.queryObject(goodsId);
        if (null == goodsInfo || goodsInfo.getIs_delete() == 1 || goodsInfo.getIs_on_sale() != 1) {
            return this.toResponsObject(400, "商品已下架", "");
        }
        //取得规格的信息,判断规格库存
        ProductVo productInfo = productService.queryObject(productId);
        if (null == productInfo || productInfo.getGoods_number() < number) {
            return this.toResponsObject(400, "库存不足", "");
        }

        //判断购物车中是否存在此规格商品
        Map cartParam = new HashMap();
        cartParam.put("goods_id", goodsId);
        cartParam.put("product_id", productId);
        cartParam.put("user_id", loginUser.getUserId());
        List<CartVo> cartInfoList = cartService.queryList(cartParam);
        CartVo cartInfo = null != cartInfoList && cartInfoList.size() > 0 ? cartInfoList.get(0) : null;
        if (null == cartInfo) {
            //添加操作
            //添加规格名和值
            String[] goodsSepcifitionValue = null;
            if (null != productInfo.getGoods_specification_ids() && productInfo.getGoods_specification_ids().length() > 0) {
                Map specificationParam = new HashMap();
                String[] idsArray = getSpecificationIdsArray(productInfo.getGoods_specification_ids());
                specificationParam.put("ids", idsArray);
                specificationParam.put("goods_id", goodsId);
                List<GoodsSpecificationVo> specificationEntities = goodsSpecificationService.queryList(specificationParam);
                goodsSepcifitionValue = new String[specificationEntities.size()];
                for (int i = 0; i < specificationEntities.size(); i++) {
                    goodsSepcifitionValue[i] = specificationEntities.get(i).getValue();
                }
            }
            cartInfo = new CartVo();

            cartInfo.setGoods_id(goodsId);
            cartInfo.setProduct_id(productId);
            cartInfo.setGoods_sn(productInfo.getGoods_sn());
            cartInfo.setGoods_name(goodsInfo.getName());
            cartInfo.setList_pic_url(goodsInfo.getList_pic_url());
            cartInfo.setNumber(number);
            cartInfo.setSession_id("1");
            cartInfo.setUser_id(loginUser.getUserId());
            cartInfo.setRetail_price(productInfo.getRetail_price());
            cartInfo.setMarket_price(productInfo.getMarket_price());
            if (null != goodsSepcifitionValue) {
                cartInfo.setGoods_specifition_name_value(StringUtils.join(goodsSepcifitionValue, ";"));
            }
            cartInfo.setGoods_specifition_ids(productInfo.getGoods_specification_ids());
            cartInfo.setChecked(1);
            cartService.save(cartInfo);
        } else {
            //如果已经存在购物车中，则数量增加
            if (productInfo.getGoods_number() < (number + cartInfo.getNumber())) {
                return this.toResponsObject(400, "库存不足", "");
            }
            cartInfo.setNumber(cartInfo.getNumber() + number);
            cartService.update(cartInfo);
        }
        Map<String,Object> getcartMap =(Map<String, Object>)getCart(loginUser);
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> cartTotalMap = (Map<String,Object>)getcartMap.get("cartTotal");
        resultMap.put("goodsCount", cartTotalMap.get("goodsCount"));
        Map<String,Object> finalresultMap = new HashMap<>();
        finalresultMap.put("cartTotal", resultMap);
        return toResponsSuccess(finalresultMap);
    }

    /**
     * 减少商品到购物车
     */
    @ApiOperation(value = "减少商品到购物车")
    @PostMapping("minus")
    public Object minus(@LoginUser UserVo loginUser) {
        JSONObject jsonParam = getJsonRequest();
        Integer goodsId = jsonParam.getInteger("goodsId");
        Integer productId = jsonParam.getInteger("productId");
        Integer number = jsonParam.getInteger("number");
        //判断购物车中是否存在此规格商品
        Map cartParam = new HashMap();
        cartParam.put("goods_id", goodsId);
        cartParam.put("product_id", productId);
        cartParam.put("user_id", loginUser.getUserId());
        List<CartVo> cartInfoList = cartService.queryList(cartParam);
        CartVo cartInfo = null != cartInfoList && cartInfoList.size() > 0 ? cartInfoList.get(0) : null;
        int cart_num = 0;
        if (null != cartInfo) {
            if (cartInfo.getNumber() > number) {
                cartInfo.setNumber(cartInfo.getNumber() - number);
                cartService.update(cartInfo);
                cart_num = cartInfo.getNumber();
            } else if (cartInfo.getNumber() == 1) {
                cartService.delete(cartInfo.getId());
                cart_num = 0;
            }
        }
        return toResponsSuccess(cart_num);
    }

    /**
     * 更新指定的购物车信息
     */
    @ApiOperation(value = "更新指定的购物车信息")
    @PostMapping("update")
    public Object update(@LoginUser UserVo loginUser) {
        JSONObject jsonParam = getJsonRequest();
        Integer goodsId = jsonParam.getInteger("goodsId");
        Integer productId = jsonParam.getInteger("productId");
        Integer number = jsonParam.getInteger("number");
        Integer id = jsonParam.getInteger("id");
        //取得规格的信息,判断规格库存
        ProductVo productInfo = productService.queryObject(productId);
        if (null == productInfo || productInfo.getGoods_number() < number) {
            return this.toResponsObject(400, "库存不足", "");
        }
        //判断是否已经存在product_id购物车商品
        CartVo cartInfo = cartService.queryObject(id);
        //只是更新number
        if (cartInfo.getProduct_id().equals(productId)) {
            cartInfo.setNumber(number);
            cartService.update(cartInfo);
            return toResponsSuccess(getCart(loginUser));
        }

        Map cartParam = new HashMap();
        cartParam.put("goodsId", goodsId);
        cartParam.put("productId", productId);
        List<CartVo> cartInfoList = cartService.queryList(cartParam);
        CartVo newcartInfo = null != cartInfoList && cartInfoList.size() > 0 ? cartInfoList.get(0) : null;
        if (null == newcartInfo) {
            //添加操作
            //添加规格名和值
            String[] goodsSepcifitionValue = null;
            if (null != productInfo.getGoods_specification_ids()) {
                Map specificationParam = new HashMap();
                specificationParam.put("ids", productInfo.getGoods_specification_ids());
                specificationParam.put("goodsId", goodsId);
                List<GoodsSpecificationVo> specificationEntities = goodsSpecificationService.queryList(specificationParam);
                goodsSepcifitionValue = new String[specificationEntities.size()];
                for (int i = 0; i < specificationEntities.size(); i++) {
                    goodsSepcifitionValue[i] = specificationEntities.get(i).getValue();
                }
            }
            cartInfo.setProduct_id(productId);
            cartInfo.setGoods_sn(productInfo.getGoods_sn());
            cartInfo.setNumber(number);
            cartInfo.setRetail_price(productInfo.getRetail_price());
            cartInfo.setMarket_price(productInfo.getRetail_price());
            if (null != goodsSepcifitionValue) {
                cartInfo.setGoods_specifition_name_value(StringUtils.join(goodsSepcifitionValue, ";"));
            }
            cartInfo.setGoods_specifition_ids(productInfo.getGoods_specification_ids());
            cartService.update(cartInfo);
        } else {
            //合并购物车已有的product信息，删除已有的数据
            Integer newNumber = number + newcartInfo.getNumber();
            if (null == productInfo || productInfo.getGoods_number() < newNumber) {
                return this.toResponsObject(400, "库存不足", "");
            }
            cartService.delete(newcartInfo.getId());
            //添加规格名和值
            String[] goodsSepcifitionValue = null;
            if (null != productInfo.getGoods_specification_ids()) {
                Map specificationParam = new HashMap();
                specificationParam.put("ids", productInfo.getGoods_specification_ids());
                specificationParam.put("goodsId", goodsId);
                List<GoodsSpecificationVo> specificationEntities = goodsSpecificationService.queryList(specificationParam);
                goodsSepcifitionValue = new String[specificationEntities.size()];
                for (int i = 0; i < specificationEntities.size(); i++) {
                    goodsSepcifitionValue[i] = specificationEntities.get(i).getValue();
                }
            }
            cartInfo.setProduct_id(productId);
            cartInfo.setGoods_sn(productInfo.getGoods_sn());
            cartInfo.setNumber(number);
            cartInfo.setRetail_price(productInfo.getRetail_price());
            cartInfo.setMarket_price(productInfo.getRetail_price());
            if (null != goodsSepcifitionValue) {
                cartInfo.setGoods_specifition_name_value(StringUtils.join(goodsSepcifitionValue, ";"));
            }
            cartInfo.setGoods_specifition_ids(productInfo.getGoods_specification_ids());
            cartService.update(cartInfo);
        }
        Map<String,Object> resultMap =getIndexMaps((Map<String, Object>)getCart(loginUser));
        //return toResponsSuccess(getCart(loginUser));
        return toResponsSuccess(resultMap);
    }

    /**
     * 是否选择商品，如果已经选择，则取消选择，批量操作
     */
    @ApiOperation(value = "是否选择商品")
    @PostMapping("checked")
    public Object checked(@LoginUser UserVo loginUser) {
        JSONObject jsonParam = getJsonRequest();
        String productIds = jsonParam.getString("productIds");
        Integer isChecked = jsonParam.getInteger("isChecked");
        if (StringUtils.isNullOrEmpty(productIds)) {
            return this.toResponsFail("删除出错");
        }
        String[] productIdArray = productIds.split(",");
        cartService.updateCheck(productIdArray, isChecked, loginUser.getUserId());
        Map<String,Object> resultMap =getIndexMaps((Map<String, Object>)getCart(loginUser));
        return toResponsSuccess(resultMap);
    }

    //删除选中的购物车商品，批量删除
    @ApiOperation(value = "删除商品")
    @PostMapping("delete")
    public Object delete(@LoginUser UserVo loginUser) {
        Long userId = loginUser.getUserId();

        JSONObject jsonObject = getJsonRequest();
        String productIds = jsonObject.getString("productIds");

        if (StringUtils.isNullOrEmpty(productIds)) {
            return toResponsFail("删除出错");
        }
        String[] productIdsArray = productIds.split(",");
        cartService.deleteByUserAndProductIds(userId, productIdsArray);

        Map<String,Object> resultMap =getIndexMaps((Map<String, Object>)getCart(loginUser));
        return toResponsSuccess(resultMap);
    }

    //  获取购物车商品的总件件数
    @ApiOperation(value = "获取购物车商品的总件件数")
    @IgnoreAuth
    @PostMapping("goodscount")
    public Object goodscount(@LoginUser UserVo loginUser) {
//        if (null == loginUser || null == loginUser.getUserId()) {
//            return toResponsFail("未登录");
//        }
        String token = request.getHeader("X-Nideshop-Token");
        if (org.apache.commons.lang.StringUtils.isBlank(token)) {
            token = request.getParameter("X-Nideshop-Token");
        }
        TokenEntity tokenEntity = tokenService.queryByToken(token);
        if (tokenEntity != null && tokenEntity.getExpireTime().getTime() >= System.currentTimeMillis()) {
            loginUser =  userService.queryObject(tokenEntity.getUserId());
        }
        Map<String, Object> resultObj = new HashMap();
        //查询列表数据
        Map param = new HashMap();
        param.put("user_id", loginUser.getUserId());
        List<CartVo> cartList = cartService.queryList(param);
        //获取购物车统计信息
        Integer goodsCount = 0;
        for (CartVo cartItem : cartList) {
            goodsCount += cartItem.getNumber();
        }
        resultObj.put("cartList", cartList);
        //
        Map<String, Object> cartTotal = new HashMap();
        cartTotal.put("goodsCount", goodsCount);

        resultObj.put("cartTotal", cartTotal);
        return toResponsSuccess(resultObj);
        //return toResponsSuccess(cartTotal);//获取购物车商品件数
    }

    /**
     * 订单提交前的检验和填写相关订单信息
     */
    @ApiOperation(value = "订单提交前的检验和填写相关订单信息")
    @PostMapping("checkout")
    public Object checkout(@LoginUser UserVo loginUser, Integer couponId, @RequestParam(defaultValue = "cart") String type,
                           @RequestParam(defaultValue = "addressId") Long addressId) {
        Map<String, Object> resultObj = new HashMap();
        //根据收货地址计算运费

        BigDecimal freightPrice = new BigDecimal(0.00);
        BigDecimal postageAmount = new BigDecimal(ResourceUtil.getConfigByName("postage.amount"));
        //默认收货地址
        Map param = new HashMap();
        param.put("user_id", loginUser.getUserId());

        if(addressId!=null&&!addressId.equals(0L)){
            param.put("id", addressId);
        }else {
            param.put("is_default", 1);
        }
        List<AddressVo> addressEntities = addressService.queryList(param);
        AddressVo addressVo= null;
        if (null == addressEntities || addressEntities.size() == 0) {
            resultObj.put("checkedAddress", new AddressVo());
        } else {
            resultObj.put("checkedAddress", addressEntities.get(0));
            addressVo = addressEntities.get(0);
        }
        // * 获取要购买的商品和总价
        ArrayList<CartVo> checkedGoodsList = new ArrayList();
        BigDecimal goodsTotalPrice;
        if (type.equals("cart")) {
            Map<String, Object> cartData = (Map<String, Object>) this.getCartOld(loginUser);


            for (CartVo cartEntity : (List<CartVo>) cartData.get("cartList")) {
                if (cartEntity.getChecked() == 1) {
                    checkedGoodsList.add(cartEntity);
                    //***************************************
                    BigDecimal subgoodsTotalPrice = null;
                    /*会员身份判断价格为零售价*/
                    BigDecimal resultPrice = apiMemberOrderService.getMemberPrice(loginUser,cartEntity.getMarket_price(),cartEntity.getRetail_price());
                    subgoodsTotalPrice = resultPrice.multiply(new BigDecimal(cartEntity.getNumber()));
                    //subgoodsTotalPrice = cartEntity.getRetail_price().multiply(new BigDecimal(cartEntity.getNumber()));
                    GoodsVo goods = goodsService.queryObject(cartEntity.getGoods_id());
                    if(goods.getAddTemplet()!=null&&goods.getTypeValue()!=null){
                        FreightVo freightEntity = apiFreightService.queryObject(goods.getAddTemplet());
                        BigDecimal goodsBig = goods.getTypeValue().multiply(new BigDecimal(cartEntity.getNumber()));
                        if(freightEntity!=null){
                            if(freightEntity.getIsDefined().equals(1)&&subgoodsTotalPrice.compareTo(freightEntity.getDefinedAmount())>=0){
                            }else {
                                if(freightEntity.getIsDefined().equals(0)&&subgoodsTotalPrice.compareTo(postageAmount)>=0){
                                }else {
                                    if(addressVo!=null){
                                        Map<String,Object> aparams = new LinkedHashMap<>();
                                        if (addressEntities!=null&&addressEntities.size()>0){
                                            aparams.put("cityId",addressVo.getCityId());
                                            aparams.put("provinceId",addressVo.getProvinceId());
                                            aparams.put("districtId",addressVo.getDistrictId());
                                            aparams.put("freightId",goods.getAddTemplet());

                                        }
                                        List<FreightSubVo> freightSubEntities = aparams.size()>0?apiFreightSubService.queryListForCun(aparams):null;//
                                        if(freightSubEntities!=null&&freightSubEntities.size()>0){
                                            FreightSubVo subEntity = freightSubEntities.get(0);
                                            if(goodsBig.compareTo(subEntity.getSubmaxunitLimit())>0){
//                                            freightPrice = freightPrice.add(subEntity.getSubperamountPlus().multiply(goodsBig.divide(subEntity.getSubmaxunitLimit(),0,BigDecimal.ROUND_CEILING)));
                                                freightPrice = freightPrice.add(subEntity.getSubmaxamountLimit().add(subEntity.getSubperamountPlus().multiply(goodsBig.subtract(subEntity.getSubmaxunitLimit()))));
                                                logger.info("1**********"+freightPrice);
                                            }else {
                                                freightPrice =   freightPrice.add(subEntity.getSubmaxamountLimit());
                                                logger.info("2**********"+freightPrice);

                                            }

                                        }else {
                                            if(goodsBig.compareTo(freightEntity.getMaxunitLimit())>0){
//                                            freightPrice = freightPrice.add(freightEntity.getPeramountPlus().multiply(goodsBig.divide(freightEntity.getMaxunitLimit(),0,BigDecimal.ROUND_CEILING)));
                                                freightPrice = freightPrice.add(freightEntity.getMaxamountLimit().add(freightEntity.getPeramountPlus().multiply(goodsBig.subtract(freightEntity.getMaxunitLimit()))));
                                                logger.info("3**********"+freightPrice);

                                            }else {
                                                freightPrice =  freightPrice.add(freightEntity.getMaxamountLimit());
                                                logger.info("4**********"+freightPrice);

                                            }
                                        }
                                    }else {
                                        //没有地址 0
                                    }

                                }

                            }
                        }
                    }

                    //***************************************
                }
            }
            goodsTotalPrice = (BigDecimal) ((HashMap) cartData.get("cartTotal")).get("checkedGoodsAmount");

//            freightPrice
        } else { // 是直接购买的
            BuyGoodsVo goodsVO = (BuyGoodsVo) J2CacheUtils.get(J2CacheUtils.SHOP_CACHE_NAME, "goods" + loginUser.getUserId() + "");
            ProductVo productInfo = productService.queryObject(goodsVO.getProductId());
            //计算订单的费用
            //商品总价
            /*会员身份判断价格为零售价*/
            BigDecimal resultPrice = apiMemberOrderService.getMemberPrice(loginUser,productInfo.getMarket_price(),productInfo.getRetail_price());
            goodsTotalPrice = resultPrice.multiply(new BigDecimal(goodsVO.getNumber()));
            //goodsTotalPrice = productInfo.getRetail_price().multiply(new BigDecimal(goodsVO.getNumber()));

            CartVo cartVo = new CartVo();
            cartVo.setGoods_name(productInfo.getGoods_name());
            cartVo.setNumber(goodsVO.getNumber());
            cartVo.setGoods_id(goodsVO.getGoodsId());
            cartVo.setRetail_price(productInfo.getRetail_price());
            cartVo.setList_pic_url(productInfo.getList_pic_url());
            cartVo.setMarket_price(productInfo.getMarket_price());
            checkedGoodsList.add(cartVo);

            //***************************************
            GoodsVo goods = goodsService.queryObject(goodsVO.getGoodsId());
            if(goods.getAddTemplet()!=null&&goods.getTypeValue()!=null){
                FreightVo freightEntity = apiFreightService.queryObject(goods.getAddTemplet());
                BigDecimal goodsBig = goods.getTypeValue().multiply(new BigDecimal(goodsVO.getNumber()));
                if(freightEntity!=null){
                    if(freightEntity.getIsDefined().equals(1)&&goodsTotalPrice.compareTo(freightEntity.getDefinedAmount())>=0){
//                freightPrice.add(freightEntity.getMaxamountLimit());
                    }else {
                        if(freightEntity.getIsDefined().equals(0)&&goodsTotalPrice.compareTo(postageAmount)>=0){
//                    freightPrice.add(postageAmount);

                        }else {
                            if(addressVo!=null){
                                Map<String,Object> aparams = new LinkedHashMap<>();
                                if (addressEntities!=null&&addressEntities.size()>0){
                                    aparams.put("cityId",addressVo.getCityId());
                                    aparams.put("provinceId",addressVo.getProvinceId());
                                    aparams.put("districtId",addressVo.getDistrictId());
                                    aparams.put("freightId",goods.getAddTemplet());
                                }
                                List<FreightSubVo> freightSubEntities = aparams.size()>0?apiFreightSubService.queryListForCun(aparams):null;//
                                if(freightSubEntities!=null&&freightSubEntities.size()>0){
                                    FreightSubVo subEntity = freightSubEntities.get(0);
                                    if(goodsBig.compareTo(subEntity.getSubmaxunitLimit())>0){
//                                    freightPrice = freightPrice.add(subEntity.getSubperamountPlus().multiply(goodsBig.divide(subEntity.getSubmaxunitLimit(),0,BigDecimal.ROUND_CEILING)));
                                        freightPrice = freightPrice.add(subEntity.getSubmaxamountLimit().add(subEntity.getSubperamountPlus().multiply(goodsBig.subtract(subEntity.getSubmaxunitLimit()))));
                                        logger.info("1**********"+freightPrice);

                                    }else {
                                        freightPrice =   freightPrice.add(subEntity.getSubmaxamountLimit());
                                        logger.info("2**********"+freightPrice);

                                    }

                                }else {
                                    if(goodsBig.compareTo(freightEntity.getMaxunitLimit())>0){
//                                    freightPrice = freightPrice.add(freightEntity.getPeramountPlus().multiply(goodsBig.divide(freightEntity.getMaxunitLimit(),0,BigDecimal.ROUND_CEILING)));
                                        freightPrice = freightPrice.add(freightEntity.getMaxamountLimit().add(freightEntity.getPeramountPlus().multiply(goodsBig.subtract(freightEntity.getMaxunitLimit()))));
                                        logger.info("3**********"+freightPrice);
                                    }else {
                                        freightPrice = freightPrice.add(freightEntity.getMaxamountLimit());
                                        logger.info("4**********"+freightPrice);

                                    }
                                }
                            }else {
                                //没有地址 默认邮费0
                            }

                        }

                    }
                }
            }



            //***************************************
        }


        //获取可用的优惠券信息
        BigDecimal couponPrice = new BigDecimal(0.00);
        if (couponId != null && couponId != 0) {
            CouponVo couponVo = apiCouponMapper.getUserCoupon(couponId);
            if (couponVo != null) {
                couponPrice = couponVo.getType_money();
            }
        }

        //订单的总价
        BigDecimal orderTotalPrice = goodsTotalPrice.add(freightPrice);

        //
        BigDecimal actualPrice = orderTotalPrice.subtract(couponPrice);  //减去其它支付的金额后，要实际支付的金额

        resultObj.put("freightPrice", freightPrice);

        resultObj.put("couponPrice", couponPrice);

        HashMap<Integer,Map> brandVoHashMap = new HashMap<>();
        checkedGoodsList.forEach((entity)->{
            GoodsVo goodsVo = goodsService.queryObject(entity.getGoods_id());
            if(!brandVoHashMap.containsKey(goodsVo.getBrand_id())){
                Map<String,Object> map = new HashMap<>();
                map.put("id",goodsVo.getBrand_id());
                BrandVo brandVo = brandService.queryObject(goodsVo.getBrand_id());
                if(brandVo==null){
                    map.put("title","");
                    map.put("app_list_pic_url","");
                }else {
                    map.put("title",brandVo.getName());
                    map.put("app_list_pic_url",brandVo.getApp_list_pic_url());
                }
                List<CartVo> cartVos = new ArrayList<>();
                cartVos.add(entity);
                map.put("list",cartVos);
                map.put("checked",entity.getChecked());
                brandVoHashMap.put(goodsVo.getBrand_id(),map);
            }else {
                ((List)(brandVoHashMap.get(goodsVo.getBrand_id()).get("list"))).add(entity);
                brandVoHashMap.get(goodsVo.getBrand_id()).put("checked",entity.getChecked());
            }
        });

        List result = new LinkedList();
        for (Map.Entry<Integer,Map> entry:brandVoHashMap.entrySet()){
            result.add(entry.getValue());
        }

        resultObj.put("checkedGoodsList", result);
        resultObj.put("goodsTotalPrice", goodsTotalPrice);
        resultObj.put("orderTotalPrice", orderTotalPrice);
        resultObj.put("actualPrice", actualPrice);
        return toResponsSuccess(resultObj);
    }

    /*优化接口调用封装方法*/
    private Map<String,Object> getAddresInfo(AddressVo vo){
        Map<String,Object> map = new HashMap<>();
        map.put("id",vo.getId());
        map.put("detailInfo",vo.getDetailInfo());
        map.put("full_region",vo.getFull_region());
        return map;
    }

    /**
     * 选择优惠券列表
     */
    @ApiOperation(value = "选择优惠券列表")
    @PostMapping("checkedCouponList")
    public Object checkedCouponList(@LoginUser UserVo loginUser) {
        //
        Map param = new HashMap();
        param.put("user_id", loginUser.getUserId());
        List<CouponVo> couponVos = apiCouponService.queryUserCouponList(param);
        if (null != couponVos && couponVos.size() > 0) {
            // 获取要购买的商品
            Map<String, Object> cartData = (Map<String, Object>) this.getCart(loginUser);
            List<CartVo> checkedGoodsList = new ArrayList();
            List<Integer> checkedGoodsIds = new ArrayList();
            for (CartVo cartEntity : (List<CartVo>) cartData.get("cartList")) {
                if (cartEntity.getChecked() == 1) {
                    checkedGoodsList.add(cartEntity);
                    checkedGoodsIds.add(cartEntity.getId());
                }
            }
            // 计算订单的费用
            BigDecimal goodsTotalPrice = (BigDecimal) ((HashMap) cartData.get("cartTotal")).get("checkedGoodsAmount");  //商品总价
            // 如果没有用户优惠券直接返回新用户优惠券
            for (CouponVo couponVo : couponVos) {
                if (couponVo.getMin_amount().compareTo(goodsTotalPrice) <= 0) {
                    couponVo.setEnabled(1);
                }
            }
        }
        return toResponsSuccess(couponVos);
    }
}
