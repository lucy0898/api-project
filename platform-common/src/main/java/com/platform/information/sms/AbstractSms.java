package com.platform.information.sms;

import com.platform.entity.SmsConfig;
import com.platform.oss.CloudStorageConfig;

public abstract class AbstractSms {

    /**
     * 短息配置信息
     */
    SmsConfig config;


    /**
     *
     * @param code  模版CODE
     * @param phoneNumbers  电话号码
     * @param templateParam  "{\"name\":\"Tom\", \"code\":\"123\"}"
     * @return  error  success
     */
    public abstract String sendSms(String code, String phoneNumbers, String templateParam);

}
