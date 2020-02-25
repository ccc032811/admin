package com.neefull.fsp.web.job.task;

import com.neefull.fsp.web.common.exception.FebsException;
import com.neefull.fsp.web.job.entity.AdminJob;
import com.neefull.fsp.web.job.mapper.AdminJobMapper;
import com.neefull.fsp.web.system.entity.ServeMenu;
import com.neefull.fsp.web.system.service.IServeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @Author: chengchengchu
 * @Date: 2020/2/7  18:12
 */
@Slf4j
@Component
public class Monotor {

    @Autowired
    private IServeService serveService;

    public void monotor() {
        List<ServeMenu> allServeMenus = serveService.getAllServeMenus();

        if(CollectionUtils.isNotEmpty(allServeMenus)) {
            for (ServeMenu serveMenu : allServeMenus) {
                CloseableHttpResponse response = null;
                CloseableHttpClient httpclient = HttpClients.createDefault();
                try {
                    HttpGet httpget = new HttpGet(serveMenu.getAdress());
                    response = httpclient.execute(httpget);

                    if (response.getStatusLine().getStatusCode() == 200) {
                        serveMenu.setAlive(1);
                        serveService.updateStatusById(serveMenu);
                        log.info("执行的URL：" + serveMenu.getAdress() + "   执行返回结果：" + response.getStatusLine().getStatusCode());
                    }
                } catch (Exception e) {
                    serveMenu.setAlive(2);
                    serveService.updateStatusById(serveMenu);
                    log.error("执行的URL：" + serveMenu.getAdress() + "  执行返回结果连接失败,失败原因"+e.getMessage());
                }finally {
                    try {
                        if(response!=null){
                            response.close();
                        }
                        httpclient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
