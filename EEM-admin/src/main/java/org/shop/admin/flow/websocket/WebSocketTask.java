package org.shop.admin.flow.websocket;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * WebSocket定时任务
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketTask {


    private final WebSocketServer webSocketServer;

    /**
     * 通过WebSocket每隔30分钟向所有端发送提醒
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void sendMessageToClient() {
        log.debug("定时任务：向客户端发送消息");
        webSocketServer.sendToAllClient("老狼老狼几点了" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
    }

}
