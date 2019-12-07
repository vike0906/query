$(function(){
    //解决表单控件不能回弹 只有微信ios有这个问题
    $("input,select,textarea").blur(function(){
        setTimeout(function () {
            var scrollHeight = document.documentElement.scrollTop || document.body.scrollTop || 0;
            window.scrollTo(0, Math.max(scrollHeight - 1, 0));
        }, 100);
    })

});

function ajaxPost(url, params, success) {
    $.ajax({
        url: url,
        type: 'POST',
        data: params,
        dataType: 'JSON',
        timeout: 10000,
        success: success,
        complete: function (xhr, status) {
            if(status=='timeout'){
                $.hideLoading();
                $.toast('请求超时', "cancel");
            }
        },
        error: function (xhr, status) {
            $.hideLoading();
            $.toast('服务器出错', "cancel");
        }


    });
}
function baseQuery() {
    var userName = $("#userName").val();
    var idNo = $("#idNo").val();
    var creditCardNo = $("#creditCardNo").val();
    var phone = $("#phone").val();

    if(userName.length==0){
        $.toast("用户名不能为空","cancel");
        return 1;
    }
    if(idNo.length==0){
        $.toast("身份证号不能为空","cancel");
        return 1;
    }
    if(creditCardNo.length==0){
        $.toast("信用卡号不能为空","cancel");
        return 1;
    }
    if(phone.length==0){
        $.toast("手机号不能为空","cancel");
        return 1;
    }


    var reg =/^[\u4e00-\u9fa5]{2,4}$/;
    if(reg.test(userName)==false){
        $.toast("请输入真实姓名","cancel");
        return 1;
    }

    var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
    if(reg.test(idNo)==false){
        $.toast("身份证号输入不合法","cancel");
        return 1;
    }

    if(creditCardNo.length<12||creditCardNo.length>19){
        $.toast("信用卡号输入错误","cancel");
        return 1;
    }

    var reg = /^[1][3,4,5,7,8,9][0-9]{9}$/;
    if(reg.test(phone)==false){
        $.toast("请输入正确的手机号","cancel");
        return 1;
    }
    return 0;

}

/**
 window.onload=function(){
    var text = "自愿查询，查询成功不予退款，姓名、身份证号、信用卡号、手机号仅用于查询报告，均经过加密处理，" +
        "平台不会泄露您的信息，平台不提供放款业务。";
    $.alert(text, "郑重声明",function () {
        return false;
    });
}*/

/**
window.onload=function(){
    var text = "本平台不提供央行征信和个人爬虫等隐私数据查询。" +
        "切勿相信他人以优化征信，保证下款，提高信用额度等为手段骗取钱财";
    $.alert(text, "友情提示",function () {
        return false;
    });
}*/
