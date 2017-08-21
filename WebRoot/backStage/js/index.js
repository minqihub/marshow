//JavaScript代码区域
layui.use('element', function(){
	var element = layui.element;
	var $ = layui.jquery;
	element.init(); //这样element对动态生成的元素才会重新有效
});

var $ = layui.jquery;

$('#lock').on('click', function() {
	alert("lock点击了");
});
