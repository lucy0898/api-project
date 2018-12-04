package com.platform.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-15 08:03:41
 */
public class UserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键
    private Long userId;
    //会员名称
    private String username;
    //会员密码
    private String password;
    //性别
    private Integer gender;
    //出生日期
    private Date birthday;
    //注册时间
    private Date register_time;
    //最后登录时间
    private Date last_login_time;
    //最后登录Ip
    private String last_login_ip;
    //会员等级
    private Integer user_level_id;
    //别名
    private String nickname;
    //手机号码
    private String mobile;
    //注册Ip
    private String register_ip;
    //头像
    private String avatar;
    //微信Id
    private String weixin_openid;



    //是否会员
    private Integer is_member;
    //会员开始时间
    private Date inspire_time;
    //会员结束时间
    private Date expire_time;
    //多花的
    private BigDecimal waste_money;
    private String cardName;
    private Integer member_card_id;

    //推荐人Id
    private Integer referrer_id;
    private String union_id;
    //微信Id
    private String app_weixin_openid;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getRegister_time() {
        return register_time;
    }

    public void setRegister_time(Date register_time) {
        this.register_time = register_time;
    }

    public Date getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Date last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getLast_login_ip() {
        return last_login_ip;
    }

    public void setLast_login_ip(String last_login_ip) {
        this.last_login_ip = last_login_ip;
    }

    public Integer getUser_level_id() {
        return user_level_id;
    }

    public void setUser_level_id(Integer user_level_id) {
        this.user_level_id = user_level_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRegister_ip() {
        return register_ip;
    }

    public void setRegister_ip(String register_ip) {
        this.register_ip = register_ip;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getWeixin_openid() {
        return weixin_openid;
    }

    public void setWeixin_openid(String weixin_openid) {
        this.weixin_openid = weixin_openid;
    }

    public Integer getIs_member() {
        return is_member;
    }

    public void setIs_member(Integer is_member) {
        this.is_member = is_member;
    }

    public Date getInspire_time() {
        return inspire_time;
    }

    public void setInspire_time(Date inspire_time) {
        this.inspire_time = inspire_time;
    }

    public Date getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(Date expire_time) {
        this.expire_time = expire_time;
    }

    public BigDecimal getWaste_money() {
        return waste_money;
    }

    public void setWaste_money(BigDecimal waste_money) {
        this.waste_money = waste_money;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Integer getMember_card_id() {
        return member_card_id;
    }

    public void setMember_card_id(Integer member_card_id) {
        this.member_card_id = member_card_id;
    }

    public Integer getReferrer_id() {
        return referrer_id;
    }

    public void setReferrer_id(Integer referrer_id) {
        this.referrer_id = referrer_id;
    }

    public String getUnion_id() {
        return union_id;
    }

    public void setUnion_id(String union_id) {
        this.union_id = union_id;
    }

    public String getApp_weixin_openid() {
        return app_weixin_openid;
    }

    public void setApp_weixin_openid(String app_weixin_openid) {
        this.app_weixin_openid = app_weixin_openid;
    }
}
