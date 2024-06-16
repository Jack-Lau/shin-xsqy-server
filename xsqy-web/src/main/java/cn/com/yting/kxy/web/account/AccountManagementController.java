/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Darkholme
 */
@RestController
@RequestMapping("/management/account")
public class AccountManagementController {

    @Autowired
    AccountService accountService;

    @PostMapping("/create")
    public Object create(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        return accountService.registerForTest("YT" + username, password).toAccountInfo();
    }

}
