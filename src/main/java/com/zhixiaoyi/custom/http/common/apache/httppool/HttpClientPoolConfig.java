package com.zhixiaoyi.custom.http.common.apache.httppool;

/**
 * <p>
 * http连接池配置
 * </p>
 *
 * @author Waiting
 * @since 2018/5/16 20:31
 */
public class HttpClientPoolConfig {
    /**
     * 链接创建超时时间 毫秒
     */
    private static int defaultConnectionTimeout = 30000;
    /**
     * 等待响应超时时间（两次响应间隔时间） 毫秒
     */
    private static int defaultSocketTimeout = 10000;
    /**
     * 从链接池获取链接超时时间 毫秒
     */
    private static int defaultRequestConnectionTimeout = 60000;
    /**
     * 链接池最大链接数
     */
    private static int defaultPoolMaxTotal = 200;
    /**
     * 每个路由最大连接数（链接主机和端口组成一个路由）
     */
    private static int defaultMaxPerRoute = 20;
    /**
     * https时是否绕过ssl
     */
    private static boolean defaultIsIgnoreSSL = false;

    /**
     * 链接创建超时时间 毫秒
     */
    private Integer connectionTimeout;

    /**
     * 等待响应超时时间（两次响应间隔时间） 毫秒
     */
    private Integer socketTimeout;

    /**
     * 从链接池获取链接超时时间 毫秒
     */
    private Integer requestConnectionTimeout;

    /**
     * 链接池最大链接数
     */
    private Integer poolMaxTotal;

    /**
     * 每个路由最大连接数（链接主机和端口组成一个路由）
     */
    private Integer maxPerRoute;

    /**
     * https时是否绕过ssl
     */
    private Boolean isIgnoreSSL;

    public Integer getConnectionTimeout() {
        return connectionTimeout == null ? defaultConnectionTimeout : connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getSocketTimeout() {
        return socketTimeout == null ? defaultSocketTimeout : socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Integer getRequestConnectionTimeout() {
        return requestConnectionTimeout == null ? defaultRequestConnectionTimeout : requestConnectionTimeout;
    }

    public void setRequestConnectionTimeout(Integer requestConnectionTimeout) {
        this.requestConnectionTimeout = requestConnectionTimeout;
    }

    public Integer getPoolMaxTotal() {
        return poolMaxTotal == null ? defaultPoolMaxTotal : poolMaxTotal;
    }

    public void setPoolMaxTotal(Integer poolMaxTotal) {
        this.poolMaxTotal = poolMaxTotal;
    }

    public Integer getMaxPerRoute() {
        return maxPerRoute == null ? defaultMaxPerRoute : maxPerRoute;
    }

    public void setMaxPerRoute(Integer maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public Boolean getIgnoreSSL() {
        return isIgnoreSSL == null ? defaultIsIgnoreSSL : isIgnoreSSL;
    }

    public void setIgnoreSSL(Boolean ignoreSSL) {
        isIgnoreSSL = ignoreSSL;
    }

    @Override
    public String toString() {
        return "HttpClientPoolConfig{" +
                "connectionTimeout=" + connectionTimeout +
                ", socketTimeout=" + socketTimeout +
                ", requestConnectionTimeout=" + requestConnectionTimeout +
                ", poolMaxTotal=" + poolMaxTotal +
                ", maxPerRoute=" + maxPerRoute +
                ", isIgnoreSSL=" + isIgnoreSSL +
                '}';
    }
}
