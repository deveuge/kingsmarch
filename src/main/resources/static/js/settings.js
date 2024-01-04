const COOKIE_PREFIX = 'kingsmarch.config.';

let configuration = {
	pieces: localStorage.getItem(COOKIE_PREFIX + 'pieces') || 'staunty',
	board: localStorage.getItem(COOKIE_PREFIX + 'board') || '0',
	notation: localStorage.getItem(COOKIE_PREFIX + 'notation') || '0'
}

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

(function () {
	$('#pieces-select').on('change', function() {
		configuration.pieces =  $(this).val();
		localStorage.setItem(COOKIE_PREFIX + 'pieces', $(this).val());
		kingsmarch.config.pieceTheme = 'img/pieces/' + $(this).val() + '/{piece}.svg';
		kingsmarch.board.resize();
	});
	
	$('#board-select').on('change', function() {
		configuration.board =  $(this).val();
		localStorage.setItem(COOKIE_PREFIX + 'board', $(this).val());
		let theme = boardThemes[$(this).val()];
		$(':root').css('--background-color', theme[0]);
		$(':root').css('--background-color-alt', theme[1]);
	});
	
	$('#notation-select').on('change', function() {
		configuration.notation =  $(this).val();
		localStorage.setItem(COOKIE_PREFIX + 'notation', $(this).val());
		kingsmarch.config.showNotation = $(this).val() != 2;
		kingsmarch.board.resize();
		if(kingsmarch.config.showNotation) {
			let positions = notationPosition[$(this).val()];
			$(':root').css('--bottom-notation', positions[0]);
			$(':root').css('--left-notation', positions[1]);
		}
	});

    $('#pieces-select').val(configuration.pieces);
    $('#board-select').val(configuration.board);
    $('#notation-select').val(configuration.notation);
    
    $('#pieces-select, #board-select, #notation-select').trigger('change');
})();

