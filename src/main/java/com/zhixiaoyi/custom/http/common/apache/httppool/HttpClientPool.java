package com.zhixiaoyi.custom.http.common.apache.httppool;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * <p>
 * http连接池
 * </p>
 *
 * @author Waiting
 * @since 2018/5/16 20:28
 */
public class HttpClientPool {

    /**
     * 连接池管理
     */
    private static PoolingHttpClientConnectionManager poolingHttpClientCM;
    /**
     * 链接工具实例
     */
    private static CloseableHttpClient httpClient;


    private static void init() throws Exception {
        if (poolingHttpClientCM != null) {
            throw new Exception("已初始化，不能重复初始化");
        }
        HttpClientPoolConfig httpClientPoolConfig = new HttpClientPoolConfig();
        init(httpClientPoolConfig);
    }

    public static void init(HttpClientPoolConfig httpClientPoolConfig) throws Exception {
        if (poolingHttpClientCM != null) {
            throw new Exception("已初始化，不能重复初始化");
        }

        LayeredConnectionSocketFactory layeredConnectionSocketFactory;
        //忽略SSL验证
        if (httpClientPoolConfig.getIgnoreSSL()) {
            layeredConnectionSocketFactory = createIgnoreVerifySSLSF();
        } else {
            layeredConnectionSocketFactory = new SSLConnectionSocketFactory(SSLContext.getDefault());
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", layeredConnectionSocketFactory)
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build();
        poolingHttpClientCM = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 将最大连接数增加
        poolingHttpClientCM.setMaxTotal(httpClientPoolConfig.getPoolMaxTotal());
        // 将每个路由基础的连接增加
        poolingHttpClientCM.setDefaultMaxPerRoute(httpClientPoolConfig.getMaxPerRoute());
        /* // 将特定路由的最大连接数增加
        poolingHttpClientCM.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);*/
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(httpClientPoolConfig.getConnectionTimeout())
                .setConnectionRequestTimeout(httpClientPoolConfig.getRequestConnectionTimeout())
                .setSocketTimeout(httpClientPoolConfig.getSocketTimeout())
                // 默认允许自动重定向
                .setRedirectsEnabled(true).build();
        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = httpRequestRetryHandlerConfig();
        httpClient = HttpClients.custom()
                .setConnectionManager(poolingHttpClientCM)
                .setRetryHandler(httpRequestRetryHandler)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    /**
     * 设置请求重试处理
     */
    private static HttpRequestRetryHandler httpRequestRetryHandlerConfig() {
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                                        int executionCount, HttpContext context) {
                // 如果已经重试了5次，就放弃
                if (executionCount >= 5) {
                    return false;
                }
                // 如果服务器丢掉了连接，那么就重试
                if (exception instanceof NoHttpResponseException) {
                    return true;
                }
                // 不要重试SSL握手异常
                if (exception instanceof SSLHandshakeException) {
                    return false;
                }
                // 超时
                if (exception instanceof InterruptedIOException) {
                    return false;
                }
                // 目标服务器不可达
                if (exception instanceof UnknownHostException) {
                    return false;
                }
                // 连接被拒绝
                if (exception instanceof ConnectTimeoutException) {
                    return false;
                }
                // SSL握手异常
                if (exception instanceof SSLException) {
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        return httpRequestRetryHandler;
    }

    /**
     * 忽略SSL验证
     */
    private static LayeredConnectionSocketFactory createIgnoreVerifySSLSF() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        SSLContextBuilder builder = new SSLContextBuilder();
        // 全部信任 不做身份鉴定
        builder.loadTrustMaterial(null, new TrustStrategy() {

            public boolean isTrusted(X509Certificate[] x509Certificates, String s) {
                return true;
            }
        });
        LayeredConnectionSocketFactory layeredConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
        return layeredConnectionSocketFactory;
    }

    /**
     * 返回链接工具
     * 此处没有对工具特殊设置，并且本身是线程安全
     *
     * @return
     */
    public static CloseableHttpClient getHttpClient() throws Exception {
        if (poolingHttpClientCM == null) {
            init();
        }
        return httpClient;
    }
}
