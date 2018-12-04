package com.platform.information.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.platform.entity.SmsConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AliSmsImpl extends AbstractSms {

    private static final Log log = LogFactory.getLog(AliSmsImpl.class);

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    public AliSmsImpl(SmsConfig config) {
        this.config = config;

        //初始化
        init();
    }

    private void init() {

    }

    @Override
    public String sendSms(String code, String phoneNumbers, String templateParam) {
        String res = "";String errorLog = null;
        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", this.config.getAliyunAccessKeyId(), this.config.getAliyunAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(phoneNumbers);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName("百分之六");
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(code);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
//            request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");
            request.setTemplateParam(templateParam);

            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");

            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//            request.setOutId("yourOutId");

            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if("OK".equals(sendSmsResponse.getCode())){
                res = "success";
            }else {
                res = "error";
                switch (sendSmsResponse.getCode()){
                    case "isp.RAM_PERMISSION_DENY":{errorLog = "RAM权限DENY";break;}
                    case "isp.OUT_OF_SERVICE":{errorLog = "业务停机";break;}
                    case "isp.PRODUCT_UN_SUBSCRIPT":{errorLog = "未开通云通信产品的阿里云客户";break;}
                    case "isp.PRODUCT_UNSUBSCRIBE":{errorLog = "产品未开通";break;}
                    case "isp.ACCOUNT_NOT_EXISTS":{errorLog = "账户不存在";break;}
                    case "isp.ACCOUNT_ABNORMAL":{errorLog = "账户异常";break;}
                    case "isp.SMS_TEMPLATE_ILLEGAL":{errorLog = "短信模板不合法";break;}
                    case "isp.SMS_SIGNATURE_ILLEGAL":{errorLog = "短信签名不合法";break;}
                    case "isp.INVALID_PARAMETERS":{errorLog = "参数异常";break;}
                    case "isp.SYSTEM_ERROR":{errorLog = "系统错误";break;}
                    case "isp.MOBILE_NUMBER_ILLEGAL":{errorLog = "非法手机号";break;}
                    case "isp.MOBILE_COUNT_OVER_LIMIT":{errorLog = "手机号码数量超过限制";break;}
                    case "isp.TEMPLATE_MISSING_PARAMETERS":{errorLog = "模板缺少变量";break;}
                    case "isp.BUSINESS_LIMIT_CONTROL":{errorLog = "业务限流";break;}
                    case "isp.INVALID_JSON_PARAM":{errorLog = "JSON参数不合法，只接受字符串值";break;}
                    case "isp.BLACK_KEY_CONTROL_LIMIT":{errorLog = "黑名单管控";break;}
                    case "isp.PARAM_LENGTH_LIMIT":{errorLog = "参数超出长度限制";break;}
                    case "isp.PARAM_NOT_SUPPORT_URL":{errorLog = "不支持URL";break;}
                    case "isp.AMOUNT_NOT_ENOUGH":{errorLog = "账户余额不足";break;}

                    default:{errorLog="未知错误，返回状态码："+sendSmsResponse.getCode();}
                }
                log.error("code:"+code+"   templateParam:"+templateParam+"   手机号码："+phoneNumbers+"  失败原因："+errorLog);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
