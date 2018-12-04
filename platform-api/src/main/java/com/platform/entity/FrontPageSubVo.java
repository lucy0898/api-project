package com.platform.entity;

import java.io.Serializable;

/**
 * @author lx
 * @email 290988002@qq.com
 * @date 2018-11-10 08:03:40
 * 首页栏目与商品关系
 */
public class FrontPageSubVo implements Serializable {

    private static final long serialVersionUID = 1L;
    //主键
    private Integer id;
    //首页ID
    private Integer fid;
    //商品ID
    private Integer gid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

}
