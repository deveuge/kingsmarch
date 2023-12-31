var moveSound = new Audio('../sound/move.mp3');
var captureSound = new Audio('../sound/capture.mp3');

const kingsmarch = {
	board: undefined,
	config: {
		pieceTheme: 'img/pieces/staunty/{piece}.svg',
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
		url: 'move',
		data: { source, target, colour: orientation.toUpperCase() },
		async: false,
		success: function(data) {
			if(data == 'ok') {
				moveSound.play();
			}
			result = data;
		}
	});
	return result;
}