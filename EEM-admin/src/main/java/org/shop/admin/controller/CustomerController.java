package org.shop.admin.controller;


import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.admin.entity.res.Result;
import org.shop.admin.flow.websocket.WebSocketServer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务端客服
 */
@Slf4j
@Tag(name = "Customer", description = "客服")
@RequestMapping("/customer/admin")  //customer为客户端和服务端的统一前缀
@RestController
@RequiredArgsConstructor
public class CustomerController {


    private final WebSocketServer webSocketServer;

    /**
     * 向客户端推送消息
     */
    @PostMapping("/service")
    @Operation(summary = "服务端向客户端推送消息")
    public Result serviceSendMes(@RequestBody String mes, Session session) {

        webSocketServer.sendToAllClient(JSON.toJSONString(mes));        //使用简单推送模式
        return Result.success();
    }
    //http://localhost:8085/customer/admin/service


}
