package com.platform.api;

import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.IgnoreAuth;
import com.platform.annotation.LoginUser;
import com.platform.entity.*;
import com.platform.service.*;
import com.platform.util.ApiBaseAction;
import com.platform.utils.*;
import com.qiniu.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.List;

/**
 * 作者: @author LuX <br>
 * 时间: 2018-11-06 <br>
 * 描述: ApiGoodsTradeController <br>
 */
@Api(tags = "商品展示交易")
@RestController
@RequestMapping("/api/trade")
public class ApiGoodsTradeController extends ApiBaseAction {
    @Autowired
    private ApiGoodsService goodsService;
    @Autowired
    private ApiOrderService orderService;
    @Autowired
    private ApiCategoryService categoryService;
    @Autowired
    private ApiAddressService addressService;
    @Autowired
    private ApiUserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ApiGoodsGalleryService goodsGalleryService;
    /*微信web应用APPID*/
    private static final String appid = "wxbb275edbe18e09c5";
    /*微信web应用APPSCRETE密钥*/
    private static final String secret = "75831e57bbda472ca4afc0b5e6cabf2a";
    /*TOKEN失败CODE值99*/
    private static final int token_fail_code = 99;

    @ApiOperation(value = "商品列表展示")
    @IgnoreAuth
    @PostMapping(value = "list")
    public Object list() {
        Map param = new HashMap();
        param.put("is_delete", 0);//过滤条件，筛选未删除的
        param.put("is_on_sale", 1);//过滤条件，筛选正在出售的
        try {
            JSONObject jsonParam = super.getJsonRequestByParameterMap();
            int result = jsonParam.getIntValue("size");//需要展示的商品数量
            int page = 0;//需要展示的商品页数
            if(jsonParam.get("page")!=null) page = jsonParam.getIntValue("page");
            int offset = page>0?(page-1)*result:0;
            int is_hot = jsonParam.getIntValue("is_hot");//是否最热，0：否，1：是，2：全部
            int is_new = jsonParam.getIntValue("is_new");//是否最新，0：否，1：是，2：全部
            String categoryId = jsonParam.getString("categoryId");//类别ID
            param.put("category_id", categoryId);//过滤条件，传入类别ID
            if (is_hot < 2) param.put("is_hot", is_hot);//最热过滤
            param.put("is_on_sale", 1);//获取正在上架的商品
            if (is_new < 2) param.put("is_new", is_new);//最新过滤
            param.put("offset",offset);//分页的页数
            param.put("limit",result);//分页展示的商品数量

            List<GoodsVo> goodsList = null;
            if (result == -1) {
                goodsList = goodsService.queryList(param);
            } else {
                List<GoodsVo> goodssubList = goodsService.queryList(param);
                if (result > goodssubList.size()) {
                    goodsList = goodsService.queryList(param);
                } else {
                    goodsList = goodsService.queryList(param).subList(0, result);
                }
            }
            List<Map<String, Object>> resultLists = new ArrayList<Map<String, Object>>();
            for (GoodsVo model : goodsList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", model.getId());
                map.put("name", model.getName());
                //map.put("goods_brief", model.getGoods_brief());
                map.put("list_pic_url", model.getList_pic_url());
                map.put("market_price", model.getMarket_price());
                map.put("retail_price", model.getRetail_price());
                resultLists.add(map);
            }
            return toResponsSuccess(resultLists);
        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }
    @ApiOperation(value = "商品详情展示")
    @IgnoreAuth
    @PostMapping(value = "info")
    public Object info() {
        JSONObject jsonParam = super.getJsonRequestByParameterMap();
        try {
            int result = jsonParam.getIntValue("id");//获取商品ID
            GoodsVo goods = goodsService.queryObject(result);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", goods.getId());
            map.put("name", goods.getName());
            map.put("goods_brief", goods.getGoods_brief());
            map.put("goods_desc", goods.getGoods_desc());
            map.put("list_pic_url", goods.getList_pic_url());
            map.put("market_price", goods.getMarket_price());
            map.put("retail_price", goods.getRetail_price());
            return toResponsSuccess(map);
        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }

    }
    @ApiOperation(value = "展示所有的商品类别")
    @IgnoreAuth
    @GetMapping(value = "category")
    public Object category() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("parent_id", 0);//根类别
            map.put("level", "L1");//第一级别
            map.put("is_show", 1);//需要展示的类别
            List<CategoryVo> categoryVoList = categoryService.queryList(map);
            List<Map<String, Object>> resultLists = new ArrayList<>();
            for (CategoryVo categoryVo : categoryVoList) {
                Map<String, Object> resultMap = new HashMap<>();
                if (categoryVo.getLevel() != null) {
                    resultMap.put("id", categoryVo.getId());//类别ID
                    resultMap.put("name", categoryVo.getName());//类别名称
                    //resultMap.put("front_desc", categoryVo.getFront_desc());//类别描述
                    //resultMap.put("banner_url", categoryVo.getBanner_url());//类别封面图片
                    resultLists.add(resultMap);
                }
            }
            return toResponsSuccess(resultLists);
        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }
    @ApiOperation(value = "生成订单")
    @IgnoreAuth
    @PostMapping(value = "order")
    public Object order() {
        JSONObject jsonPara = super.getJsonRequestByParameterMap();
        String token = jsonPara.getString("token");
        TokenEntity tokenEntity = tokenService.queryByToken(token);
        if(tokenEntity==null){
            return  toResponsObject(token_fail_code,"Token非法","");
        }
        long userId = tokenEntity.getUserId();
        try {
            int id = jsonPara.getIntValue("id");//获取商品ID
            Integer number = jsonPara.getInteger("num");//获取商品ID数量
            GoodsVo goods = goodsService.queryObject(id);
            if (goods == null) {
                return toResponsFail("未找到对应商品请联系客服");
            }
            OrderVo model = orderService.submit(goods, number,userId);//调用生成订单接口返回订单数据
            Map<String, Object> map = new HashMap<>();
            map.put("order_id", model.getId());//订单ID
            map.put("order_sn", model.getOrder_sn());//订单编号
            map.put("order_price", model.getOrder_price());//订单金额
            map.put("add_time", model.getAdd_time());//下单时间
            map.put("order_status_text", model.getOrder_status_text());//订单状态
            map.put("goods_id", goods.getId());//商品ID
            map.put("goods_num", number);//商品数量
            map.put("goods_name", goods.getName());//商品名称
            map.put("goods_price", goods.getRetail_price());//商品单价金额
            map.put("goods_img", goods.getList_pic_url());//商品封面
            map.put("goods_brief", goods.getGoods_brief());//商品简述
            return toResponsSuccess(map);
        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }

    @ApiOperation(value = "收货地址保存接口")
    @IgnoreAuth
    @Transactional
    @PostMapping(value = "save_address")
    public Object saveAddress() {
        try {
            JSONObject addressJson = super.getJsonRequestByParameterMap();
            String token = addressJson.getString("token");
            TokenEntity tokenEntity = tokenService.queryByToken(token);
            if(tokenEntity==null){
              return  toResponsObject(token_fail_code,"Token非法","");
            }
            long userId = tokenEntity.getUserId();
            AddressVo entity = new AddressVo();
            if (null != addressJson) {
                entity.setId(addressJson.getLong("id"));
                entity.setUserId(userId);
                entity.setUserName(addressJson.getString("userName"));
                entity.setPostalCode(addressJson.getString("postalCode"));
                entity.setDetailInfo(addressJson.getString("detailInfo"));
                entity.setTelNumber(addressJson.getString("telNumber"));
                entity.setIs_default(addressJson.getBooleanValue("is_default") == true ? 1 : 0);
            }
            if (null == entity.getId() || entity.getId() == 0) {
                entity.setId(null);
                if(CheckIsDefault(entity)){
                    return  toResponsObject(1,"已有默认地址","");
                }
                addressService.save(entity);
            } else {
                /*如果设置默认地址，则取消原有默认地址*/
                if (entity.getIs_default() == 1) {
                    Map<String, Object> maps = new HashMap<>();
                    maps.put("is_default", 1);
                    maps.put("user_id", entity.getUserId());
                    List<AddressVo> voList = addressService.queryList(maps);
                    if (voList.size() > 0) {
                        voList.get(0).setIs_default(0);
                        addressService.update(voList.get(0));
                    }
                }
                addressService.update(entity);
            }
            Map<String, Object> map = new HashMap<>();
            return toResponsSuccess(map);
        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }
    /**检查是否有默认地址
     * */
    private boolean CheckIsDefault(AddressVo entity){
            boolean result =  false;
            Map<String, Object> maps = new HashMap<>();
            maps.put("is_default", 1);
            maps.put("user_id", entity.getUserId());
            List<AddressVo> voList = addressService.queryList(maps);
            if (voList.size() > 0) {
                if(entity.getIs_default()==1){
                    result = true;
                }
            }
            return result;
    }
    /**
     * 获取用户的收货地址
     */
    @ApiOperation(value = "获取用户的收货地址接口")
    @IgnoreAuth
    @PostMapping("get_address")
    public Object getAddress() {
        try {
            JSONObject addressJson = super.getJsonRequestByParameterMap();
            String token = addressJson.getString("token");
            TokenEntity tokenEntity = tokenService.queryByToken(token);
            if(tokenEntity==null){
                return  toResponsObject(token_fail_code,"Token非法","");
            }
            long userId = tokenEntity.getUserId();
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("user_id", userId);
            List<AddressVo> addressEntities = addressService.queryList(param);
            List<Map<String, Object>> lists = new ArrayList<>();
            for (AddressVo addressVo : addressEntities) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", addressVo.getId());
                map.put("userName", addressVo.getUserName() == null ? "" : addressVo.getUserName());
                map.put("telNumber", addressVo.getTelNumber() == null ? "" : addressVo.getTelNumber());
                map.put("postalCode", addressVo.getPostalCode() == null ? "" : addressVo.getPostalCode());
                map.put("detailInfo", addressVo.getDetailInfo());
                map.put("is_default", addressVo.getIs_default());
                lists.add(map);
            }
            return toResponsSuccess(lists);
        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }
    @ApiOperation(value = "删除用户的收货接口")
    @IgnoreAuth
    @PostMapping("delete_address")
    public Object deleteAddress() {
        try {
            JSONObject addressJson = super.getJsonRequestByParameterMap();
            String token = addressJson.getString("token");
            TokenEntity tokenEntity = tokenService.queryByToken(token);
            if(tokenEntity==null){
                return  toResponsObject(token_fail_code,"Token非法","");
            }
            Integer id = addressJson.getInteger("id");
            AddressVo vo = addressService.queryObject(id);
            if (vo != null) {
                addressService.delete(id);
            }
            Map<String, Object> map = new HashMap<>();
            return toResponsSuccess(map);
        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }
    @ApiOperation(value = "扫码登录")
    @IgnoreAuth
    @GetMapping("login_by_weixin")
    public Object loginByWeixin() {
        JSONObject jsonParam = super.getJsonRequestByParameterMap();
        String code = "";
        if (!StringUtils.isNullOrEmpty(jsonParam.getString("code"))) {
            code = jsonParam.getString("code");
        }
        Map<String, Object> resultObj = new HashMap<String, Object>();
        Map<String, String> parame = new HashMap<>();
        parame.put("code",code);
        parame.put("appid",appid);
        parame.put("secret",secret);
        parame.put("grant_type","authorization_code");
        Map<String,Object> access_map = (Map<String,Object>)JSONObject.parse(HttpUtil.URLGet("https://api.weixin.qq.com/sns/oauth2/access_token", parame, "UTF-8"));//获取accesstoken
        String access_token =  access_map.get("access_token").toString();
        String openid =  access_map.get("openid").toString();
        Date nowTime = new Date();
        UserVo userVo = userService.queryByOpenId(openid);
        if (null != userVo) {
            userVo.setLast_login_ip(this.getClientIp());
            userVo.setLast_login_time(nowTime);
            userService.update(userVo);
        } else {
            Map<String,String> user_map = new HashMap<>();
            user_map.put("access_token",access_token);
            user_map.put("openid",openid);
            user_map.put("remark","");
            Map<String,Object> userinfo = (Map<String,Object>)JSONObject.parse(HttpUtil.URLGet("https://api.weixin.qq.com/sns/userinfo", user_map, "UTF-8"));
            userVo = new UserVo();
            userVo.setWeixin_openid(openid);
            userVo.setAvatar(userinfo.get("headimgurl").toString());
            userVo.setGender(Integer.getInteger(userinfo.get("sex").toString())); // //性别 0：未知、1：男、2：女
            userVo.setNickname(userinfo.get("nickname").toString());
            userVo.setUsername("微信用户" + CharUtil.getRandomString(12));
            userVo.setPassword(openid);
            userVo.setRegister_time(nowTime);
            userVo.setRegister_ip(this.getClientIp());
            userVo.setLast_login_ip(userVo.getRegister_ip());
            userVo.setLast_login_time(userVo.getRegister_time());
            userService.save(userVo);
        }
        Map<String, Object> tokenMap = tokenService.createToken(userVo.getUserId());
        String token = MapUtils.getString(tokenMap, "token");
        if ( StringUtils.isNullOrEmpty(token)) {
            return toResponsFail("登录失败");
        }
        resultObj.put("token", token);
        resultObj.put("userId", userVo.getUserId());
        return toResponsSuccess(resultObj);
    }

    @ApiOperation(value = "商品详情轮播图接口")
    @IgnoreAuth
    @PostMapping(value = "info_gallery")
    public Object infoGallery() {
        JSONObject jsonParam = super.getJsonRequestByParameterMap();
        try {
            int id = jsonParam.getIntValue("id");//获取商品ID
            Map<String,Object> map = new HashMap<>();
            map.put("goods_id",id);
            List<GoodsGalleryVo> goodsGalleryVoList = goodsGalleryService.queryList(map);
            return toResponsSuccess(goodsGalleryVoList);
        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }

    }

}
