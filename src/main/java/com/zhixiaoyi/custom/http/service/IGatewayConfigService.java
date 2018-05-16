package com.zhixiaoyi.custom.http.service;


import com.zhixiaoyi.custom.http.entity.GatewayHttpSetting;

/**
 * <p> 网关http接口</p>
 *
 * @author ZhiXy
 * @since 2017-11-18 14:18
 */
public interface IGatewayConfigService {

    GatewayHttpSetting getGatewayHttpSetting();

}
