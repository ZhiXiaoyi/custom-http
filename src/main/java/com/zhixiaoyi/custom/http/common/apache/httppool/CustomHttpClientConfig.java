package com.zhixiaoyi.custom.http.common.apache.httppool;

import org.apache.http.message.BasicHeader;

import java.nio.charset.Charset;

/**
 * <p>
 * CustomHttpClientConfig
 * </p>
 *
 * @author Waiting
 * @since 2018/5/16 20:43
 */
public class CustomHttpClientConfig {
    private BasicHeader formContentType;
    private BasicHeader bodyJsonContentType;

    public CustomHttpClientConfig(Charset charset){
        formContentType = new BasicHeader("Content-Type","application/x-www-form-urlencoded; charset="+charset.name());
        bodyJsonContentType = new BasicHeader("Content-Type","application/json; charset="+charset.name());
    }

    public BasicHeader getFormContentType() {
        return formContentType;
    }

    public BasicHeader getBodyJsonContentType() {
        return bodyJsonContentType;
    }
}
