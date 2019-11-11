package com.vike.query.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vike.query.common.GlobalConstant;
import com.vike.query.component.AccessTokenSync;
import com.vike.query.component.WXApiInfoComment;
import com.vike.query.pojo.Button;
import com.vike.query.pojo.Menu;
import com.vike.query.util.HttpsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author: lsl
 * @createDate: 2019/10/22
 */
@RestController
@Slf4j
@RequestMapping("manager")
public class ManagerController {

    @Autowired
    WXApiInfoComment wxApiInfoComment;

    @GetMapping("create-menu")
    public String createMenu(@RequestParam(required = false) String code){

        if(!"vike0906".equals(code)){
            return "Hello World";
        }

        Menu menu1 = new Menu("view","征信查询",wxApiInfoComment.getUrlWithAuthorize("http://106.13.222.152/view/index","1"));
        Menu menu2 = new Menu("view","查询记录",wxApiInfoComment.getUrlWithAuthorize("http://106.13.222.152/view/history","2"));
        Menu menu3 = new Menu("view","我的邀请",wxApiInfoComment.getUrlWithAuthorize("http://106.13.222.152/view/invite","3"));

        Menu [] menus = {menu1,menu2,menu3};
        Button button = new Button(menus);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String s = gson.toJson(button);
        log.info(s);

        String url = String.format(GlobalConstant.CREATE_MENU_URL, AccessTokenSync.getAccessToken());
        String result = HttpsUtil.httpsRequest(url, HttpsUtil.METHOD_POST, s);
        return result;
    }

    @GetMapping("get-menu")
    public String getMenu(@RequestParam(required = false) String code){

        if(!"vike0906".equals(code)){
            return "Hello World";
        }

        String url = String.format(GlobalConstant.GET_MENU_URL, AccessTokenSync.getAccessToken());

        String result = HttpsUtil.httpsRequest(url, HttpsUtil.METHOD_GET);

        return result;

    }

    @GetMapping("delete-menu")
    public String deleteMenu(@RequestParam(required = false) String code){

        if(!"vike0906".equals(code)){
            return "Hello World";
        }

        String url = String.format(GlobalConstant.DELETE_MENU_URL, AccessTokenSync.getAccessToken());

        String result = HttpsUtil.httpsRequest(url, HttpsUtil.METHOD_GET);

        return result;

    }


}
