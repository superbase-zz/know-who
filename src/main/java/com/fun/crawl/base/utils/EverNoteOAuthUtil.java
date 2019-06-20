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
    static final String CALLBACK_URL = "https://supernote.com.cn/";

    public static OAuthService oAuthService( ) {
        String thisUrl = "https://supernote.com.cn/";
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
        System.out.println(authUrl);
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

        OAuthService oAuthService = oAuthService();
        System.out.println("token"+oAuthService.getRequestToken().getToken());
        System.out.println("secret"+oAuthService.getRequestToken().getSecret());
        System.out.println("URL"+oAuthService.getAuthorizationUrl(oAuthService.getRequestToken()));

    }
}
