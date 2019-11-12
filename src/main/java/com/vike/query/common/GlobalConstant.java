package com.vike.query.common;

/**
 * @author: lsl
 * @createDate: 2019/10/22
 */
public class GlobalConstant {

    /**微信APi*/
    public static final String WEIXIN_API = "https://open.weixin.qq.com";

    /**获取AccessToken链接*/
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /**创建菜单链接*/
    public static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";

    /**查看菜单链接*/
    public static final String GET_MENU_URL = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=%s";

    /**删除菜单链接*/
    public static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=%s";

    /**改造菜单，获取code*/
    public static final String CODE_MENU_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=%s#wechat_redirect";

    /**获取网页AccessToken链接*/
    public static final String WEB_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**通过AccessToken拉取用户信息链接*/
    public static final String COLLECT_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";


    /**黑核科技短信验证码获取*/
    public static final String HP_AUTH_SMS = "http://api.hihippo.cn/api/authorizedSms";

    /**黑核科技短信验证码校验*/
    public static final String HP_AUTH_SMS_CHECK = "http://api.hihippo.cn/api/authorizedSmsCheck";

    /**黑核科技查询征信结果*/
    public static final String HP_REPORT_CARD_NO_DATA = "http://api.hihippo.cn/api/reportCardNoData";

    /**代理标识长度*/
    public static final int AGENT_TAG_LENGTH = 32;
}
