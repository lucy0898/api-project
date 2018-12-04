package com.platform.api;


import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.IgnoreAuth;
import com.platform.entity.GoodsVo;
import com.platform.service.*;
import com.platform.util.ApiBaseAction;
import com.platform.utils.CharUtil;
import com.platform.utils.ImgsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 作者: @author LuX <br>
 * 时间: 2018-11-10 <br>
 * 描述: ApiImgProduceController <br>
 */

@Api(tags = "后台图片处理接口")
@RestController
@RequestMapping("/api/img")
public class ApiImgProduceController extends ApiBaseAction {
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

    @ApiOperation(value = "后台生成图片")
    @IgnoreAuth
    @GetMapping(value = "getPic")
    public Object getPic(){
        JSONObject jsonParam = super.getJsonRequestByParameterMap();
        try {
            int result = jsonParam.getIntValue("id");//获取商品ID
            GoodsVo goods = goodsService.queryObject(result);
            if(goods==null)  return toResponsFail("参数有误");
            ImgsUtil tt = new ImgsUtil();
            BufferedImage background_pic = tt.loadImageLocal("D:\\1.jpg");
            BufferedImage goods_pic = tt.loadImageUrl(goods.getList_pic_url());
            String nonceStr = CharUtil.getRandomString(12);
            tt.writeImageLocal("D:\\"+nonceStr+".jpg", tt.modifyImagetogeter(goods_pic,background_pic ,-100,100,1300,1300));

            /*** 第一步增加商品名称，返回路径* */
            String first_file = ReAddWatermark("D:\\"+nonceStr+".jpg",goods.getName(),tt,new Font("微软雅黑", Font.PLAIN, 50),20,180,Color.black);
            delFile("D:\\"+nonceStr+".jpg");
            /*** 第二步增加商品金额，返回路径* */
            String second_file = ReAddWatermark(first_file,"￥"+goods.getMarket_price().toString(),tt,new Font("微软雅黑", Font.PLAIN, 45),20,250,Color.RED);
            delFile(first_file);
            /*** 第三步增加LOGO，返回路径* */
            String third_file = ReAddWatermark(second_file,"百分之六购物",tt,new Font("微软雅黑", Font.PLAIN, 40),450,60,Color.white);
            delFile(second_file);
            /*** 第四步增加小程序扫码名称，返回路径* */
            String fourth_file = ReAddWatermark(third_file,"百分之六小程序",tt,new Font("微软雅黑", Font.PLAIN, 40),700,1500,Color.BLACK);
            delFile(third_file);
            return toResponsSuccess("OK");
        }
        catch (Exception ex){
            return toResponsFail("参数有误");
        }
    }
    /**
     * 同一图片增加多处水印
     * */
    private String ReAddWatermark(String srcImgPath,String content,ImgsUtil tt,Font font,int x,int y,Color color){
        String nonceStr_font = CharUtil.getRandomString(14);
        String tarImgPath="D:\\tmp\\"+nonceStr_font+".jpg"; //待存储的地址
        String waterMarkContent=content;  //水印内容
        return tt.addWaterMark(srcImgPath, tarImgPath,waterMarkContent, color, font,x,y);
    }
    /**
     * 删除文件
     * */
    public void delFile(String path){
        File file=new File(path);
        if(file.exists()&&file.isFile())
            file.delete();
    }

}
