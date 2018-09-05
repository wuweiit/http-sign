package com.wuweibi.common.sign;

import com.google.common.collect.Lists;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <pre>
 * 签名相关工具类.
 * </pre>
 *
 * @author wangx
 */
public final class SignUtils {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SignUtils.class);


    private SignUtils() {
    }



    /**
     * 微信支付签名算法(详见:https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=4_3).
     *
     * @param params         参数信息
     * @param signType       签名类型，如果为空，则默认为MD5
     * @param signKey        签名Key
     * @param ignoreSignType 签名时，是否忽略signType
     * @return 签名字符串
     */
    public static String createSign(Map<String, String> params, String signType, String signKey, boolean ignoreSignType) {
        SortedMap<String, String> sortedMap = new TreeMap<>(params);

        StringBuilder toSign = new StringBuilder();
        for (String key : sortedMap.keySet()) {
            String value = params.get(key);
            boolean shouldSign = false;
            if (ignoreSignType && "sign_type".equals(key)) {
                shouldSign = false;
            } else if (StringUtils.isNotEmpty(value)
                    && !Lists.newArrayList("sign", "key", "xmlString", "xmlDoc", "couponList").contains(key)) {
                shouldSign = true;
            }

            if (shouldSign) {
                toSign.append(key).append("=").append(value).append("&");
            }
        }

        toSign.append("key=").append(signKey);
        LOGGER.debug("toSign Str= {}", toSign);
        String toSignEncoder = "";
        try {
            toSignEncoder = URLEncoder.encode(toSign.toString(), "utf-8");
            // // "; / ? : @ & = + $ , #"
            toSignEncoder = toSignEncoder.replaceAll("\\+", "%20");
            toSignEncoder = toSignEncoder.replaceAll("@", "%40");
            toSignEncoder = toSignEncoder.replaceAll("#", "%23");
            toSignEncoder = toSignEncoder.replaceAll("&", "%26");
            toSignEncoder = toSignEncoder.replaceAll("\\?", "%3F");
            toSignEncoder = toSignEncoder.replaceAll("%21", "!");
            LOGGER.debug("toSignEncoder Str= {}", toSignEncoder);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("{}", e.getMessage());
        }
        if (SignType.HMAC_SHA256.equals(signType)) {
            return createHmacSha256Sign(toSignEncoder, signKey);
        } else {
            return DigestUtils.md5Hex(toSignEncoder).toUpperCase();
        }
    }


    /**
     * 创建签名
     *
     * @param message 加密内容
     * @param key     key
     * @return String
     */
    private static String createHmacSha256Sign(String message, String key) {
        try {
            Mac sha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256.init(secretKeySpec);
            byte[] bytes = sha256.doFinal(message.getBytes());
            return Hex.encodeHexString(bytes).toUpperCase();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }


    /**
     * 校验签名是否正确.
     *
     * @param params   需要校验的参数Map
     * @param signType 签名类型，如果为空，则默认为MD5
     * @param signKey  校验的签名Key
     * @return true - 签名校验成功，false - 签名校验失败
     */
    public static boolean checkSign(Map<String, String> params, String signType, String signKey) {
        String sign = createSign(params, signType, signKey, false);
        return sign.equals(params.get("sign"));
    }


}
