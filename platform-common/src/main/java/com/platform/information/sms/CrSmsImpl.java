package com.platform.information.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.platform.entity.SmsConfig;
import com.platform.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.*;

public class CrSmsImpl extends AbstractSms {

    private static final Log log = LogFactory.getLog(CrSmsImpl.class);


    public CrSmsImpl(SmsConfig config) {
        this.config = config;

        //初始化
        init();
    }

    private void init() {

    }

    @Override
    @Deprecated
    public String sendSms(String code, String phoneNumbers, String templateParam) {
        String res = "success";
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        String resultBuffer = "";
        try {
            StringBuffer param = new StringBuffer();
            param.append("name=" + this.config.getName());
            param.append("&pwd=" + this.config.getPwd());
            param.append("&mobile=").append(phoneNumbers);
            param.append("&content=").append(URLEncoder.encode(templateParam, "UTF-8"));
//        if (StringUtils.isNotEmpty(stime)) {
//            param.append("&stime=" + stime);
//        }
            param.append("&sign=").append(URLEncoder.encode(this.config.getSign(), "UTF-8"));
            param.append("&type=pt");
//        if (StringUtils.isNotEmpty(extno)) {
//            param.append("&extno=").append(extno);
//        }

            URL localURL = new URL("http://web.cr6868.com/asmx/smsservice.aspx?");
            URLConnection connection = localURL.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(param.length()));


            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(param.toString());
            outputStreamWriter.flush();

            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            inputStream = httpURLConnection.getInputStream();
            resultBuffer = convertStreamToString(inputStream);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

                if (reader != null) {
                    reader.close();
                }

                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return resultBuffer;

//        return res;
    }


    /**
     * 转换返回值类型为UTF-8格式.
     *
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {
        StringBuilder sb1 = new StringBuilder();
        byte[] bytes = new byte[4096];
        int size = 0;

        try {
            while ((size = is.read(bytes)) > 0) {
                String str = new String(bytes, 0, size, "UTF-8");
                sb1.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb1.toString();
    }
}
