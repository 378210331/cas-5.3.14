$(function () {
	$('.login-btn').click(function(){//登录按钮
		var tab = $('.tab-cur').attr("id");
		if(tab === 'password'){
			if($('#username').val() === ''){
				layer.msg("用户名不能为空", {icon: 5});
				console.error("用户名不能为空")
				$('#username').focus();
				return ;
			}
			if($('#passwd').val() === ''){
				layer.msg("密码不能为空", {icon: 5});
				console.error("密码不能为空")
				$('#passwd').focus();
				return ;
			}
		}else if (tab === "message"){
			if($('#phoneNumber').val() === ''){
				layer.msg("号码不能为空", {icon: 5});
				console.error("号码不能为空")
				$('#phoneNumber').focus();
				return ;
			}
			if($('#validateCode').val() === ''){
				layer.msg("验证码不能为空", {icon: 5});
				console.error("验证码不能为空")
				$('#validateCode').focus();
				return ;
			}
		}
		var form = $("#fm-"+tab);
		form.submit();
	})

	$('.sendcode').click(function (){
		var $phoneNumber =  $('#phoneNumber')
		if($phoneNumber.val() === ''){
			console.error("号码不能为空")
			layer.msg('号码不能为空', {icon: 5});
			$phoneNumber.focus();
			return;
		}else{
			$.get("/cas/validateCode/"+$phoneNumber.val(), function(res){
				if(res.success){
					layer.msg(res.message, {icon: 1});
					$(this).addClass('btn-ban')
					$('.sendcode').html('59秒后重新发送')
					$(this).attr({ "disabled": "disabled" })
					var i = 59
					var interval = setInterval(() => {
						i--;
						var tips = i + '秒后重新发送'
						$('.sendcode').html(tips)
						if (i === 0) {
							clearInterval(interval)
							$('.sendcode').html('发送验证码')
							$(this).removeClass('btn-ban')
							$(this).removeAttr("disabled")
						}
					}, 1000);
				}else{
					layer.msg(res.message, {icon: 5});
					console.error(res.message);
					return ;
				}
			});
		}
	})

	$('.tab>.item').click(function () {
		$(this).addClass('tab-cur').siblings().removeClass('tab-cur')
		if ($(this).index() == 1) {
			$('#routine').hide()
			$('.msg-wrap').show()
		} else {
			$('.msg-wrap').hide()
			$('#routine').show()

		}
	})
	$('.tab>.item')[0].click()//初始化
	//切换扫码登录方式
	$('#qrcode').click(function () {
		$('.type1').hide()
		$('.qrcode-wrap').show()
	})
	//切换正常登录模式
	$('#othertype').click(function () {
		$('.qrcode-wrap').hide()
		$('.type1').show()
	})


	/*$('.sendcode').click(function () {
		$(this).addClass('btn-ban')
		$('.sendcode').html('59秒后重新发送')
		$(this).attr({ "disabled": "disabled" })
		var i = 59
		var interval = setInterval(() => {
			i--;
			var tips = i + '秒后重新发送'
			$('.sendcode').html(tips)
			if (i === 0) {
				clearInterval(interval)
				$('.sendcode').html('发送验证码')
				$(this).removeClass('btn-ban')
				$(this).removeAttr("disabled")
			}
		}, 1000);

	})*/
})