package com.platform.service;

import java.math.BigDecimal;
import java.util.*;

import com.platform.entity.*;
import com.platform.utils.ResourceUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.platform.cache.J2CacheUtils;
import com.platform.dao.ApiAddressMapper;
import com.platform.dao.ApiCartMapper;
import com.platform.dao.ApiCouponMapper;
import com.platform.dao.ApiOrderGoodsMapper;
import com.platform.dao.ApiOrderMapper;
import com.platform.util.CommonUtil;


@Service
public class ApiOrderService {
    protected Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ApiOrderMapper orderDao;
    @Autowired
    private ApiAddressMapper apiAddressMapper;
    @Autowired
    private ApiCartMapper apiCartMapper;
    @Autowired
    private ApiCouponMapper apiCouponMapper;
    @Autowired
    private ApiOrderMapper apiOrderMapper;
    @Autowired
    private ApiOrderGoodsMapper apiOrderGoodsMapper;
    @Autowired
    private ApiProductService productService;
    @Autowired
    private ApiGoodsService goodsService;
    @Autowired
    private ApiFreightService freightService;
    @Autowired
    private ApiFreightSubService freightSubService;


    public OrderVo queryObject(Integer id) {
        return orderDao.queryObject(id);
    }


    public List<OrderVo> queryList(Map<String, Object> map) {
        return orderDao.queryList(map);
    }


    public int queryTotal(Map<String, Object> map) {
        return orderDao.queryTotal(map);
    }


    public void save(OrderVo order) {
        orderDao.save(order);
    }


    public int update(OrderVo order) {
        return orderDao.update(order);
    }


    public void delete(Integer id) {
        orderDao.delete(id);
    }


    public void deleteBatch(Integer[] ids) {
        orderDao.deleteBatch(ids);
    }

    public List<MyOrderDetailsVo> queryMyList(Map<String,Object> map) {return orderDao.queryMyList(map);}

    public List<MyOrderDetailsVo> queryMySubList(Map<String,Object> map) {return orderDao.queryMySubList(map);}


    @Transactional
    public Map<String, Object> submit(JSONObject jsonParam, UserVo loginUser) throws Exception {
        Map<String, Object> resultObj = new HashMap<String, Object>();

        Integer couponId = jsonParam.getInteger("couponId");
        String type = jsonParam.getString("type");
        String postscript = jsonParam.getString("postscript");
//        AddressVo addressVo = jsonParam.getObject("checkedAddress",AddressVo.class);
        AddressVo addressVo = apiAddressMapper.queryObject(jsonParam.getInteger("addressId"));

        BigDecimal postageAmount = new BigDecimal(ResourceUtil.getConfigByName("postage.amount"));

        BigDecimal freightPrice = new BigDecimal(0.00);

        // * 获取要购买的商品
        List<CartVo> checkedGoodsList = new ArrayList<>();
        BigDecimal goodsTotalPrice;
        Integer sellerId = 0;
        if (type.equals("cart")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("user_id", loginUser.getUserId());
            param.put("session_id", 1);
            param.put("checked", 1);
            checkedGoodsList = apiCartMapper.queryList(param);
            if (null == checkedGoodsList) {
                resultObj.put("errno", 400);
                resultObj.put("errmsg", "请选择商品");
                return resultObj;
            }
            //统计商品总价
            goodsTotalPrice = new BigDecimal(0.00);
            for (CartVo cartEntity : checkedGoodsList) {
                /*会员身份判断价格为零售价*/
                BigDecimal resultPrice = loginUser.getIs_member()==1?cartEntity.getRetail_price():cartEntity.getMarket_price();
                goodsTotalPrice = goodsTotalPrice.add(resultPrice.multiply(new BigDecimal(cartEntity.getNumber())));
                //goodsTotalPrice = goodsTotalPrice.add(cartEntity.getRetail_price().multiply(new BigDecimal(cartEntity.getNumber())));
                //***************************************
                BigDecimal subgoodsTotalPrice = null;
                subgoodsTotalPrice = resultPrice.multiply(new BigDecimal(cartEntity.getNumber()));
                //subgoodsTotalPrice = cartEntity.getRetail_price().multiply(new BigDecimal(cartEntity.getNumber()));
                freightPrice = freightPrice.add(getPostageAmount(addressVo,cartEntity,subgoodsTotalPrice));
                if(0!=cartEntity.getSellerId()){
                    sellerId = cartEntity.getSellerId();
                }
                //***************************************
            }
//            freightPrice
        } else {
            BuyGoodsVo goodsVo = (BuyGoodsVo) J2CacheUtils.get(J2CacheUtils.SHOP_CACHE_NAME, "goods" + loginUser.getUserId());
            ProductVo productInfo = productService.queryObject(goodsVo.getProductId());
            //计算订单的费用
            //商品总价
            /*会员身份判断价格为零售价*/
            BigDecimal resultPrice = loginUser.getIs_member()==1?productInfo.getRetail_price():productInfo.getMarket_price();
            goodsTotalPrice = resultPrice.multiply(new BigDecimal(goodsVo.getNumber()));
            //goodsTotalPrice = productInfo.getRetail_price().multiply(new BigDecimal(goodsVo.getNumber()));

            CartVo cartVo = new CartVo();
            BeanUtils.copyProperties(productInfo, cartVo);
            cartVo.setNumber(goodsVo.getNumber());
            cartVo.setGoods_id(goodsVo.getGoodsId());
            cartVo.setProduct_id(goodsVo.getProductId());
            checkedGoodsList.add(cartVo);



            /**************************************/
            freightPrice = freightPrice.add(getPostageAmount(addressVo,cartVo,goodsTotalPrice));


            /**************************************/
            GoodsVo goodsVoTemp = goodsService.queryObject(goodsVo.getGoodsId());//goodsVo.getGoodsId()
            if (null!=goodsVoTemp){
                sellerId = goodsVoTemp.getSellerId();
            }
        }


        //获取订单使用的优惠券
        BigDecimal couponPrice = new BigDecimal(0.00);
        CouponVo couponVo = null;
        if (couponId != null && couponId != 0) {
            couponVo = apiCouponMapper.getUserCoupon(couponId);
            if (couponVo != null && couponVo.getCoupon_status() == 1) {
                couponPrice = couponVo.getType_money();
            }
        }

        //订单价格计算
        BigDecimal orderTotalPrice = goodsTotalPrice.add(freightPrice); //订单的总价

        BigDecimal actualPrice = orderTotalPrice.subtract(couponPrice);  //减去其它支付的金额后，要实际支付的金额

        Long currentTime = System.currentTimeMillis() / 1000;

        //
        OrderVo orderInfo = new OrderVo();
        orderInfo.setSellerId(sellerId);
        orderInfo.setOrder_sn(CommonUtil.generateOrderNumber());
        orderInfo.setUser_id(loginUser.getUserId());
        //收货地址和运费
        orderInfo.setConsignee(addressVo.getUserName());
        orderInfo.setMobile(addressVo.getTelNumber());
        orderInfo.setCountry(addressVo.getNationalCode());
        orderInfo.setProvince(addressVo.getProvinceName());
        orderInfo.setCity(addressVo.getCityName());
        orderInfo.setDistrict(addressVo.getCountyName());
        orderInfo.setAddress(addressVo.getDetailInfo());
        //运费
        orderInfo.setFreight_price(freightPrice.intValue());
        //留言
        orderInfo.setPostscript(postscript);
        //使用的优惠券
        orderInfo.setCoupon_id(couponId);
        orderInfo.setCoupon_price(couponPrice);
        orderInfo.setAdd_time(new Date());
        orderInfo.setGoods_price(goodsTotalPrice);
        orderInfo.setOrder_price(orderTotalPrice);
        orderInfo.setActual_price(actualPrice);
        // 待付款
        orderInfo.setOrder_status(0);
        orderInfo.setShipping_status(0);
        orderInfo.setPay_status(0);
        orderInfo.setShipping_id(0);
        orderInfo.setShipping_fee(new BigDecimal(0));
        orderInfo.setIntegral(0);
        orderInfo.setIntegral_money(new BigDecimal(0));
        if (type.equals("cart")) {
            orderInfo.setOrder_type("1");
        } else {
            orderInfo.setOrder_type("4");
        }

        //开启事务，插入订单信息和订单商品
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY,1);
        orderInfo.setExpire_time(calendar.getTime());
        apiOrderMapper.save(orderInfo);
        if (null == orderInfo.getId()) {
            resultObj.put("errno", 1);
            resultObj.put("errmsg", "订单提交失败");
            return resultObj;
        }
        //统计商品总价
        List<OrderGoodsVo> orderGoodsData = new ArrayList<OrderGoodsVo>();
        int pnum = 0;
        int gnum = 0;
        int member = loginUser.getIs_member()==null?0:loginUser.getIs_member();
        for (CartVo goodsItem : checkedGoodsList) {
            OrderGoodsVo orderGoodsVo = new OrderGoodsVo();
            orderGoodsVo.setOrder_id(orderInfo.getId());
            orderGoodsVo.setGoods_id(goodsItem.getGoods_id());
            orderGoodsVo.setGoods_sn(goodsItem.getGoods_sn());
            orderGoodsVo.setProduct_id(goodsItem.getProduct_id());
            orderGoodsVo.setGoods_name(goodsItem.getGoods_name());
            orderGoodsVo.setList_pic_url(goodsItem.getList_pic_url());
            orderGoodsVo.setMarket_price(goodsItem.getMarket_price());
            orderGoodsVo.setRetail_price(goodsItem.getRetail_price());
            orderGoodsVo.setNumber(goodsItem.getNumber());
            orderGoodsVo.setGoods_specifition_name_value(goodsItem.getGoods_specifition_name_value());
            orderGoodsVo.setGoods_specifition_ids(goodsItem.getGoods_specifition_ids());
            /*会员身份判断价格为零售价否则为市场价*/
            BigDecimal orderPrice = member==1?goodsItem.getRetail_price():goodsItem.getMarket_price();
            orderGoodsVo.setOrder_price(orderPrice);
            orderGoodsData.add(orderGoodsVo);
            Map productParams = new HashMap();
            productParams.put("id",goodsItem.getProduct_id());
            productParams.put("num",goodsItem.getNumber());
            pnum = productService.updateSubNum(productParams);
            if(pnum<1){
                throw new RuntimeException("库存不足，提交订单失败！");
            }
            Map goodsParams = new HashMap();
            goodsParams.put("id",goodsItem.getGoods_id());
            goodsParams.put("num",goodsItem.getNumber());
            gnum = goodsService.updateAddNum(goodsParams);
            if(gnum<1){
                throw new RuntimeException("库存不足，提交订单失败！");
            }
            apiOrderGoodsMapper.save(orderGoodsVo);

        }

        //清空已购买的商品
        apiCartMapper.deleteByCart(loginUser.getUserId(), 1, 1);
        resultObj.put("errno", 0);
        resultObj.put("errmsg", "订单提交成功");
        //
        //Map<String, OrderVo> orderInfoMap = new HashMap<String, OrderVo>();
        //orderInfoMap.put("orderInfo", orderInfo);
        /*优化接口只返回ID`*/
        Map<String, Object> orderInfoMap = new HashMap<String, Object>();
        orderInfoMap.put("orderInfo", getOrderVo(orderInfo));
        //
        resultObj.put("data", orderInfoMap);
        // 优惠券标记已用
        if (couponVo != null && couponVo.getCoupon_status() == 1) {
            couponVo.setCoupon_status(2);
            apiCouponMapper.updateUserCoupon(couponVo);
        }

        return resultObj;
    }

    /*优化接口调用封装方法*/
    private Map<String,Object> getOrderVo(OrderVo vo){
        Map<String,Object> map = new HashMap<>();
        map.put("id",vo.getId());
        return map;
    }

    /*生成订单简易方法，不使用购物车，收货发货及优惠券，不需验证登录*/
    @Transactional
    public OrderVo submit(GoodsVo goodsVo,Integer num,long userId) {
        BigDecimal goodsTotalPrice;//全部商品价格
        Integer freightPrice = 0;
        //流程步骤1、获取要购买的商品,并根据产品id获取到产品实例对象
        //BigDecimal market_price = goodsVo.getMarket_price();
        BigDecimal market_price = goodsVo.getRetail_price();
        Map<String,Object> productMap = new HashMap<>();
        productMap.put("goods_id",goodsVo.getId());
        List<ProductVo> productVoList = productService.queryList(productMap);
        Integer product_id = productVoList.size()>0?productVoList.get(0).getId():0;
        //流程2、计算商品总价
        goodsTotalPrice = market_price.multiply(BigDecimal.valueOf(num));
        //订单价格计算
        BigDecimal orderTotalPrice = goodsTotalPrice.add(new BigDecimal(freightPrice)); //订单的总价
        OrderVo orderInfo = new OrderVo();
        orderInfo.setOrder_sn(CommonUtil.generateOrderNumber());
        orderInfo.setUser_id(userId);
        orderInfo.setFreight_price(freightPrice);
        orderInfo.setAdd_time(new Date());
        orderInfo.setGoods_price(goodsTotalPrice);
        orderInfo.setOrder_price(orderTotalPrice);
        orderInfo.setActual_price(orderTotalPrice);
        // 待付款
        orderInfo.setOrder_status(0);
        orderInfo.setShipping_status(0);
        orderInfo.setPay_status(0);
        orderInfo.setShipping_id(0);
        orderInfo.setShipping_fee(new BigDecimal(0));
        orderInfo.setIntegral(0);
        orderInfo.setIntegral_money(new BigDecimal(0));
        orderInfo.setOrder_type("4");

        //开启事务，插入订单信息和订单商品
        apiOrderMapper.save(orderInfo);

        //统计商品总价
        List<OrderGoodsVo> orderGoodsData = new ArrayList<OrderGoodsVo>();
        OrderGoodsVo orderGoodsVo = new OrderGoodsVo();
        orderGoodsVo.setOrder_id(orderInfo.getId());
        orderGoodsVo.setGoods_id(goodsVo.getId());
        orderGoodsVo.setGoods_sn(goodsVo.getGoods_sn());
        orderGoodsVo.setGoods_name(goodsVo.getName());
        orderGoodsVo.setList_pic_url(goodsVo.getList_pic_url());
        orderGoodsVo.setMarket_price(goodsVo.getMarket_price());
        orderGoodsVo.setRetail_price(goodsVo.getRetail_price());
        orderGoodsVo.setNumber(num);
        orderGoodsVo.setProduct_id(product_id);
        orderGoodsData.add(orderGoodsVo);
        apiOrderGoodsMapper.save(orderGoodsVo);

        return orderInfo;
    }

    public BigDecimal getPostageAmount(AddressVo addressVo,CartVo cartVo,BigDecimal goodsTotalPrice){

        BigDecimal freightPrice = new BigDecimal(0.00);
        BigDecimal postageAmount = new BigDecimal(ResourceUtil.getConfigByName("postage.amount"));
        GoodsVo goods = goodsService.queryObject(cartVo.getGoods_id());
        if(goods.getAddTemplet()!=null&&goods.getTypeValue()!=null){
            FreightVo freightEntity = freightService.queryObject(goods.getAddTemplet());
            BigDecimal goodsBig = goods.getTypeValue().multiply(new BigDecimal(cartVo.getNumber()));
            if(freightEntity!=null){
                if(freightEntity.getIsDefined().equals(1)&&goodsTotalPrice.compareTo(freightEntity.getDefinedAmount())>=0){
//                freightPrice.add(freightEntity.getMaxamountLimit());
                }else {
                    if(freightEntity.getIsDefined().equals(0)&&goodsTotalPrice.compareTo(postageAmount)>=0){
//                    freightPrice.add(postageAmount);

                    }else {
                        if(addressVo!=null){
                            Map<String,Object> aparams = new LinkedHashMap<>();
//                        if (addressEntities!=null&&addressEntities.size()>0){
                            aparams.put("cityId",addressVo.getCityId());
                            aparams.put("provinceId",addressVo.getProvinceId());
                            aparams.put("districtId",addressVo.getDistrictId());
                            aparams.put("freightId",goods.getAddTemplet());
//                        }
                            List<FreightSubVo> freightSubEntities = aparams.size()>0?freightSubService.queryListForCun(aparams):null;//
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
        return freightPrice;
    }

}