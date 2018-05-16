package com.zhixiaoyi.custom.http.common.apache;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 单独的一个httpClientUtil工具
 * </p>
 *
 * @author Waiting
 * @since 2018/5/16 20:37
 */
public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private final static String DEFAULT_CHARSET = "UTF-8";
    private static int defaultConnectionTimeout = 60000;
    private static int defaultRequestTimeout = 5000;
    private static int defaultSocketTimeout = 1000;

    public static CookieStore cookieStore = new BasicCookieStore();
    public static CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

    private static RequestConfig defaultRequestConfig() {
        // 配置超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(defaultConnectionTimeout)// 设置连接超时时间
                .setConnectionRequestTimeout(defaultRequestTimeout)// 设置请求超时时间
                .setSocketTimeout(defaultSocketTimeout)
                .setRedirectsEnabled(true).build();// 默认允许自动重定向
        return requestConfig;
    }

    public static String sendGet(String url) throws Exception {
        RequestConfig requestConfig = defaultRequestConfig();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        String strResult;
        int statusCode;
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse != null) {
                statusCode = httpResponse.getStatusLine().getStatusCode();
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    strResult = EntityUtils.toString(httpResponse.getEntity(), DEFAULT_CHARSET);// 获得返回的结果
                    logger.debug("httpGet/" + statusCode + ":" + strResult);
                    return strResult;
                } else {
                    strResult = EntityUtils.toString(httpResponse.getEntity(), DEFAULT_CHARSET);// 获得返回的结果
                    logger.warn("httpGet/" + statusCode + ":" + strResult);
                    throw new Exception("httpGet/" + statusCode + ":" + strResult);
                }
            } else {
                logger.warn("Error Response:httpResponse=null");
                throw new Exception("Error Response:httpResponse=null");
            }
        } catch (IOException e) {
            throw e;
        } finally {
        }
    }

    public static String sendFromPost(String url, Map<String, Object> paramsMap) throws Exception {
        RequestConfig requestConfig = defaultRequestConfig();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        List<NameValuePair> nvps = new ArrayList<>();
        for (String key : paramsMap.keySet()) {
            nvps.add(new BasicNameValuePair(key, String.valueOf(paramsMap.get(key))));
        }
        String strResult;
        Integer statusCode;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, DEFAULT_CHARSET));
            logger.debug(("httpPostMapForm/" + EntityUtils.toString(httpPost.getEntity())));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            if (httpResponse != null) {
                strResult = EntityUtils.toString(httpResponse.getEntity(), DEFAULT_CHARSET);
                if (statusCode != HttpStatus.SC_OK) {
                    logger.warn("Error Response: httpPostMapForm/" + statusCode + ":" + strResult);
                    logger.debug("httpPostMapForm/" + statusCode + ":" + strResult);
                    throw new Exception("Error Response: " + httpResponse.getStatusLine().toString());
                }
                return strResult;
            } else {
                logger.warn("Error Response:httpResponse=null");
                throw new Exception("Error Response:httpResponse=null");
            }
        } catch (Exception e) {
            throw e;

        } finally {
            //关闭
        }

    }

    // 接口地址
    public static String sendBodyPost(String url, String parameters) throws Exception {
        logger.debug("httpPostStrBody/parameters:{},url:{}", parameters, url);
        RequestConfig requestConfig = defaultRequestConfig();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        String strResult;
        Integer statusCode;
        try {
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(new StringEntity(parameters, Charset.forName(DEFAULT_CHARSET)));
            long startTime = System.currentTimeMillis();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            long endTime = System.currentTimeMillis();
            logger.info("httpPostStrBody 调用花费时间(单位：毫秒)：{}", (endTime - startTime));
            statusCode = httpResponse.getStatusLine().getStatusCode();
            strResult = EntityUtils.toString(httpResponse.getEntity(), DEFAULT_CHARSET);
            logger.debug("httpPostMapForm/" + statusCode + ":" + strResult);
            if (statusCode != HttpStatus.SC_OK) {
                logger.warn("Error Response: httpPostStrBody/" + statusCode + ":" + strResult);
                throw new Exception("Error Response: " + httpResponse.getStatusLine().toString());
            }
            return strResult;
        } catch (IOException e) {
            throw e;
        } finally {
            //关闭
        }
    }

    public static void createCookie(List<BasicClientCookie> cookielist) {
        for (BasicClientCookie cookie : cookielist) {
            HttpClientUtil.cookieStore.addCookie(cookie);
        }
    }
}
