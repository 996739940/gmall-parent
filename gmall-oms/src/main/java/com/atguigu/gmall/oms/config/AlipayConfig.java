package com.atguigu.gmall.oms.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName AlipayConfig
 * @Description 阿里支付配置类
 * @Author 张燕廷
 * @Date 2020/5/15 15:57
 * @Version 1.0
 **/
@Configuration
public class AlipayConfig implements InitializingBean {
//    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
//    public static String app_id = "2016092300574219";
//
//    // 商户私钥，您的PKCS8格式RSA2私钥
//    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCG/HYvXM+h/06yCbesEcNzOOEoI4GyKnZsGQTvO9cq6SBJyZ7+2BkGPMgurf3VgS0tg+TIDPTIzbEQ47ft7dScoVEOgRT780HM10kesZtYCz1G85ABtH/zN+gxviN7Mp+GUMDwDj4mwN0u9Vom+fViiKQ2edt7KnVSOVZvSWs2LZ6gkSEck9PVIlBalHj3QVOzi+Q+64ekE5rFpGW8cOXkPCu1FYLLHJMGlSjzBbwt20xrHODSpJwAASzVPZ6k4gSfc9POdC9d7l6OmHtTms4Ozt7X2NjjdJ4LfvLklXzmjr6TCCHk1zohz0+3utPFs5j7JB77wH1ugI6+nZRPoGejAgMBAAECggEAKIfZfLMjbQLZwD+GtNXIl+o2QPUYVkTFtgN1JdgT7UB+f2lYojCEowjRGqMHmnnk9MurgB3Tyg9o4Sde7MQCmN0fF3nOsTADS9xAikTN3gvR5GHxlXF7Ea5xu63ZvX4fxBavN+Uu6Df0scrdvOJEucdJd0nzivHetEefGl8zH9r29yqik3GDiJeMqzCKzkwQSUOaasskkm7A5ELE6s3mshykHY9MZ8RHeK2CU70kLIVLTwjlhUTvgwu2d4hg+27UHP02yNNX0dfeY2bHt7sPAxQXVDdv5M4tj28M3lWVT0Cd4eeBAV42RjksBdH4ntTQGUvj1oqij0FL0qcGAUj/kQKBgQDFUYXjnXXFfEX9apDmSQouZ4McnvbC6zLis/oeWLQpU5heNWaFcTl1sy6mftbXQaJZQF5f5vaNboNKeVur27IxWxT26mQNZfMIhCNI3KufLVtCvrE1kP66x1ax96Ygfb0gXybEF0CAdMeV3RzQQD18vr+z2XpaPpuOyLUOH4e61wKBgQCvIWLMFEGwcYnaidkVi3FeUCw4v/cQ/TZUe0EYWlwb30BoVauvim32GlQF6T/IGSNUk9TzYCkdvNLDo/GoN9kBjpx6g0wLu6yMP2rGZsQ3XG25ZDxFABwXfcFRoz5b53rUhfpb1x8y+GoFr6j87ZitZNoPq/xis16zxyQtTaQMFQKBgGKiNcNp2o01aecbdgMnT6eSpr+P1tnoqC8auFSjEqv11ZyCJRVfW9GyGAeyL8K6VGU4//QAiA56x7Zao7Bsyc5SMVCcKhdoduwy4VByvnd7PLtIh1y3dh33tyaPHMPxFrEv3zj2Ro/v0DThqp2O/y10TG+irr6dvTTKr3a33ClRAoGAWJnHZgAJi76lQ47h6Oh3HoPpG3zi/hFc3zjMIgn095Tm/OH6zgB2yT9gquzqe81b9nokDkzkAEt7Rcc3hydZ/lOsDr9GRdlY3ktnNaDgPDHiwRxY8X+wtqHEoAy6i7x8g/CUTMp02lCbMu0/zVZXG8IeIDeepECi9MkXIGRbTlECgYAHPjKN3sXGJU0d2rJIIjjRdXfCTUCVUop7ZQO6tlWap+ViWBEp7cO4FNiGYZ1guf9XCBmrEGVTTOSPvthOSgzsTDUUuQc6IVIZ0/S2PDkOm30u1q2Jb324aNuiuQCWmKkTfhMWuYh0exfhFVBLawJwHPAPgeyDmigx6Bc1QtK6/g==";
//
//    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
//    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx2oXRv2jYeAJfolU/KtlQUAWFRM/qGngRIVm2RoTCrN+9VMVB0Ct9r7WqWNf5PmIG+BbnLypdNa8cxqZh+cacyi2r3GZ84bJwvF75QwRvmGKWTTnLjmpxXLTOdFFnKNZImYIqCJTGwt8cIO8LZWm/bfUKCaQKeyodZgfDUsCQmOqtj0rOucWhy2OEoSU2v9QYLrQxIOSfEPJp2PxvkU4LaK4fo3FKEnaZs5LHTs/ANHW2w5ef6HF8dmKUQLZEXABOnj95t+C5EhFALhQpAZU6JO/jUZy8nwEfIJOTB/+mc5epzSHyGaZgRBDlJmlL/wgiFqelnkDCZ7yxzD5MMCjOQIDAQAB";
//
//    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
//    //通知后台系统，支付结果
//    public static String notify_url = "http://y16q7udp4v.bjhttp.cn/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";
//
//    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
//    //通知前台系统，支付结果
//    public static String return_url = "http://y16q7udp4v.bjhttp.cn/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";
//
//    // 签名方式
//    public static String sign_type = "RSA2";
//
//    // 字符编码格式
//    public static String charset = "utf-8";
//
//    // 支付宝网关
//    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public static String app_id;
    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key ;
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key;
    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url;
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //支付成功以后跳到哪里 http://tfkv0cljsb.51http.tech/
    public static String return_url;
    // 签名方式
    public static String sign_type;
    // 字符编码格式
    public static String charset;

    // 支付宝网关  https://openapi.alipaydev.com/gateway.do 这是正式地址
    public static String gatewayUrl;


    @Value("${alipay.app_id}")
    public  String appid;
    @Value("${alipay.merchant_private_key}")
    public  String merchantprivatekey ;
    @Value("${alipay.alipay_public_key}")
    public  String alipaypublickey;
    @Value("${alipay.notify_url}")
    public  String notifyurl;
    @Value("${alipay.return_url}")
    public  String returnurl;
    @Value("${alipay.sign_type}")
    public  String signtype;
    @Value("${alipay.charset}")
    public  String charset2;
    @Value("${alipay.gatewayUrl}")
    public  String gatewayUrl2;


    //Spring给这个bean的所有属性设置好值以后
    @Override
    public void afterPropertiesSet() throws Exception {
        // 为所有的静态属性赋值为Spring获取到的值
        app_id = appid;
        merchant_private_key = merchantprivatekey;
        alipay_public_key = alipaypublickey;
        notify_url = notifyurl;
        return_url = returnurl;
        sign_type = signtype;
        charset = charset2;
        gatewayUrl = gatewayUrl2;
    }



}
