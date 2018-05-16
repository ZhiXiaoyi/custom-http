package com.zhixiaoyi.custom.http;

import com.zhixiaoyi.custom.http.common.SignUtil;
import com.zhixiaoyi.custom.http.common.apache.CustomHttpClientUtils;
import com.zhixiaoyi.custom.http.common.apache.HttpClientUtil;
import com.zhixiaoyi.custom.http.entity.GatewayHttpSetting;
import com.zhixiaoyi.custom.http.entity.GatewayUtilResp;
import com.zhixiaoyi.custom.http.entity.ThirdApiHttpSetting;
import com.zhixiaoyi.custom.http.entity.ThirdApiResp;
import com.zhixiaoyi.custom.http.service.IGatewayConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import static com.alibaba.fastjson.JSON.parseObject;

/**
 * <p>
 * 用http请求网关工具
 * 用http请求第三方工具
 * </p>
 *
 * @author Waiting
 * @since 2018/5/16 21:01
 */
public class CustomReqUtil {
    private static Logger logger = LoggerFactory.getLogger(CustomReqUtil.class);
    /**
     * 网关返回成功状态值
     */
    public static final String GATEWAY_RESPCODE_SUCCESS = "01";

    /**
     * 请求网关
     */
    public static GatewayUtilResp gatewayPost(String version, String method, String data) throws Exception {
        logger.debug("GatewayUtilPost receive request,version={},method={},data={}", version, method, data);
        IGatewayConfigService gatewayHttpService = HttpToolBeanMgr.getGatewayHttpConfigService();
        GatewayHttpSetting gatewayHttpSetting = gatewayHttpService.getGatewayHttpSetting();
        logger.debug("GatewayUtilPost getGatewayHttpSetting={}", gatewayHttpSetting);
        String privateKey = gatewayHttpSetting.getSignPrivateKey();
        Map<String, Object> map = new HashMap<>(6);
        map.put("version", version);
        map.put("method", method);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("developerId", gatewayHttpSetting.getDeveloperId());
        map.put("data", data);
        String sign = SignUtil.signByRSA(map, privateKey);
        map.put("sign", sign);
        try {
            //因为请求网关是一个高频操作，故采用http连接池
            String rst = CustomHttpClientUtils.sendFormPost(gatewayHttpSetting.getGatewayUrl(), map);
            GatewayUtilResp respGateway = parseObject(rst, GatewayUtilResp.class);
            if (gatewayHttpSetting.getSignPrivateKey() != null && respGateway.getSign() != null) {
                Map<String, Object> vertifyMap = parseObject(rst, Map.class);
                respGateway.setSignFlag(SignUtil.verifyByRSA(vertifyMap, respGateway.getSign(), gatewayHttpSetting.getVerifyPublicKey()));
            }
            return respGateway;
        } catch (Exception e) {
            logger.warn("GatewayUtilPost receive request,version={},method={},data={}", version, method, data);
            logger.warn("GatewayUtilPost getGatewayHttpSetting={}", gatewayHttpSetting);
            logger.warn("GatewayUtilPost fail", e);
            throw e;
        }

    }

    /**
     * @param data
     * @return
     */
    public static ThirdApiResp thirdPost(ThirdApiHttpSetting thirdApiHttpSetting, Map<String, Object> data) throws Exception {
        logger.debug("ThirdAppUtilPost receive,thirdApiHttpSetting={} data={}", thirdApiHttpSetting, data);
        String privateKey = thirdApiHttpSetting.getSignPrivateKey();
        String sign = SignUtil.signByRSA(data, privateKey);
        Map<String, Object> map = new HashMap<>(2);
        map.put("data", data.toString());
        map.put("sign", sign);
        try {
            String rst = HttpClientUtil.sendFromPost(thirdApiHttpSetting.getThirdUrl(), map);
            ThirdApiResp thirdApiResp = parseObject(rst, ThirdApiResp.class);
            if (thirdApiHttpSetting.getVerifyPublicKey() != null && thirdApiResp.getSign() != null) {
                Map<String, Object> verifyMap = parseObject(rst, Map.class);
                thirdApiResp.setSignFlag(SignUtil.verifyByRSA(verifyMap, thirdApiResp.getSign(), thirdApiHttpSetting.getVerifyPublicKey()));
            }
            return thirdApiResp;
        } catch (Exception e) {
            logger.warn("ThirdAppUtilPost receive,thirdApiHttpSetting={} data={}", thirdApiHttpSetting, data);
            logger.warn("ThirdAppUtilPost fail", e);
            throw e;
        }
    }

}
