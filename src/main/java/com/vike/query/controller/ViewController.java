package com.vike.query.controller;

import com.vike.query.component.WXApiInfoComment;
import com.vike.query.dao.FansRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: lsl
 * @createDate: 2019/10/22
 */
@Slf4j
@Controller
public class ViewController {

    @Autowired
    WXApiInfoComment wxApiInfoComment;
    @Autowired
    FansRepository fansRepository;

    @GetMapping("/")
    public String root(ModelMap modelMap, @RequestParam(required = false) Long fansId, @RequestParam(required = false) String code, @RequestParam(required = false) String state){
        return index(modelMap, fansId, code,state);
    }

    @GetMapping("view/index")
    public String index(ModelMap modelMap, @RequestParam(required = false) Long fansId,@RequestParam(required = false) String code, @RequestParam(required = false) String state){
        if(fansId==null||fansId==0L){
            fansId = code2FansId(code,state);
        }
        modelMap.addAttribute("fansId",fansId);
        return "index";
    }

    @GetMapping("tag/{agentTag}")
    public String indexWithTag(ModelMap modelMap, @PathVariable String agentTag){
        modelMap.addAttribute("fansId",-1L);
        modelMap.addAttribute("agentTag",agentTag);
        return "index";
    }

    @GetMapping("view/history")
    public String history(ModelMap modelMap,@RequestParam(required = false) Long fansId, @RequestParam(required = false) String code, @RequestParam(required = false) String state){
        if(fansId==null||fansId==0L){
            fansId = code2FansId(code,state);
        }
        //TODO fansID存在的话去查询历史记录
        modelMap.addAttribute("fansId",fansId);
        return "history";
    }

    @GetMapping("view/invite")
    public String invite(ModelMap modelMap, @RequestParam(required = false) String code, @RequestParam(required = false) String state){
        long fansId = code2FansId(code,state);
        modelMap.addAttribute("fansId",fansId);
        return "invite";
    }

    @GetMapping("view/query")
    public String query(ModelMap modelMap, @RequestParam(required = false) Long fansId){
        if(fansId==null){
            fansId = -1L;
        }
        modelMap.addAttribute("fansId",fansId);
        return "query";
    }

    @GetMapping("view/agreement")
    public String agreement(){
        return "agreement";
    }

    @PostMapping("view/query-summit")
    public String query(ModelMap modelMap,
                        @RequestParam(required = false) Long fansId,
                        @RequestParam String userName,
                        @RequestParam String idNo,
                        @RequestParam String creditCardNo,
                        @RequestParam String phone,
                        @RequestParam String authCode){
        log.info("传递值：{} {} {} {} {}", userName, idNo, creditCardNo, phone, authCode);
        if(fansId==null){
            fansId = -1L;
        }
        modelMap.addAttribute("fansId",fansId);
        return "result";
    }

    private long code2FansId(String code,String state){
        long fansId = -1L;
        if(code!=null&&!"".equals(code)){
            fansId = wxApiInfoComment.getFansIdByCode(code,state);
        }
        return fansId;
    }
}
