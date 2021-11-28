;(function ($, win) {
// 整个页面的操作
var handler = {
    init: function (el) {
        this.$el = $(el);

        // 执行页面方法
	this.handleFunction();
	$('#getValidateCode').click(function(e){
		var $phoneNumber =  $('#phoneNumber')
		if($phoneNumber.val() === ''){
			$('.login-tip').show();
			$('.tip').text("号码不能为空");
			$phoneNumber.focus();
		}
		$.get("/cas/validateCode/"+$phoneNumber.val(), function(res){
			if(res.success){

			}else{
				$('.login-tip').show();
				$('.tip').text(res.message);
			}
		});
	})
    },
    // 页面操作
    handleFunction: function () {
        var $hide_pass=$(".hide-pass"),type="1",
        $icon_forget=$(".icon-forget"),forget="1",
        $login=$(".login-btn button"),$tip=$(".login-tip"),
        $pass_wrap=$(".login-password"),
        $password=$(".login-password input");
        $hide_pass.on("click",function(){
        	if(type=="1"){
        		$hide_pass.addClass("show-pass");
        		$hide_pass.parent(".login-password").find("input").attr("type","text");
        		type="2";
        	}else{
        		$hide_pass.removeClass("show-pass");
        		$hide_pass.parent(".login-password").find("input").attr("type","password");
        		type="1";
        	}
        });
        
        $icon_forget.on("click",function(){
        	if(forget=="1"){
        		$icon_forget.addClass("show-forget");
        		forget="2";
        		//TODO 执行记住密码
        	}else{
        		$icon_forget.removeClass("show-forget");
        		forget="1";
        		//TODO 执行取消记住密码
        	}
        });
        
        //点击登录
   /*     $login.on("click",function(){
        	var pass=$password.val();
        	//TODO 验证密码正确性,123是测试效果。
        	if(pass=="123"){
        		$tip.hide();
        		$pass_wrap.removeClass("border-red");
        	}else{
        		$tip.show();
        		$pass_wrap.addClass("border-red");
        	}
        });*/
    }
};

$(function () {
    handler.init('#container');
});
}(jQuery, window));