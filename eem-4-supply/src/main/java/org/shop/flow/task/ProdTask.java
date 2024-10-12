package org.shop.flow.task;


import lombok.extern.slf4j.Slf4j;
import org.shop.entity.ProdFunc;
import org.shop.service.HotsearchService;
import org.shop.service.ProdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类 商品
 */
@Component
@Slf4j
public class ProdTask {


    private ProdService prodService;

    private HotsearchService hotsearchService;


    /**
     * 处理到达失效展示时间的商品对象, 每天1点触发一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processShowingProd() {

        log.debug("定时判断所有当前展示中的商品对象是否到达展示失效时间：{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-60); //错开高峰期

        List<ProdFunc> prodFuncList = prodService.getOutdateProdA(time);

        if (prodFuncList != null && !prodFuncList.isEmpty()) {
            for (ProdFunc prodFunc : prodFuncList) {
                prodService.cooldownUpshowProdA(prodFunc); //停止提升展示对应券对象
                prodService.cooldownRotationProdA(prodFunc); //停止轮播对应券对象
            }
        }
    }

    /**
     * 提取热搜表, 每天1点触发一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processHotSearch() {

        hotsearchService.clearAllHotsearch(); //清空热搜表

        log.debug("定时提取热搜表：{}", LocalDateTime.now());

        List<ProdFunc> prodFuncList = prodService.getHotProdA();

        if (prodFuncList != null && !prodFuncList.isEmpty()) {
            for (ProdFunc prodFunc : prodFuncList) {
                prodService.add2HotSearchA(prodFunc); //添加到热搜表
            }
        }
    }
}
