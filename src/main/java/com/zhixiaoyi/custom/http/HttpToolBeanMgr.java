package com.zhixiaoyi.custom.http;

import com.zhixiaoyi.custom.http.common.apache.httppool.HttpClientPool;
import com.zhixiaoyi.custom.http.common.apache.httppool.HttpClientPoolConfig;
import com.zhixiaoyi.custom.http.service.IGatewayConfigService;

/**
 * <p>
 * 引用方需实现服务
 * </p>
 *
 * @author Waiting
 * @since 2018/5/16 21:02
 */
public class HttpToolBeanMgr {
    private static IGatewayConfigService gatewayConfigService;

    public HttpToolBeanMgr(IGatewayConfigService gatewayConfigService) throws Exception {
        HttpClientPoolConfig httpClientPoolConfig = new HttpClientPoolConfig();
        httpClientPoolConfig.setIgnoreSSL(true);
        HttpClientPool.init(httpClientPoolConfig);
        HttpToolBeanMgr.gatewayConfigService = gatewayConfigService;
    }

    public static IGatewayConfigService getGatewayHttpConfigService() {
        return gatewayConfigService;
    }
}
