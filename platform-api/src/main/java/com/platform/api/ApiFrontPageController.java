package com.platform.api;


import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.IgnoreAuth;
import com.platform.annotation.LoginUser;
import com.platform.entity.*;
import com.platform.service.ApiFrontPageService;
import com.platform.service.ApiFrontPageSubService;
import com.platform.service.SysMacroService;
import com.platform.util.ApiBaseAction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
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
 * 作者: @lx <br>
 * 时间: 2018-11-13 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "首页栏目配置")
@RestController
@RequestMapping("/api/front")
public class ApiFrontPageController extends ApiBaseAction {
    @Autowired
    private ApiFrontPageService apiFrontPageService;
    @Autowired
    private ApiFrontPageSubService apiFrontPageSubService;
    @Autowired
    private SysMacroService sysMacroService;

    @ApiOperation(value = "查询所有栏目信息接口")
    @IgnoreAuth
    @GetMapping("get_front_list")
    public Object getFrontList() {
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("fields","id,name");
            map.put("enabled",0);
            List<FrontPageVo> frontPageVoList = apiFrontPageService.queryList(map);
            return toResponsSuccess(frontPageVoList);
        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }

    @ApiOperation(value = "获取栏目下的商品信息接口")
    @IgnoreAuth
    @GetMapping("get_goods_list")
    public Object getGoodsList(){
        try{
            JSONObject jsonParam = super.getJsonRequestByParameterMap();
            String categoryValue = jsonParam.getString("categoryValue");
            Integer category = apiFrontPageService.queryMacrosByValue(categoryValue);
            Map<String,Object> n_map = new HashMap<>();
            n_map.put("fields","id");
            n_map.put("category",category);
            n_map.put("enabled",0);
            List<FrontPageVo> frontPageVoList = apiFrontPageService.queryList(n_map);
            Integer fid = frontPageVoList.size()>0?frontPageVoList.get(0).getId():0;
            FrontPageVo frontPageVo = apiFrontPageService.queryObject(fid);
            String frontpageName = frontPageVo==null?"":frontPageVo.getName();
            Map<String,Object> params = new HashMap<>();
            params.put("fields","b.id,b.name,b.list_pic_url,b.retail_price");
            params.put("sidx","a.sorted_order");
            params.put("order","desc");
            params.put("is_delete",0);
            params.put("fid",fid);
            List<GoodsVo> goodsVoList = apiFrontPageSubService.queryByFid(params);
            Map<String,Object> result =new HashMap<>();
            result.put("title",frontpageName);
            List<Map<String,Object>> mapList = new ArrayList<>();
            for(GoodsVo vo : goodsVoList)
            {
                Map<String,Object> map = new HashMap<>();
                map.put("id",vo.getId());
                map.put("name",vo.getName());
                map.put("retailPrice",vo.getRetail_price());
                map.put("imgUrl",vo.getList_pic_url());
                mapList.add(map);
            }
            result.put("list",mapList);
            return toResponsSuccess(result);
        }
        catch (Exception ex){
            return  toResponsFail(ex.getMessage());
        }
    }



    @ApiOperation(value = "获取其他栏目及商品信息")
    @IgnoreAuth
    @GetMapping(value = "get_other_front_list")
    public Object getOtherFrontList(){
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("fields","id,name,content");
            map.put("enabled",0);
            List<FrontPageVo> frontPageVoList = apiFrontPageService.queryList(map);
            List<Map<String,Object>> resultList = new ArrayList<>();
            for(int i=0;i<frontPageVoList.size();i++){
                if(i==0) continue;
                Map<String,Object> maps = new HashMap<>();
                FrontPageVo entity =frontPageVoList.get(i);
                if(entity!=null){
                    maps.put("id",entity.getId());
                    maps.put("name",entity.getName());
                    maps.put("desc",entity.getContent());
                    Map<String,Object> params = new HashMap<>();
                    params.put("fields","b.list_pic_url");
                    params.put("sidx","a.sorted_order");
                    params.put("order","desc");
                    params.put("is_delete",0);
                    params.put("fid",entity.getId());
                    List<GoodsVo>  frontPageSubVoList = apiFrontPageSubService.queryByFid(params);
                    if(frontPageSubVoList.size()>0) {
                        GoodsVo goodsVo = frontPageSubVoList.get(0);
                        if(goodsVo!=null){
                            maps.put("imgUrl",goodsVo.getList_pic_url());
                        }
                        else{
                            maps.put("imgUrl","");
                        }
                    }else{
                        maps.put("imgUrl","");
                    }
                }
                resultList.add(maps);
            }
            return  toResponsSuccess(resultList);
        }
        catch (Exception ex){
            return  toResponsFail(ex.getMessage());
        }

    }

}
