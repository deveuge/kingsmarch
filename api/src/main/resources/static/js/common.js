function markLastMove(moveStr) {
	removeHighlight();
	if (moveStr == null) return;

	const parts = moveStr.split('-');
	if (parts.length !== 2) return;

	const from = parts[0];
	const to = parts[1];

	$('#board .square-' + from).addClass('highlight');
	$('#board .square-' + to).addClass('highlight');
}

function removeHighlight() {
	$('div[data-square]').removeClass('highlight');
}