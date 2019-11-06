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
    $.showLoading();
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
