package com.zhixiaoyi.custom.http.common;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * <p> 签名工具类</p>
 *
 * @author ZhiXy
 * @since 2017-11-15 16:10
 */
public class SignUtil {
    private static Logger logger = LoggerFactory.getLogger(SignUtil.class);
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final String CHARSET = "utf-8";

    public static String signByMD5(Map<String, Object> params, String key) {
        return DigestUtils.md5Hex(String.format("%s%s", createLinkString(paramsFilter(params)), key));
    }

    public static boolean verifyByMD5(Map<String, Object> params, String sign, String key) {
        String result = DigestUtils.md5Hex(String.format("%s%s", createLinkString(paramsFilter(params)), key));
        return result.equals(sign);
    }

    public static String signByRSA(Map<String, Object> params, String privateKey) {
        try {
            String content = createLinkString(paramsFilter(params));
            logger.debug("content=" + content);
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature
                    .getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(CHARSET));
            byte[] signed = signature.sign();
            return Base64.encodeBase64String(signed);
        } catch (Exception e) {
            logger.warn("sign rsa error", e);
        }
        return null;
    }

    public static boolean verifyByRSA(Map<String, Object> params, String sign, String publicKey) {
        try {
            String content = createLinkString(paramsFilter(params));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature
                    .getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(CHARSET));
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            logger.warn("vertify rsa error", e);
        }
        return false;
    }

    public static String getVerifyByRSAContent(Map<String, Object> params) {
        String content = createLinkString(paramsFilter(params));
        return content;
    }

    private static Map<String, String> paramsFilter(Map<String, Object> params) {
        Map<String, String> result = new HashMap<>();
        if (params == null || params.size() == 0) {
            return result;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() == null || "sign".equals(entry.getKey())) {
                continue;
            }
            result.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return result;
    }

    private static String createLinkString(Map<String, String> params) {
        if (params == null) {
            return "";
        }
        ArrayList<String> keys = new ArrayList(params.keySet());
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer();
        int keyLastNum = keys.size() - 1;
        for (int i = 0; i < keys.size(); ++i) {
            String key = keys.get(i);
            String value = String.valueOf(params.get(key));
            sb.append(key).append("=").append(value);
            if (i != keyLastNum) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

}
