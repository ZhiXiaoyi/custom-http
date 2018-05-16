package com.zhixiaoyi.custom.http.entity;

import java.io.Serializable;

/**
 * <p> 第三方返回</p>
 * @author ZhiXy
 * @since 2017-11-15 11:28
 */
public class ThirdApiResp implements Serializable {

    /**
     * 网关返回码
     */
    private String respCode;
    /**
     * 网关返回消息
     */
    private String respMsg;
    /**
     * 返回数据
     */
    private String data;
    /**
     * 返回签名
     */
    private String sign;
    /**
     * 是否验签
     */
    private Boolean signFlag;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Boolean getSignFlag() {
        return signFlag;
    }

    public void setSignFlag(Boolean signFlag) {
        this.signFlag = signFlag;
    }

    @Override
    public String toString() {
        return "ThirdApiResp{" +
                "respCode='" + respCode + '\'' +
                ", respMsg='" + respMsg + '\'' +
                ", data='" + data + '\'' +
                ", sign='" + sign + '\'' +
                ", signFlag=" + signFlag +
                '}';
    }
}
