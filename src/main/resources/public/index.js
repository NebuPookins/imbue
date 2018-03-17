(() => {
	'use strict';
	console.log("JavaScript running.");
	$(() => {
		console.log("JQuery Initialized");
		const $yourName = $('#your-name');
		const $chatContents = $('#chat-contents');
		const $messageInput = $('#message-input');
		const systemLineTemplate = Handlebars.compile($('#system-line').text());
		const userLineTemplate = Handlebars.compile($('#user-line').text());
		const ws = new WebSocket(`ws://${location.host}/ws`);
		ws.onopen = (event) => {
			console.log("Websocket open.");
			$messageInput.on('keypress', (event) => {
				const keycode = event.keyCode ? event.keyCode : event.which;
				if (keycode !== 13) {
					return;
				}
				const message = $messageInput.val();
				ws.send(JSON.stringify({
					type: 'Message',
					payload: message
				}));
				$messageInput.val('');
			});
		};
		ws.onmessage = (event) => {
			console.log("Received event", event);
			var data = JSON.parse(event.data);
			switch (data.type) {
				case 'SetName':
					$yourName.text(data.name);
					break;
				case 'AddSystemLine':
					$chatContents.append(systemLineTemplate(data));
					$chatContents.scrollTop(99999999999999);
					break;
				case 'AddUserLine':
					$chatContents.append(userLineTemplate(data));
					$chatContents.scrollTop(99999999999999);
					break;
				case 'KeepAlive':
					ws.send(JSON.stringify({
					type: 'KeepAlive',
					payload: ''
				}));
				default:
					console.log("Unknown event type", data);
			}
		};
	});
})();