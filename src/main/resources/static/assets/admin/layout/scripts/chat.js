var Chat = function() {

	return {
		init : function() {
			cont = $('#chats');
			list = $('.chats', cont);
			form = $('.chat-form', cont);
			input = $('input', form);
			btn = $('.btn', form);
			var handleClick = function(e) {
				e.preventDefault();
				
				var text = input.val();
				if (text.length == 0) {
					return;
				}
				//如果没连接
				if(ws == null){
					$("#joinModal").modal();
					return;
				}
				var time_str = new Date().format("hh:mm:ss");

				var tpl = '';
				tpl += '<li class="out">';
				tpl += '<img class="avatar" alt="" src="/assets/admin/layout/img/avatar3.jpg"/>';
				tpl += '<div class="message">';
				tpl += '<span class="arrow"></span>';
				tpl += '<a class="name">' + $("#username").val() + '</a>&nbsp;';
				tpl += '<span class="datetime">at ' + time_str + '</span>';
				tpl += '<span class="body">';
				tpl += text;
				tpl += '</span>';
				tpl += '</div>';
				tpl += '</li>';
				list.append(tpl);
				userSend();
				input.val("");
				
			cont.find('.scroller').slimScroll({
				scrollTo : getLastPostPos()
			});
		};
			$('body').on('click', '.message .name', function(e) {
				e.preventDefault(); // prevent click event

				var name = $(this).text(); // get clicked user's full name
				input.val('@' + name + ':'); // set it into the input field
				input.focus();
				Metronic.scrollTo(input); // scroll to input if needed
			});

			btn.click(handleClick);

			input.keypress(function(e) {
				if (e.which == 13) {
					handleClick(e);
					return false;
				}
			});
		}
	}
}();
var getLastPostPos = function() {
	var height = 0;
	cont.find("li.out, li.in").each(function() {
		height = height + $(this).outerHeight();
	});

	return height;
};
Date.prototype.format = function(fmt) {
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"h+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
					: (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}