var moveSound = new Audio('../sound/move.mp3');
var captureSound = new Audio('../sound/capture.mp3');

const kingsmarch = {
	board: undefined,
	config: {
		pieceTheme: 'img/pieces/staunty/{piece}.svg',
		orientation: 'black',
		position: 'start',
		draggable: true,
		moveSpeed: 'slow',
		snapbackSpeed: 500,
		snapSpeed: 100,
		showNotation: true,
		onDragStart: onDragStart,
		onDrop: onDrop
	},
	init() {
		this.board = Chessboard('board', this.config);
	},
	freeze(message) {
		$("#board").attr('data-content', message);
		$("#board").addClass("freeze");
	},
	unfreeze() {
		$("#board").removeClass("freeze");
	},
	playMoveSound() {
		moveSound.play();
	},
	setPosition(fen) {
		this.board.position(fen);
	}
};

function onDragStart (source, piece, position, orientation) {
  if ((orientation === 'white' && piece.search(/^w/) === -1) ||
      (orientation === 'black' && piece.search(/^b/) === -1)) {
    return false
  }
}

function onDrop(source, target, piece, newPos, oldPos, orientation) {
	let result = null;
	$.ajax({
		type: 'POST',
		url: 'mp/move',
		data: {id: $("#uuid").val(), source, target, colour: orientation.toUpperCase() },
		async: false,
		success: function(data) {
			console.log(data);
			if(data.responseType == 'OK') {
				websocket.makeMove(source, target, data);
			}
			result = data.responseType.toLowerCase();
		},
		error: function () {
			result = 'snapback';
		}
	});
	return result;
}

function copyText(input) {
	var text = $(input).prev().val();
	navigator.clipboard.writeText(text);
	showAlert("Link copied to clipboard");
}