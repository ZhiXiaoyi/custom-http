package com.zhixiaoyi.custom.http.entity;

/**
 * <p> 第三方http设置信息</p>
 *
 * @author ZhiXy
 * @since 2017-11-18 15:45
 */
public class ThirdApiHttpSetting {

    /**
     * 签名私钥
     */
    private String signPrivateKey;
    /**
     * 第三方url
     */
    private String thirdUrl;
    /**
     * 验签公钥
     */
    private String verifyPublicKey;

    public String getSignPrivateKey() {
        return signPrivateKey;
    }

    public void setSignPrivateKey(String signPrivateKey) {
        this.signPrivateKey = signPrivateKey;
    }

    public String getThirdUrl() {
        return thirdUrl;
    }

    public void setThirdUrl(String thirdUrl) {
        this.thirdUrl = thirdUrl;
    }

    public String getVerifyPublicKey() {
        return verifyPublicKey;
    }

    public void setVerifyPublicKey(String verifyPublicKey) {
        this.verifyPublicKey = verifyPublicKey;
    }

    @Override
    public String toString() {
        return "ThirdApiHttpSetting{" +
                "signPrivateKey='" + signPrivateKey + '\'' +
                ", thirdUrl='" + thirdUrl + '\'' +
                ", verifyPublicKey='" + verifyPublicKey + '\'' +
                '}';
    }
}
