Toastify.defaults.gravity = "toastify-bottom";

function showAlert(text) {
	if(text.length > 0) {
		var toast = Toastify({
			text: text,
			onClick: function() {
				toast.hideToast();
			}
		}).showToast();
	}
}
