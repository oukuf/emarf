$(function() {

	var $aside = $('div.aside');

	$aside.prepend('<div class="aside_slider"><a href="#"></a></div>');

	var $aside_slider = $('.aside_slider a');

	var $article = $('div.article');

	var duration = 500;

	$aside_slider.addClass('close');

	$aside_slider.click(function() {

		if ($aside_slider.hasClass('open')) {

			$aside.stop().animate({
				left : '0'
			}, duration, 'easeOutQuint');

			$article.stop().animate({
				'margin-left' : '10em'
			}, duration, 'easeOutQuint');

			$aside_slider.addClass('close');
			$aside_slider.removeClass('open');

		} else {

			$aside.stop().animate({
				left : '-9.5em'
			}, duration, 'easeOutQuint');

			$article.stop().animate({
				'margin-left' : '0px'
			}, duration, 'easeOutQuint');

			$aside_slider.addClass('open');
			$aside_slider.removeClass('close');
		}
	});

});
