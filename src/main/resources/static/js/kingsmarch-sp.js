var moveSound = new Audio('../sound/move.mp3');
var captureSound = new Audio('../sound/capture.mp3');

const endings = {
    BLACK_WIN: 'Checkmate: Black wins',
    WHITE_WIN: 'Checkmate: White wins',
    STALEMATE: 'Stalemate'
};
const END_GAME = new Map(Object.entries(endings));

const delay = ms => new Promise(res => setTimeout(res, ms));

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
		kingsmarch.setPosition($("#singleplayerFEN").val());
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
	playCaptureSound() {
		captureSound.play();
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
	let result = 'snapback';
	$.ajax({
		type: 'POST',
		url: 'sp/move',
		data: { source, target },
		async: false,
		success: function(data) {
			if(data.responseType == 'OK') {
				if(!data.promotion) {
					makeMove(data);
					if(data.responseType == 'OK' && !data.endOfGame) {
						getOpponentMove();
					}
				} else {
					let promotionValue = 'q';
					let promise = new Promise(function(resolve, reject) {
						showPromotionModal();
						$("#promotion-list > button").each(function() {
							$(this).on("click", function() {
								promotionValue = $(this).attr('data-value');
								resolve();
							});
						});
					});
					promise.then(function() {
						$.ajax({
							type: 'POST',
							url: 'sp/promote',
							data: { promotion: promotionValue },
							async: false,
							success: function(data) {
								makeMove(data);
								hidePromotionModal();
													
								if(data.responseType == 'OK' && !data.endOfGame) {
									getOpponentMove();
								}
							}
						});
					});
				}
			}
			result = data.responseType.toLowerCase();
		}
	});
	
	return result;
}

const makeMove = async (data) => {
	if(data.refresh) {
		await delay(100); // Avoid problems refreshing positions
		kingsmarch.setPosition(data.gameFEN);
	}
	data.capture 
		? kingsmarch.playCaptureSound()
		: kingsmarch.playMoveSound();
	// End game
	if(data.endOfGame) {
		let status = data.gameStatus;
		kingsmarch.freeze(END_GAME.get(status));
		showAlert("Game over");
		if(status === 'BLACK_WIN' && kingsmarch.config.orientation === 'black'
			|| status === 'WHITE_WIN' && kingsmarch.config.orientation === 'white') {
				$("#confetti-wrapper").addClass("show");
			}
	}
}

const getOpponentMove = async () => {
	await delay(100); // Avoid problems refreshing positions
	$.ajax({
		type: 'POST',
		url: 'sp/automove',
		success: function(data) {
			kingsmarch.board.move(data.move);
			makeMove(data);
		}
	});
}