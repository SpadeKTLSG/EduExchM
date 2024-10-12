package org.shop.flow.websocket;

import com.alibaba.fastjson.JSON;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket服务
 */
@Slf4j
@Component
@ServerEndpoint("/customer/{sid}")
public class WebSocketServer {


    /**
     * 存放会话对象
     */
    private static final Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("客户端：" + sid + "建立连接");
        sessionMap.put(sid, session);
    }


    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        //判定消息类型 -> 是否是发给自己的 ...

        log.info("收到来自客户端：" + sid + "的信息:" + message);
        //取出JSON消息, 找到对应的用户, 发送消息给对应两个用户
        //JSON.toJSONString(orderMap) 拆开
        Map<String, Object> orderMap = JSON.parseObject(message, Map.class);
        Long sellerId = (Long) orderMap.get("sellerId");
        Long prodId = (Long) orderMap.get("prodId");
        Long buyerId = (Long) orderMap.get("buyerId");
        log.info("需要处理的客服接入请求: 售出者" + sellerId + " 购入者" + buyerId + " 物品" + prodId);
    }

    /**
     * 连接关闭调用的方法
     *
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("连接断开:" + sid);
        sessionMap.remove(sid);
    }


    /**
     * 群发
     */
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();

        for (Session session : sessions) { //服务器向客户端发送消息
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("发送消息失败: " + e.getMessage());
            }
        }
    }

}
