package com.zhixiaoyi.custom.http.entity;

import java.io.Serializable;

/**
 * <p> 网关返回</p>
 * @author ZhiXy
 * @since 2017-11-15 11:28
 */
public class GatewayUtilResp implements Serializable {
    /**
     * 网关返回码
     */
    private String respCode;
    /**
     * 网关返回消息
     */
    private String respMsg;
    /**
     * 服务返回码
     */
    private String subCode;
    /**
     * 服务返回消息
     */
    private String subMsg;
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

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public String getSubMsg() {
        return subMsg;
    }

    public void setSubMsg(String subMsg) {
        this.subMsg = subMsg;
    }

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
        return "GatewayUtilResp{" +
                "respCode='" + respCode + '\'' +
                ", respMsg='" + respMsg + '\'' +
                ", subCode='" + subCode + '\'' +
                ", subMsg='" + subMsg + '\'' +
                ", data='" + data + '\'' +
                ", sign='" + sign + '\'' +
                ", signFlag=" + signFlag +
                '}';
    }
}
