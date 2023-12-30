const boardThemes = [
	['#b58863', '#f0d9b5'],
	['#769656', '#eeeed2'],
	['#afafaf', '#ededed'],
	['#D18B47', '#FFCE9E'],
	['#FFE5B6', '#B16228'],
	['#c1ddef', '#5d98bb'],
	['#f3f3f3', '#58AC8A']
];

const notationPosition = [
	['1px', '2px'],
	['-30px', '-25px']
];

$('#pieces-select').on('change', function() {
	kingsmarch.config.pieceTheme = 'img/pieces/' + $(this).val() + '/{piece}.svg';
	kingsmarch.board.resize();
});

$('#board-select').on('change', function() {
	let theme = boardThemes[$(this).val()];
	$(':root').css('--background-color', theme[0]);
	$(':root').css('--background-color-alt', theme[1]);
});

$('#notation-select').on('change', function() {
	kingsmarch.config.showNotation = $(this).val() != 2;
	kingsmarch.board.resize();
	if(kingsmarch.config.showNotation) {
		let positions = notationPosition[$(this).val()];
		$(':root').css('--bottom-notation', positions[0]);
		$(':root').css('--left-notation', positions[1]);
	}
});
