package com.platform.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.platform.service.ApiMemberCardService;
import com.platform.util.ApiBaseAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.platform.entity.MemberCardVo;
import com.platform.utils.PageUtils;
import com.platform.utils.Query;
import com.platform.utils.R;

/**
 * Controller
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2018-11-20 13:17:30
 */
@RestController
@RequestMapping("api/membercard")
public class ApiMemberCardController extends ApiBaseAction {
    @Autowired
    private ApiMemberCardService memberCardService;

    /**
     * 查看列表
     */
    @RequestMapping("/list")
    public Object list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);

        List<MemberCardVo> memberCardList = memberCardService.queryList(null);
        int total = memberCardService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(memberCardList, total, query.getLimit(), query.getPage());

        Map<String,Object> res= new HashMap<>();
        res.put("page", pageUtil);
        return toResponsSuccess(res);
    }

    /**
     * 查看信息
     */
    @RequestMapping("/info/{id}")
    public Object info(@PathVariable("id") Integer id) {
        MemberCardVo memberCard = memberCardService.queryObject(id);
        Map<String,Object> res= new HashMap<>();
        res.put("detail", memberCard);
        return toResponsSuccess(res);
    }


    /**
     * 查看所有列表
     */
    @PostMapping("/queryAll")
    public Object queryAll() {

        List<MemberCardVo> list = memberCardService.queryList(null);

        Map<String,Object> res= new HashMap<>();
        res.put("list", list);
        return toResponsSuccess(res);
    }
}
