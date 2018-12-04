package com.platform.api;

import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.IgnoreAuth;
import com.platform.annotation.LoginUser;
import com.platform.entity.AddressVo;
import com.platform.entity.UserVo;
import com.platform.service.ApiAddressService;
import com.platform.util.ApiBaseAction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "收货地址")
@RestController
@RequestMapping("/api/address")
public class ApiAddressController extends ApiBaseAction {
    @Autowired
    private ApiAddressService addressService;

    /**
     * 获取用户的收货地址
     */
    @ApiOperation(value = "获取用户的收货地址接口", response = Map.class)
    @PostMapping("list")
    public Object list(@LoginUser UserVo loginUser) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("user_id", loginUser.getUserId());
        List<AddressVo> addressEntities = addressService.queryList(param);
        return toResponsSuccess(getAddresList(addressEntities));
    }
    /*优化接口调用封装方法一*/
    private Map<String,Object> getAddressInfo(AddressVo addressVo){
        Map<String,Object> map = new HashMap<>();
        map.put("id",addressVo.getId());
        map.put("userId",addressVo.getUserId());
        map.put("userName",addressVo.getUserName());
        map.put("telNumber",addressVo.getTelNumber());
        map.put("provinceName",addressVo.getProvinceName());
        map.put("cityName",addressVo.getCityName());
        map.put("countyName",addressVo.getCountyName());
        map.put("detailInfo",addressVo.getDetailInfo());
        map.put("cityId",addressVo.getCityId());
        map.put("provinceId",addressVo.getProvinceId());
        map.put("districtId",addressVo.getDistrictId());
        map.put("is_default",addressVo.getIs_default());
        map.put("full_region",addressVo.getFull_region());
        return map;
    }
    /*优化接口调用封装方法*/
    private List<Map<String,Object>> getAddresList(List<AddressVo> gallery){
        List<Map<String,Object>> lists = new ArrayList<>();
        for(AddressVo addressVo : gallery){
            Map<String,Object> map = new HashMap<>();
            map.put("id",addressVo.getId());
            map.put("userId",addressVo.getUserId());
            map.put("userName",addressVo.getUserName());
            map.put("telNumber",addressVo.getTelNumber());
            map.put("provinceName",addressVo.getProvinceName());
            map.put("cityName",addressVo.getCityName());
            map.put("countyName",addressVo.getCountyName());
            map.put("detailInfo",addressVo.getDetailInfo());
            map.put("cityId",addressVo.getCityId());
            map.put("provinceId",addressVo.getProvinceId());
            map.put("districtId",addressVo.getDistrictId());
            map.put("is_default",addressVo.getIs_default());
            map.put("full_region",addressVo.getFull_region());
            lists.add(map);
        }
        return lists;
    }

    /**
     * 获取收货地址的详情
     */
    @ApiOperation(value = "获取收货地址的详情", response = Map.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "收获地址ID", required = true, dataType = "Integer")})
    @PostMapping("detail")
    public Object detail(Integer id, @LoginUser UserVo loginUser) {
        AddressVo entity = addressService.queryObject(id);
        //判断越权行为
        if (!entity.getUserId().equals(loginUser.getUserId())) {
            return toResponsObject(403, "您无权查看", "");
        }

        return toResponsSuccess(getAddressInfo(entity));
    }

    /**
     * 添加或更新收货地址
     */
    @ApiOperation(value = "添加或更新收货地址", response = Map.class)
    @PostMapping("save")
    public Object save(@LoginUser UserVo loginUser) {
        JSONObject addressJson = this.getJsonRequest();
        AddressVo entity = new AddressVo();
        if (null != addressJson) {
            entity.setId(addressJson.getLong("id"));
            entity.setUserId(loginUser.getUserId());
            entity.setUserName(addressJson.getString("userName"));
            entity.setPostalCode(addressJson.getString("postalCode"));
            entity.setProvinceName(addressJson.getString("provinceName"));
            entity.setCityName(addressJson.getString("cityName"));
            entity.setCountyName(addressJson.getString("countyName"));
            entity.setDetailInfo(addressJson.getString("detailInfo"));
            entity.setNationalCode(addressJson.getString("nationalCode"));
            entity.setTelNumber(addressJson.getString("telNumber"));
            entity.setIs_default(addressJson.getBooleanValue("is_default")==true?1:0);

            entity.setCityId(addressJson.getInteger("city_id"));
            entity.setProvinceId(addressJson.getInteger("province_id"));
            entity.setDistrictId(addressJson.getInteger("district_id"));
        }
        if(entity.getIs_default()==1){
            Map<String,Object> objectMap = new HashMap<>();
            objectMap.put("is_default",0);
            objectMap.put("userId",entity.getUserId());
            addressService.updateAddresDefault(objectMap);
        }
        if (null == entity.getId() || entity.getId() == 0) {
            entity.setId(null);
            addressService.save(entity);
        } else {
            addressService.update(entity);
        }
        //return toResponsSuccess(entity);
        return toResponsSuccess("添加成功");
    }

    /**
     * 删除指定的收货地址
     */
    @ApiOperation(value = "删除指定的收货地址", response = Map.class)
    @PostMapping("delete")
    public Object delete(@LoginUser UserVo loginUser) {
        JSONObject jsonParam = this.getJsonRequest();
        Integer id = jsonParam.getIntValue("id");

        AddressVo entity = addressService.queryObject(id);
        //判断越权行为
        if (!entity.getUserId().equals(loginUser.getUserId())) {
            return toResponsObject(403, "您无权删除", "");
        }
        addressService.delete(id);
        if(entity.getIs_default()==1){
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("user_id", loginUser.getUserId());
            List<AddressVo> addressEntities = addressService.queryList(param);
            if(addressEntities!=null&&addressEntities.size()>0){
                AddressVo obj = new AddressVo();
                obj.setUserId(loginUser.getUserId());
                obj.setIs_default(1);
                obj.setId(addressEntities.get(0).getId());
                addressService.update(obj);
            }

        }
        return toResponsSuccess("");
    }


}