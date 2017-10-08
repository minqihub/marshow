var current_fs, next_fs, previous_fs;
var left, opacity, scale;
var animating;
var smsCode;

//第一步
$('.btnNext1').on('click', function() {debugger
	var mobile = $("input[name='mobile']").val();
	var password = $("input[name='password']").val();
	var repassword = $("input[name='repassword']").val();

	if(form.isNull(password) || form.isNull(repassword)){
		$("#tips1")[0].innerText = '"密码都不填完，是要上天啊"';
		$("#tips1").shake(3, 10, 400);
		return;
	}else if(password != repassword){
		$("#tips1")[0].innerText = '"给我俩扯犊子呢，两次密码都不一样"';
		$("#tips1").shake(3, 10, 400);
		return;
	}
	
	
	if(form.isPhone(mobile)){
		var json = {"json": JSON.stringify({"mobile" : mobile})};
		var url = form.getprojectUrl + "/trust/regist/checkMobileExist.do";
		var resultData = form.ajax(json, url).data;
		debugger
		if(resultData.MSGID == "S"){		//手机号正确，且未被使用
			if(animating) return false;
			animating = true;
			
			current_fs = $(this).parent();
			next_fs = $(this).parent().next();
			
			$("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");
			
			next_fs.show(); 
			current_fs.animate({opacity: 0}, {
				step: function(now, mx) {
					scale = 1 - (1 - now) * 0.2;
					left = (now * 50)+"%";
					opacity = 1 - now;
					current_fs.css({'transform': 'scale('+scale+')'});
					next_fs.css({'left': left, 'opacity': opacity});
				}, 
				duration: 800, 
				complete: function(){
					current_fs.hide();
					animating = false;
				}, 
				easing: 'easeInOutBack'
			});
		}else{								//手机号已存在
			$("#tips1")[0].innerText = '"哎呀，手机号和别人重复了，换个呗"';
			$("#tips1").shake(3, 10, 400);
		}
	}else{									//手机号不正确
		$("#tips1")[0].innerText = '"你是不是瞎填手机号，打你哦"';
		$("#tips1").shake(3, 10, 400);
	}
	
	
});


//第二步
$('.btnNext2').on('click', function() {debugger
	
});


/*
$(".next").click(function(){
	if(animating) return false;
	animating = true;
	
	current_fs = $(this).parent();
	next_fs = $(this).parent().next();
	
	//activate next step on progressbar using the index of next_fs
	$("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");
	
	//show the next fieldset
	next_fs.show(); 
	//hide the current fieldset with style
	current_fs.animate({opacity: 0}, {
		step: function(now, mx) {
			//as the opacity of current_fs reduces to 0 - stored in "now"
			//1. scale current_fs down to 80%
			scale = 1 - (1 - now) * 0.2;
			//2. bring next_fs from the right(50%)
			left = (now * 50)+"%";
			//3. increase opacity of next_fs to 1 as it moves in
			opacity = 1 - now;
			current_fs.css({'transform': 'scale('+scale+')'});
			next_fs.css({'left': left, 'opacity': opacity});
		}, 
		duration: 800, 
		complete: function(){
			current_fs.hide();
			animating = false;
		}, 
		//this comes from the custom easing plugin
		easing: 'easeInOutBack'
	});
});
*/



$(".previous").click(function(){
	if(animating) return false;
	animating = true;
	
	current_fs = $(this).parent();
	previous_fs = $(this).parent().prev();
	
	//de-activate current step on progressbar
	$("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active");
	
	//show the previous fieldset
	previous_fs.show(); 
	//hide the current fieldset with style
	current_fs.animate({opacity: 0}, {
		step: function(now, mx) {
			//as the opacity of current_fs reduces to 0 - stored in "now"
			//1. scale previous_fs from 80% to 100%
			scale = 0.8 + (1 - now) * 0.2;
			//2. take current_fs to the right(50%) - from 0%
			left = ((1-now) * 50)+"%";
			//3. increase opacity of previous_fs to 1 as it moves in
			opacity = 1 - now;
			current_fs.css({'left': left});
			previous_fs.css({'transform': 'scale('+scale+')', 'opacity': opacity});
		}, 
		duration: 800, 
		complete: function(){
			current_fs.hide();
			animating = false;
		}, 
		//this comes from the custom easing plugin
		easing: 'easeInOutBack'
	});
});

$(".submit").click(function(){
	return false;
})

//获取短信验证码
var smsCode = function(){
	var rand = (Math.random()+"").substring(2, 6);
	console.log(rand);
}

/**
 * 元素抖动效果
 * $("#tips1").shake(2, 10, 400);
 * $("抖动元素").shake(次数, 距离, 持续时间);
 */
jQuery.fn.shake = function (intShakes /*Amount of shakes*/, intDistance /*Shake distance*/, intDuration /*Time duration*/) {
	this.each(function () {
		var jqNode = $(this);
		jqNode.css({ position: 'relative' });
		for (var x = 1; x <= intShakes; x++) {
			jqNode.animate({ left: (intDistance * -1) }, (((intDuration / intShakes) / 4)))
			.animate({ left: intDistance }, ((intDuration / intShakes) / 2))
			.animate({ left: 0 }, (((intDuration / intShakes) / 4)));
		}
	});
	return this;
}
