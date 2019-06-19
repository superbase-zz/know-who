package com.fun.crawl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fun.crawl.base.service.impl.BaseServiceImpl;
import com.fun.crawl.mapper.PanUserMapper;
import com.fun.crawl.model.PanUser;
import com.fun.crawl.service.PanUserService;
import com.fun.crawl.base.utils.PanCoreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.fun.crawl.base.utils.PanCoreUtil.toMap;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-04-29
 */
@Service
public class PanUserServiceImpl extends BaseServiceImpl<PanUserMapper, PanUser> implements PanUserService {


    @Autowired
    private PanUserMapper panUserMapper;

    @Override
    public Map<String, String> getQrCodeUrl(HttpServletRequest request) {
        Map<String, String> codeSignAndCodeURL = PanCoreUtil.getCodeSignAndCodeURL(null);
        request.getSession().setAttribute("diyCookie", PanCoreUtil.standard_cookie);
        return codeSignAndCodeURL;
    }

    @Override
    public PanUser sysPanUser(String cookie, PanUser panUser) {
        PanCoreUtil.standard_cookie = cookie;
        ConcurrentHashMap<String, String> stringStringConcurrentHashMap = cookieMap(cookie);
        PanCoreUtil.standard_cookieMap = stringStringConcurrentHashMap;
//            PanCoreUtil.diskHome();
        Map<String, String> smap = PanCoreUtil.home();
        if (smap != null) {
            String bdstoken = smap.get("bdstoken");
            String userPaninfo = "";
            userPaninfo = PanCoreUtil.mapToJson(smap, false);
            Map<String, String> headMap = PanCoreUtil.xmlHttpHead();
            String headMapString = PanCoreUtil.mapToJson(headMap, false);
            panUser.setCookie(PanCoreUtil.standard_cookie)
                    .setCreateTime(new Date())
                    .setHeaders(headMapString)
                    .setUk(Long.valueOf(smap.get("uk")))
                    .setJsons(userPaninfo)
                    .setPanName(smap.get("username"))
                    .setModifyTime(new Date())
                    .setUid(0L);
            panUserMapper.updateById(panUser);
            return panUser;
        }
        return null;
    }


    @Override
    public Boolean unicast(String sign, HttpServletRequest request) {
        String diyCookie = (String) request.getSession().getAttribute("diyCookie");
        PanCoreUtil.standard_cookie = diyCookie;
        PanCoreUtil.standard_cookieMap = cookieMap(diyCookie);
        Map<String, String> map = PanCoreUtil.vertifyCodeUnicast(sign);
        if (map.containsKey("channel_v")) {
            String channel_v = map.get("channel_v");
            Map<String, String> channel_vmap = null;
            channel_vmap = toMap(channel_v);
            if ("0".equals(channel_vmap.get("status"))) {
                String v3Bduss = channel_vmap.get("v");
                Map<String, String> v3map = PanCoreUtil.v3Login(v3Bduss, null);
                String URL = PanCoreUtil.v3LoginAuthGetToken(null);
                PanCoreUtil.diskHome();
                PanCoreUtil.sendTodiskHomeOne(URL);
                Map<String, String> smap = PanCoreUtil.sendTodiskHomeTwo();
                String bdstoken = smap.get("bdstoken");
                String userPaninfo = "";
                userPaninfo = PanCoreUtil.mapToJson(smap, false);

                PanUser panUser = new PanUser();
                Map<String, String> headMap = PanCoreUtil.xmlHttpHead();
                String headMapString = PanCoreUtil.mapToJson(headMap, false);

                panUser.setCookie(PanCoreUtil.standard_cookie)
                        .setCreateTime(new Date())
                        .setHeaders(headMapString)
                        .setUk(Long.valueOf(smap.get("uk")))
                        .setJsons(userPaninfo)
                        .setPanName(smap.get("username"))
                        .setModifyTime(new Date())
                        .setUid(0L);
                boolean save = this.save(panUser);
                return save;

            }
        }
        return false;
    }

    @Override
    public PanUser selectByUk(Long uk) {
        QueryWrapper<PanUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PanUser::getUk, uk);
        PanUser panUser = panUserMapper.selectOne(queryWrapper);
        return panUser;
    }

    /**
     * 将cookie 转MAP
     *
     * @param tempcookie
     * @return
     */
    @Override
    public ConcurrentHashMap<String, String> cookieMap(String tempcookie) {
        ConcurrentHashMap<String, String> mapa = new ConcurrentHashMap<>();
        String[] temparray = tempcookie.split(";");
        for (int i = 0; i < temparray.length; i++) {
            String keyVal = temparray[i];
            String[] keyValarr = keyVal.split("=");
            mapa.put(keyValarr[0], keyValarr[1]);
        }
        return mapa;
    }


}
