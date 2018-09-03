/**
 * Created by marker on 2018/8/21.
 */

import com.wuweibi.common.sign.SignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author marker
 * @create 2018-08-21 15:34
 **/
public class TestParams {


    static Logger logger = LoggerFactory.getLogger(TestParams.class);

    public static void main(String[] args) throws UnsupportedEncodingException {


        Map<String, String> params = new HashMap<>();
        params.put("mobile", "13518135097");
        params.put("password", "123456");

        String signServer =  SignUtils.createSign(params, null, "123",false);


        System.out.println(signServer);
    }

}
