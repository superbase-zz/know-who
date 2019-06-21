package com.fun.crawl.base.utils;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.EvernoteApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.HashMap;
import java.util.Map;

public class EverNoteOAuthUtil {

    static final String CONSUMER_KEY = "zhishihuiju";
    static final String CONSUMER_SECRET = "1a43a041e38ac857";
    static final EvernoteService EVERNOTE_SERVICE = EvernoteService.YINXIANG;
    static final String CALLBACK_URL = "https://127.0.0.1:9002/";

    public static OAuthService oAuthService( ) {

        //String thisUrl = request.getRequestURL().toString();
        String cbUrl =  CALLBACK_URL;

        Class<? extends EvernoteApi> providerClass = EvernoteYingXiangApi.class;

        OAuthService service = new ServiceBuilder()
                .provider(providerClass)
                .apiKey(CONSUMER_KEY)
                .apiSecret(CONSUMER_SECRET)
                .callback(cbUrl)
                .build();
        return service;
    }

    /**
     *
     * EverNote OAuth 授权第一步
     *
     * 该方法获三个数据：
     * 1、获取跳转授权页面的连接 authUrl
     * 2、获取 authUrl 和第三步中所需要的 oauthToken
     * 3、获取第三步中需要要的 oauthTokenSecret
     *
     * @return requestMap
     */
    public static Map<String, String> getRequestToken(){
        OAuthService service = oAuthService();
        Token requestToken = service.getRequestToken();
        String oauthToken = requestToken.getToken();
        String oauthTokenSecret = requestToken.getSecret();
        String authUrl = service.getAuthorizationUrl(requestToken);
        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("oauthToken", oauthToken);
        requestMap.put("oauthTokenSecret", oauthTokenSecret);
        requestMap.put("authUrl", authUrl);
        return requestMap;
    }

    /**
     * EverNote OAuth 授权第三步
     *
     * 该方法获取连个数据
     * 1、获取用户访问令牌 accessToken
     * 2、获取 noteStoreUrl
     *
     * @param oauthToken 第一步获取到的值
     * @param oauthTokenSecret 第一步获取到的值
     * @param oauthVerifier 第二步获取到的值
     * (第二步是网页端的操作，网页端使用第一步返回的 authUrl 连接进行登录授权，授权成功后回调的浏览器地址栏里面会看到该参数)
     * @return accessMap
     */
    public static Map<String, String> getAccessToken (String oauthToken,
                                                      String oauthTokenSecret, String oauthVerifier){
        OAuthService service = oAuthService();
        Token scribeRequestToken = new Token(oauthToken, oauthTokenSecret);
        Verifier scribeVerifier = new Verifier(oauthVerifier);
        Token scribeAccessToken = service.getAccessToken(scribeRequestToken, scribeVerifier);
        System.out.println(scribeAccessToken.getRawResponse());
        EvernoteAuth evernoteAuth = EvernoteAuth.parseOAuthResponse(EVERNOTE_SERVICE, scribeAccessToken.getRawResponse());
        String accessToken = evernoteAuth.getToken();
        String noteStoreUrl = evernoteAuth.getNoteStoreUrl();
        Map<String, String> accessMap = new HashMap<String, String>();
        accessMap.put("accessToken", accessToken);
        accessMap.put("noteStoreUrl", noteStoreUrl);
        return accessMap;
    }

    public static void main(String[] args) {

        Map<String, String> requestToken = getRequestToken();

        String oauthToken=requestToken.get("oauthToken");
        String oauthTokenSecret=requestToken.get("oauthTokenSecret");
        String authUrl=requestToken.get("authUrl");


        System.out.println("token   :"+oauthToken);
        System.out.println("secret   :"+oauthTokenSecret);
        System.out.println("URL   :"+authUrl);
//        https://127.0.0.1:9002/?oauth_token=zhishihuiju.16B78BEACC2.68747470733A2F2F3132372E302E302E313A393030322F.E85DFC56F5F8244D951307E09BB73B03&oauth_verifier=F6A91534AFA5764884A1288D178C3173&sandbox_lnb=false
//        String url="https://127.0.0.1:9002/?oauth_token=zhishihuiju.16B77D96E61.68747470733A2F2F3132372E302E302E313A393030322F.C62A49B08688F2CEACC657E8D6A42201&oauth_verifier=A8EAD1D3F62657167C6E9E6D27A3D6F9&sandbox_lnb=false";
//
        Map<String, String> res = getAccessToken("zhishihuiju.16B78BEACC2.68747470733A2F2F3132372E302E302E313A393030322F.E85DFC56F5F8244D951307E09BB73B03", "177B1804555E1E2DD5CCD8ECF8381AAB", "F6A91534AFA5764884A1288D178C3173");
        System.out.println(res);

    }
}
