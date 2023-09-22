package com.chinasie.codearts.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @Description 运行时配置类
 * 特别注意：
 * （1）该类配置不能直接用于静态常量的定义，因为会有赋值顺序的问题，只能用于方法内使用
 * 错误示范：IhrUrlConstants类
 * public static final String POST_SIGNEDCONTRACTS =  ApplicationConfig。BASE_HTTPS_HOST + "/ihr/api/v1/contract/signed";
 * 正确示范：方法内
 * public void doSomeThing() {
 *     url = ApplicationConfig。BASE_HTTPS_HOST + IhrUrlConstants.POST_SIGNEDCONTRACTS;
 * }
 *
 * @Author sie_shenjingrong
 * @Date 2022/12/3 17:30
 */
@Component
public class ApplicationConfig implements EnvironmentAware {

    /** HTTP域名 **/
    public static String BASE_HTTP_HOST;
    /** HTTPS域名 **/
    public static String BASE_HTTPS_HOST;
    /** 谷神API_HTTP域名 **/
    public static String API_BASE_HTTP_HOST;
    /** 主数据服务地址 **/
    public static String MASTER_HOST;
    /** 谷神物资管理系统编码 */
    public static String SYSTEM_CODE;
    /** 认证中心校验url */
    public static String VALIDATE_JWT_URL;
    /** 认证中心校验url */
    public static String SSO_LOGIN_URL;

    @Override
    public void setEnvironment(Environment environment) {
        BASE_HTTP_HOST = environment.getProperty("sie.http-domain");
        BASE_HTTPS_HOST = environment.getProperty("sie.https-domain");
        API_BASE_HTTP_HOST = environment.getProperty("sie.api-http-domain");
        MASTER_HOST = environment.getProperty("sie.master-host");
        SYSTEM_CODE = environment.getProperty("sie.system-code");
        VALIDATE_JWT_URL = environment.getProperty("sie.certification.validate-token-url");
        SSO_LOGIN_URL = environment.getProperty("sie.certification.sso-login-url");
    }

}
