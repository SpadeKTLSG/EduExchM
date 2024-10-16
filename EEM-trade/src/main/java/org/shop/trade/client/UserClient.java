package org.shop.trade.client;

import org.shop.trade.entity.remote.User;
import org.shop.trade.entity.remote.UserFunc;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "guest", url = "http://localhost:10080")
public interface UserClient {

    //User
    @GetMapping("/guest/user/remote/User/getById/{id}")
    User getById(@PathVariable Long id);


    @PostMapping("/guest/user/remote/User/updateById")
    void updateById(@RequestBody User user);

    //UserFunc
    @GetMapping("/guest/user/remote/UserFunc/getById/{id}")
    UserFunc getById_UserFunc(@PathVariable Long id);


    @PostMapping("/guest/user/remote/UserFunc/updateById")
    void updateById_UserFunc(@RequestBody UserFunc userFunc);


}
