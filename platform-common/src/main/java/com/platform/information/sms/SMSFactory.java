package com.platform.information.sms;

import com.platform.entity.SmsConfig;
import com.platform.oss.*;
import com.platform.service.SysConfigService;
import com.platform.utils.Constant;
import com.platform.utils.SpringContextUtils;

public final class SMSFactory {

    private static SysConfigService sysConfigService;

    static {
        SMSFactory.sysConfigService = (SysConfigService) SpringContextUtils.getBean("sysConfigService");
    }

    public static AbstractSms build() {
        //获取短信配置信息
        SmsConfig config = sysConfigService.getConfigObject(Constant.SMS_CONFIG_KEY, SmsConfig.class);

        if (config.getType() == Constant.SmsService.CHUANGRUI.getValue()) {
//            return new AliSmsImpl(config);
        }else if (config.getType() == Constant.SmsService.ALIYUN.getValue()){
            return new AliSmsImpl(config);
        }

        return null;
    }

}
