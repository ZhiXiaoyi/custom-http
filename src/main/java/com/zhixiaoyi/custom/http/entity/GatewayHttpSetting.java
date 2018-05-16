package com.zhixiaoyi.custom.http.entity;

/**
 * <p> 网关http设置信息</p>
 *
 * @author ZhiXy
 * @since 2017-11-18 14:24
 */
public class GatewayHttpSetting {
    /**
     * 网关地址
     */
    private String gatewayUrl;
    /**
     * 开发者Id
     */
    private String developerId;
    /**
     * 签名私钥
     */
    private String signPrivateKey;
    /**
     * 验证签名公钥
     */
    private String verifyPublicKey;

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }

    public String getSignPrivateKey() {
        return signPrivateKey;
    }

    public void setSignPrivateKey(String signPrivateKey) {
        this.signPrivateKey = signPrivateKey;
    }

    public String getVerifyPublicKey() {
        return verifyPublicKey;
    }

    public void setVerifyPublicKey(String verifyPublicKey) {
        this.verifyPublicKey = verifyPublicKey;
    }

    @Override
    public String toString() {
        return "GatewayHttpSetting{" +
                "gatewayUrl='" + gatewayUrl + '\'' +
                ", developerId='" + developerId + '\'' +
                ", signPrivateKey='" + signPrivateKey + '\'' +
                ", verifyPublicKey='" + verifyPublicKey + '\'' +
                '}';
    }
}
