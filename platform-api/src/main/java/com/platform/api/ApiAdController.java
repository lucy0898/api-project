package com.platform.api;


import com.platform.annotation.IgnoreAuth;
import com.platform.entity.AdVo;
import com.platform.service.ApiAdService;
import com.platform.util.ApiBaseAction;
import com.platform.utils.ResourceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "首页接口文档")
@RestController
@RequestMapping("/api/ad")
public class ApiAdController extends ApiBaseAction {
    @Autowired
    private ApiAdService adService;

    @ApiOperation(value = "查询首页轮播图")
    @IgnoreAuth
    @GetMapping("list")
    public Object list() {
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("ad_position_id", ResourceUtil.getConfigByName("ad.postition"));
            map.put("enabled",1);
            List<AdVo> adVoList = adService.queryList(map);
            List<Map<String,Object>> resultLists = new ArrayList<>();
            for(AdVo adVo : adVoList){
                Map<String,Object> resultMap = new HashMap<>();
                resultMap.put("image_url",adVo.getImage_url());
                resultLists.add(resultMap);
            }
            return toResponsSuccess(resultLists);
        } catch (Exception ex) {
            return toResponsFail(ex.getMessage());
        }
    }

}
