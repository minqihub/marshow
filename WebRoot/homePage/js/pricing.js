debugger


var parames={"type1":"paramer1","type2":"paramer2"};
$.ajax({
	url:'footer.html',
	type:'post',
	dataType:'html',
	data:parames,
	error: function(){
		alert('error');
	},
	success:function(data){
		$("#footer").html(data);
	}
});

$.ajax({
	url:'header.html',
	type:'post',
	dataType:'html',
	data:parames,
	error: function(){
		alert('error');
	},
	success:function(data){
		$("#header").html(data);
	}
});