package com.fun.crawl.base.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.fun.crawl.base.utils.VisitApiUtil.mapToPostStr;

@Slf4j
public class EvenNoteHttpUtils {

    private static final String CHARSET_NAME = "UTF-8";
    private static final String POST_TPYE = "multipart/form-data; boundary=" + "------WebKitFormBoundary7oxyhWkQLfYSlaeV--";

    public static final String HOST = "https://app.yinxiang.com";//访问订单系统接口的地址

    public static final String consumer_key = "zhishihuiju";//key
    public static final String consumer_secret = "1a43a041e38ac857";//key


    private static final OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .followRedirects(false)
            .followSslRedirects(false)
            .pingInterval(30, TimeUnit.SECONDS).build();

    /**
     * 该不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    public static void enqueue(Request request, Callback responseCallback) {
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param request
     */
    public static void enqueue(Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    /**
     * 这里使用了HttpClinet的API。只是为了方便
     *
     * @param params
     * @return
     */
    public static String formatParams(List<BasicNameValuePair> params) {
        return URLEncodedUtils.format(params, CHARSET_NAME);
    }

    /**
     * 为HttpGet 的 url 方便的添加多个name value 参数。
     *
     * @param url
     * @param params
     * @return
     */
    public static String attachHttpGetParams(String url, List<BasicNameValuePair> params) {
        return url + "?" + formatParams(params);
    }

    /**
     * 接口请求返回response
     *
     * @param host      请求接口的域名地址
     * @param apiUrl    请求接口名称，格式如："/api/list"
     * @param inputMap  参数集合Map<key,value>，key参数名称，value参数值,都是字符串
     * @param method    提交方式：DELETE，POST，PUT，GET,"GET"直接地址请求，默认："POST"
     * @param onlyValue 请求参数只有一个的时候的值(GET请求用到，其他请求为null即可)
     * @return 响应结果
     */
    public static Response getRequest(String host, String apiUrl, Map<String, String> params, Map<String, String> headers) {
        Request request = null;
        Request.Builder uilder = new Request.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                uilder.addHeader(header.getKey(), header.getValue());
            }
        }
        String requestURL = getRequestURL(host, apiUrl);
        if (params != null) {
            requestURL = requestURL + mapToGetString(params, false);
        }
        uilder.url(requestURL);
        request = uilder.get().build();

        try {
            Response response = execute(request);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 接口请求返回response
     * 使用xmlHttpHead xml_headMap 头部信息
     *
     * @param host      请求接口的域名地址
     * @param apiUrl    请求接口名称，格式如："/api/list"
     * @param inputMap  参数集合Map<key,value>，key参数名称，value参数值,都是字符串
     * @param method    提交方式：DELETE，POST，PUT，GET,"GET"直接地址请求，默认："POST"
     * @param onlyValue 请求参数只有一个的时候的值(GET请求用到，其他请求为null即可)
     * @return 响应结果
     */
    public static String visit(String host, String apiUrl, Map<String, String> inputMap, String method) {
        log.info("visit,请求方式：" + method + ",请求接口：" + apiUrl + ",请求参数：" + inputMap);
        String jsonStr = null;
        long startTime = System.currentTimeMillis();
        Map<String, String> headers;

        Request request = null;
        Request.Builder uilder = new Request.Builder();

        String requestURL = getRequestURL(host, apiUrl);

        try {
            Map<String, String> requestMap = inputMap;//请求参数

            if ("GET".equals(method.toUpperCase())) {
                if (requestMap != null) {
                    requestURL = requestURL + mapToGetString(requestMap, false);
                }
            }
            uilder.url(requestURL);
            if ("GET".equals(method.toUpperCase())) {
                request = uilder.get().build();
            } else if ("POST_STRING".equals(method.toUpperCase())) {
                FormBody.Builder builder = new FormBody.Builder();
                FormBody formBody = builder.build();
                request = uilder.post(formBody).build();
            } else if ("POST_PARM".equals(method.toUpperCase())) {
                FormBody.Builder builder = new FormBody.Builder();
                //java 8 遍历map entry
                requestMap.entrySet().forEach(key -> builder.add(key.getKey(), key.getValue()));
                FormBody formBody = builder.build();
                request = uilder.post(formBody).build();
            } else {
                uilder.addHeader("content-type", POST_TPYE);
                RequestBody body = RequestBody.create(MediaType.parse(POST_TPYE), mapToPostStr(requestMap));//请求参数
                if ("DELETE".equals(method.toUpperCase())) {
                    request = uilder.delete(body).build();
                } else if ("POST".equals(method.toUpperCase())) {
                    request = uilder.post(body).build();
                } else if ("PUT".equals(method.toUpperCase())) {
                    request = uilder.put(body).build();
                }
            }
            Response response = execute(request);
            jsonStr = response.body().string();
        } catch (Exception e) {
            log.error("req,请求接口异常", e);
        } finally {
            log.info("req请求接口：" + apiUrl + ",响应时间为" + ((System.currentTimeMillis() - startTime)) + "ms,响应结果：" + jsonStr);
        }
        return jsonStr;
    }

    /**
     * map集合转json字符串
     *
     * @param inputMap 集合
     * @param boo      true表示进行转码，false表示不进行转码
     * @return 字符串
     * @throws UnsupportedEncodingException
     */
    public static String mapToJson(Map<String, String> inputMap, boolean boo) {
        String res = null;
        if (inputMap != null && inputMap.size() > 0) {
            JSONObject js = new JSONObject();
            Iterator<String> iterator = inputMap.keySet().iterator();
            while (iterator.hasNext()) {
                String name = String.valueOf(iterator.next());
                String value = String.valueOf(inputMap.get(name));
                if (value == null) {
                    value = "";
                }
                if (name != null) {
                    try {
                        js.put(name, value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (js.length() > 0) {
                res = js.toString();
                if (boo) {
                    try {
                        res = URLEncoder.encode(res, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        log.error("字符中ENCODE失败", e);
                    }
                }
            }
        }
        return res;
    }

    /**
     * GET 字符方法构建
     * map集合转  String字符串
     *
     * @param inputMap 集合
     * @param boo      true表示进行转码，false表示不进行转码
     * @return 字符串
     * @throws UnsupportedEncodingException
     */
    public static String mapToGetString(Map<String, String> inputMap, boolean boo) {
        String res = null;
        if (inputMap != null && inputMap.size() > 0) {
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = inputMap.keySet().iterator();
            int sum = 0;
            while (iterator.hasNext()) {
                String name = String.valueOf(iterator.next());
                String value = String.valueOf(inputMap.get(name));
                if (value == null) {
                    value = "";
                }
                if (name != null) {
                    if (sum == 0) {
                        sb.append("?");
                    } else {
                        sb.append("&");
                    }
                    sum++;
                    sb.append(name);
                    sb.append("=");
                    if (boo) {
                        try {
                            value = URLEncoder.encode(value, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            log.error("字符中ENCODE失败", e);
                        }
                    }
                    sb.append(value);
                }
            }
            if (sb.length() > 0) {
                res = sb.toString();
            }
        }
        return res;
    }


    /**
     * GET 字符方法构建
     * map集合转  String字符串
     *
     * @param inputMap 集合
     * @param boo      true表示进行转码，false表示不进行转码
     * @return 字符串
     * @throws UnsupportedEncodingException
     */
    public static String mapToPostString(Map<String, String> inputMap, boolean boo) {
        String res = null;
        if (inputMap != null && inputMap.size() > 0) {
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = inputMap.keySet().iterator();
            int sum = 0;
            while (iterator.hasNext()) {
                String name = String.valueOf(iterator.next());
                String value = String.valueOf(inputMap.get(name));
                if (value == null) {
                    value = "";
                }
                if (name != null) {
                    if (sum == 0) {
                        sb.append("");
                    } else {
                        sb.append("&");
                    }
                    sum++;
                    sb.append(name);
                    sb.append("=");
                    if (boo) {
                        try {
                            value = URLEncoder.encode(value, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            log.error("字符中ENCODE失败", e);
                        }
                    }
                    sb.append(value);
                }
            }
            if (sb.length() > 0) {
                res = sb.toString();
            }
        }
        return res;
    }


    /**
     * GET 字符方法构建
     * map集合转  String字符串
     *
     * @param inputMap 集合
     * @param boo      true表示进行转码，false表示不进行转码
     * @return 字符串
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> mapToToEncode(Map<String, String> inputMap) {
        Map<String, String> result = new HashMap<String, String>();
        if (inputMap != null && inputMap.size() > 0) {
            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = inputMap.keySet().iterator();
            int sum = 0;
            while (iterator.hasNext()) {
                String name = String.valueOf(iterator.next());
                String value = String.valueOf(inputMap.get(name));
                if (value == null) {
                    value = "";
                }
                if (name != null) {
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        log.error("字符中ENCODE失败", e);
                    }
                    result.put(name, value);
                }
            }

        }
        return result;
    }

    /**
     * 将Json对象转换成Map
     *
     * @param jsonObject json对象
     * @return Map对象
     * @throws JSONException
     */
    public static Map<String, String> toMap(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, String> result = new HashMap<String, String>();
        Iterator<?> iterator = jsonObject.keys();
        String key = null;
        String value = null;
        while (iterator.hasNext()) {
            key = String.valueOf(iterator.next());
            try {
                value = String.valueOf(jsonObject.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            result.put(key, value);
        }
        return result;
    }

    /**
     * 组合请求地址
     *
     * @param apiUrl
     * @return
     */
    public static String getRequestURL(String host, String apiUrl) {
        if (StringUtils.isEmpty(apiUrl)) {
            return host;
        }
        if (apiUrl.indexOf("/") == 0
                && (host.lastIndexOf("/") == (host.length() - 1))) {
            return host.substring(0, host.lastIndexOf("/")) + apiUrl;
        } else if (apiUrl.indexOf("/") != 0
                && (host.lastIndexOf("/") != (host.length() - 1))) {
            return host + "/" + apiUrl;
        } else {
            return host + apiUrl;
        }
    }


    public static String streamToStr(InputStream inputStream, String chartSet) {

        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, chartSet));
            String con;
            while ((con = br.readLine()) != null) {
                builder.append(con);
            }
            br.close();
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "";
    }


    /**
     * @param host
     * @return
     * @xml_headMap apiUrl
     */
    public static String subJStoJson(String JsStr) {
        int front = JsStr.indexOf("(");
        int last = JsStr.indexOf(")");
        String substring = JsStr.substring(front + 1, last);
        return substring;
    }

    /**
     * 将url参数转换成map
     *
     * @param param aa=11&bb=22&cc=33
     * @return
     */
    public static Map<String, Object> getUrlParams(String param) {
        Map<String, Object> map = new HashMap<String, Object>(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }


    public static void main(String[] args) throws IOException {
       String collbak= "localhost:9002/callback";
        Map<String,String> qmap=new TreeMap<>();
        qmap.put("oauth_callback",collbak);
        qmap.put("oauth_consumer_key",consumer_key);
        qmap.put("oauth_nonce",UUIDGen.getEightRandom());
        qmap.put("oauth_signature_method","HMAC-SHA1");
        qmap.put("oauth_timestamp",System.currentTimeMillis()/1000+"");
        qmap.put("oauth_version","1.0");

        String getString = mapToGetString(qmap, false);

        String urlString="GET&"+HOST+"/oauth"+getString;
        System.out.println(urlString);
        try {
            urlString = URLEncoder.encode(urlString, "UTF-8");
            collbak = URLEncoder.encode(collbak, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("字符中ENCODE失败", e);
        }

        System.out.println(urlString);


        try {
            String s = HMAC.hmacSha1Encrypt(urlString, consumer_secret);
            System.out.println(s);
            qmap.put("oauth_signature",s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        qmap.put("oauth_callback",collbak);


        String get = visit(HOST, "/oauth", qmap, "GET");

        System.out.println(get);
    }


}
