package com.fun.crawl.base.utils;

public class ConstantUtils {

    public static final String LOGIN_JS_TEXT = "function getGid() {return \"xxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx\".replace(/[xy]/g, function (e) {var t = 16 * Math.random() | 0, n = \"x\" == e ? t : 3 & t | 8;return n.toString(16)}).toUpperCase()}function  getCallback() {return \"bd__cbs__\" + Math.floor(2147483648 * Math.random()).toString(36)}\n"; //cookie中sessionid
    public static final String SIGN_JS_TEXT = "function s(j,r){var a=[];var p=[];var o=\"\";var v=j.length;for(var q=0;q<256;q++){a[q]=j.substr((q%v),1).charCodeAt(0);p[q]=q}for(var u=q=0;q<256;q++){u=(u+p[q]+a[q])%256;var t=p[q];p[q]=p[u];p[u]=t}for(var i=u=q=0;q<r.length;q++){i=(i+1)%256;u=(u+p[i])%256;var t=p[i];p[i]=p[u];p[u]=t;k=p[((p[i]+p[u])%256)];o+=String.fromCharCode(r.charCodeAt(q)^k)}return o};";
    public static final String BASE_64_JS_TEXT = "function base64(t) {var e, r, a, o, n, i, s = \"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\";for (a = t.length, r = 0, e = \"\"; a > r; ) {if (o = 255 & t.charCodeAt(r++), r == a) {e += s.charAt(o >> 2), e += s.charAt((3 & o) << 4), e += \"==\";break}if (n = t.charCodeAt(r++), r == a) {e += s.charAt(o >> 2), e += s.charAt((3 & o) << 4 | (240 & n) >> 4), e += s.charAt((15 & n) << 2), e += \"=\";break}i = t.charCodeAt(r++), e += s.charAt(o >> 2), e += s.charAt((3 & o) << 4 | (240 & n) >> 4), e += s.charAt((15 & n) << 2 | (192 & i) >> 6), e += s.charAt(63 & i)}return e}";
    public static final String makePrivatePassword_TEXT = "function makePrivatePassword() {\n" +
            "            var e = [\"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"a\", \"b\", \"c\", \"d\", \"e\", \"f\", \"g\", \"h\", \"i\", \"j\", \"k\", \"m\", \"n\", \"p\", \"q\", \"r\", \"s\", \"t\", \"u\", \"v\", \"w\", \"x\", \"y\", \"z\"]\n" +
            "              , i = function(e, i) {\n" +
            "                return Math.round((i - e) * Math.random() + e)\n" +
            "            }\n" +
            "              , t = function(t) {\n" +
            "                for (var s = [], a = 1; t >= a; a++)\n" +
            "                    s.push(e[i(0, e.length - 1)]);\n" +
            "                return s.join(\"\")\n" +
            "            };\n" +
            "            return t(4)\n" +
            "        }";
}
