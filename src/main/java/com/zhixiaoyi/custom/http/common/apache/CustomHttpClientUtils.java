package com.zhixiaoyi.custom.http.common.apache;

import com.alibaba.fastjson.JSON;
import com.zhixiaoyi.custom.http.common.apache.httppool.CustomHttpClientConfig;
import com.zhixiaoyi.custom.http.common.apache.httppool.HttpClientPool;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
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
 * CustomHttpClientPool工具包
 * </p>
 *
 * @author Waiting
 * @since 2018/5/16 20:41
 */
public class CustomHttpClientUtils {
    private static Logger logger = LoggerFactory.getLogger(CustomHttpClientUtils.class);
    private static Charset DEFAULT_CHARSET = Consts.UTF_8;
    private final static int HTTP_OK = 200;
    private static CustomHttpClientConfig customHttpClientConfig = new CustomHttpClientConfig(DEFAULT_CHARSET);

    /**
     * 设置字符集
     * 针对整个工具全部替换为此编码，不针对单一接口用特定编码
     *
     * @param charset  Consts.UTF_8
     */
    public static void setCharset(Charset charset) {
        DEFAULT_CHARSET = charset;
        CustomHttpClientConfig customHttpClientConfig = new CustomHttpClientConfig(charset);
    }

    public static String sendGet(String url) throws Exception {
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = HttpClientPool.getHttpClient().execute(httpget,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, DEFAULT_CHARSET);
            EntityUtils.consume(entity);
            return result;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * FORM表单POST方式提交请求
     *
     * @param url
     * @param params
     */
    public static String sendFormPost(String url, Map<String, Object> params) throws Exception {
        return sendFormPost(url, params, null);
    }

    /**
     * FORM表单POST方式提交请求
     *
     * @param url
     * @param params
     * @param proxyHost 代理服务器信息 HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
     */
    public static String sendFormPost(String url, Map<String, Object> params, HttpHost proxyHost) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        if (proxyHost != null) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setProxy(proxyHost)
                    .build();
            httpPost.setConfig(requestConfig);
        }
        httpPost.addHeader(customHttpClientConfig.getFormContentType());
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> formParams = new ArrayList<>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formParams, DEFAULT_CHARSET);
            httpPost.setEntity(urlEncodedFormEntity);
        }
        return sendPost(httpPost, url, JSON.toJSONString(params));
    }

    /**
     * BODY字符串JSON格式POST方式提交请求
     *
     * @param url
     * @param jsonStr
     */
    public static String sendBodyPost(String url, String jsonStr) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(customHttpClientConfig.getBodyJsonContentType());
        httpPost.setEntity(new StringEntity(jsonStr, DEFAULT_CHARSET));
        return sendPost(httpPost, url, jsonStr);
    }


    /**
     * 发送POST请求
     */
    private static String sendPost(HttpPost httpPost, String url, String paramStr) throws Exception {
        String rst;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = HttpClientPool.getHttpClient().execute(httpPost, HttpClientContext.create());
            if (httpResponse == null) {
                throw new Exception("http调用异常，response返回空");
            }

            int httpCode = httpResponse.getStatusLine().getStatusCode();
            if (httpCode == HTTP_OK) {
                rst = EntityUtils.toString(httpResponse.getEntity(), DEFAULT_CHARSET);
                logger.debug("http调用成功,url={},params={},rst={}", url, paramStr, rst);
                return rst;
            } else {
                rst = EntityUtils.toString(httpResponse.getEntity(), DEFAULT_CHARSET);
                logger.warn("http调用失败,code={},url={},params={},rst={}", httpCode, url, paramStr, rst);
                throw new Exception("http请求失败,返回code=" + httpCode + ",msg=" + rst);
            }
        } catch (Exception e) {
            throw e;
        }
    }


}
