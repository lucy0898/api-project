package com.platform.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lx
 * @email 290988002@qq.com
 * @date 2018-11-10 08:03:40
 * 首页栏目
 */
public class FrontPageVo implements Serializable {

    private static final long serialVersionUID = 1L;
    //主键
    private Integer id;
    //栏目名称
    private String name;
    //栏目类型，关联字典表
    private Integer category;
    //栏目封面
    private String imgurl;
    //栏目链接
    private String link;
    //栏目描述
    private String content;
    //栏目创建时间
    private Date create_time;
    //状态 0：启用，1：删除
    private Integer enabled;


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getCategory() {
        return category;
    }
    public void setCategory(Integer category) {
        this.category = category;
    }
    public String getImgurl() {
        return imgurl;
    }
    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Date getCreate_time() {
        return create_time;
    }
    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
    public Integer getEnabled() {
        return enabled;
    }
    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
}
