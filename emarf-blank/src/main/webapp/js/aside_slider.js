$(function() {

	var $aside = $('div.aside');

	$aside.prepend('<div class="button"><a href="#"></a></div>');

	var $button = $('.button a');

	//$button.height($aside.height());

	var $article = $('div.article');

	var duration = 500;

	$button.addClass('close');

	$button.click(function() {

		if ($button.hasClass('open')) {

			$aside.stop().animate({
				left : '0'
			}, duration, 'easeOutQuint');

			$article.stop().animate({
				'margin-left' : '10em'
			}, duration, 'easeOutQuint');

			$button.addClass('close');
			$button.removeClass('open');

		} else {

			$aside.stop().animate({
				left : '-9.5em'
			}, duration, 'easeOutQuint');

			$article.stop().animate({
				'margin-left' : '0px'
			}, duration, 'easeOutQuint');

			$button.addClass('open');
			$button.removeClass('close');
		}
	});

});
