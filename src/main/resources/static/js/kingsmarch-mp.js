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
			if(data == 'ok') {
				websocket.makeMove(source, target);
			}
			result = data;
		},
		error: function () {
			result = 'snapback';
		}
	});
	return result;
}

function copyText(input) {
	console.log($(input).prev().val());
	var text = $(input).prev().val();
	navigator.clipboard.writeText(text);

	showAlert("Link copied to clipboard");
}