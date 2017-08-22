layui.use('element', function(){
	var element = layui.element;
//	var $ = layui.jquery;
	element.init(); //这样element对动态生成的元素才会重新有效
});


layui.use('carousel', function(){
	var carousel = layui.carousel;
  	//建造实例
  	carousel.render({
    	elem: '#test1',
    	height: '750px',
    	width: '100%',
    	arrow: 'hover', 	//显示箭头hover,always
    	anim: 'fade' 		//切换动画方式
  	});
});