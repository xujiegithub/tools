package top.xujie.tools.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author xujie
 */
@Slf4j
public class HttpClientUtil {

    public static String doGet(String url, Map<String, String> param) {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doGet(String url) {
        return doPostString(url, null);
    }

    public static String doPostString(String url, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static String doPost(String url) {
        return doPostString(url, null);
    }

    public static String doPostJson(String url, String json) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static void main(String[] args) throws Exception {
        List<String> strings = testBlack();
        String phoneString = listToString(strings, ',');
        String accessKey = "test1";
        String accessSecret = "test1";
        requestBlackList(phoneString,accessKey,accessSecret);
    }

    private static void requestBlackList(String phoneString,String accessKey,String accessSecret) throws Exception {

        long start=System.currentTimeMillis();
        String url = "https://api.huiyu.org.cn/api/queryBlacklist";
        long timestamp = System.currentTimeMillis();

        String sign = DigestUtils.md5Hex(accessKey + accessSecret + timestamp);

        Map<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey);
        map.put("timestamp", String.valueOf(timestamp));
        map.put("sign", sign);
        map.put("phones", phoneString);

        String json = HttpClientUtil.doPost(url, map);

        System.out.println(json);
        long end=System.currentTimeMillis();
        System.out.println(end-start);
    }

    public static List<String> testBlack() {
        List<Integer> phones = Collections.singletonList(130);
//        String path = "/mnt/d6/phone-sha256/phone-sha256.txt";
        Random random = new Random();
        List<String> phonesList = new ArrayList<>();

        for (Integer phone : phones) {

            String head = String.valueOf(phone);
            long start = Long.parseLong(head + "00000000");
            long end = Long.parseLong(head + "00050000");
            for (long i = start; i < end; i++) {
                phonesList.add(String.valueOf(i + random.nextInt(99999999)));
            }
        }
        return phonesList;
    }

    public static String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setSocketTimeout(300000).setConnectTimeout(300000).build();
    public static String doPost(String url, Map<String, String> paramsMap) throws Exception {
        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

        }
        String result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
        //设置请求和传输超时时间
        httpPost.setConfig(REQUEST_CONFIG);
        CloseableHttpResponse httpResp = httpclient.execute(httpPost);
        try {
            int statusCode = httpResp.getStatusLine().getStatusCode();
            // 判断是够请求成功
            if (statusCode == HttpStatus.SC_OK) {
                // 获取返回的数据
                result = EntityUtils.toString(httpResp.getEntity(), Consts.UTF_8);
            }
        } finally {
            httpResp.close();
            httpclient.close();
        }
        return result;
    }
}
