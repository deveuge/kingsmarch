const PIECES_NOTATION = ['q', 'r', 'b', 'n'];

function showPromotionModal() {
	$('#promotion-list > button > img').each(function(index) {
		let color = kingsmarch.config.orientation == 'white' ? 'w' : 'b';
		$(this).attr('src', `img/pieces/${configuration.pieces}/${color}${PIECES_NOTATION[index]}.svg`);
	});
	$("#promotion-wrapper").addClass("show");
}

function hidePromotionModal() {
	$("#promotion-wrapper").removeClass("show");
}