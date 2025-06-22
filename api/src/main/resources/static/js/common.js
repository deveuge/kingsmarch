const game = new Chess();
var moveSound = new Audio('../sound/move.mp3');
var captureSound = new Audio('../sound/capture.mp3');

const endings = {
    BLACK_WIN: 'Checkmate: Black wins',
    WHITE_WIN: 'Checkmate: White wins',
    STALEMATE: 'Stalemate'
};

const END_GAME = new Map(Object.entries(endings));


function splitMove(moveStr) {
	return { from: moveStr.split('-')[0], to: moveStr.split('-')[1] };
}

function markLastMove(move) {
	removeHighlight();
	$('#board .square-' + move.from).addClass('highlight');
	$('#board .square-' + move.to).addClass('highlight');
}

function removeHighlight() {
	$('div[data-square]').removeClass('highlight');
}

function onDragStart (source, piece, position, orientation) {
  if ((orientation === 'white' && piece.search(/^w/) === -1) ||
      (orientation === 'black' && piece.search(/^b/) === -1)) {
    return false
  }
}

function onMouseoverSquare(square) {
	const moves = game.moves({ square: square, verbose: true });
	if (moves.length === 0) return;
	highlightSquare(square);

	for (let i = 0; i < moves.length; i++) {
		highlightSquare(moves[i].to);
	}
}

function onMouseoutSquare(square) {
	removeHighlights();
}

function highlightSquare(square) {
	$('#board .square-' + square).addClass("highlight-alt");
}

function removeHighlights() {
	$('div[data-square]').removeClass("highlight-alt");
}