function showPromotionModal() {
	$('#promotion-list > button > img').each(function() {
		let piece = $(this).parent().attr('data-value').toUpperCase();
		let color = kingsmarch.config.orientation == 'white' ? 'w' : 'b';
		$(this).attr('src', `img/pieces/${configuration.pieces}/${color}${piece}.svg`);
	});
	$("#promotion-wrapper").addClass("show");
}

function hidePromotionModal() {
	$("#promotion-wrapper").removeClass("show");
}