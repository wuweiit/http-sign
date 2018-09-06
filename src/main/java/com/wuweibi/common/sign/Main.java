package com.wuweibi.common.sign;

/**
 * Created by marker on 2018/9/6.
 */

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 测试类
 *
 * @author marker
 *  2018-09-06 09:59
 **/
public final class Main {


    /**
     * 私有构造
     */
    private Main() { }


    /**
     *
     * @param args 参数
     */
    public static void main(String[] args) {



        Map<String, String> params = new HashMap<>();
        params.put("mobile", "13518135097");
        params.put("password", "123456");

        String signServer =  SignUtils.createSign(params, null, "123", false);


        System.out.println(signServer);

    }
}
