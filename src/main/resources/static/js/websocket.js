'use strict';

var stompClient = null;

const websocket = {
	connect() {
		kingsmarch.freeze('Waiting for your opponent...');
		var socket = new SockJS('/ws');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, onConnect, onError);
	},
	makeMove(source, target) {
		stompClient.send('/app/chat.private.' + $("#uuid").val(), {}, JSON.stringify({type: 'MOVE', content: source + "-" + target}));
	}
}

websocket.connect();

function onConnect() {
	stompClient.subscribe('/topic/' + $("#uuid").val(), onMessageReceived);
	stompClient.send('/app/chat.private.' + $("#uuid").val(), {}, JSON.stringify({type: 'JOIN'}));
}

function onError() {
	window.location.href = $("#nav-logo").attr("href") + "?error=Couldn't connect to the game"; 
}

function onMessageReceived(payload) {
	var message = JSON.parse(payload.body);
	console.log("RECEIVED MESSAGE: ");
	console.log(message);

	if (message.type === 'JOIN') {
		if(message.content === '1') {
			kingsmarch.config.orientation = 'white';
			kingsmarch.init();
		}
		if(message.content === '2') {
			kingsmarch.unfreeze();
			showAlert("Your opponent has entered the game");
			$("#share").hide();
		}
	} else if (message.type === 'LEAVE') {
		kingsmarch.freeze("Game over");
		showAlert("Your opponent has disconnected");
	} else {
		// Perform move
		kingsmarch.board.move(message.content);
		kingsmarch.playMoveSound();
	}

}