(function ($) {
  $.fn.loading = function () {
    var DEFAULTS = {
      backgroundColor: '#FFFFFF',
      progressColor: '#6EA204',
      duration: 4000
    };

    $(this).each(function () {
      var $target = $(this);

      var goalStatusString = $("#goal-status").text();
      var goalStatus = goalStatusString ? goalStatusString.substring(0, goalStatusString.indexOf("%")) : 10;

      var opts = {
        backgroundColor: $target.data('color') ? $target.data('color').split(',')[0] : DEFAULTS.backgroundColor,
        progressColor: $target.data('color') ? $target.data('color').split(',')[1] : DEFAULTS.progressColor,
        percent: goalStatus,
        duration: DEFAULTS.duration,
        text: goalStatusString
      };

      $target.append('<div class="rotate"></div>' +
          '<div class="left"></div>' +
          '<div class="right"></div>' +
          '<div class="">' +
          '<span>' + opts.text + ' </span>' +
          '</div>');

      if (opts.percent < 1) {
        $target.find('.background').css('background-color', opts.backgroundColor);
        $target.find('.left').css('background-color', opts.backgroundColor);
        $target.find('.rotate').css('background-color', opts.backgroundColor);
        $target.find('.right').css('background-color', opts.backgroundColor);
      }
      else {
        $target.find('.background').css('background-color', opts.backgroundColor);
        $target.find('.left').css('background-color', opts.backgroundColor);
        $target.find('.rotate').css('background-color', opts.progressColor);
        $target.find('.right').css('background-color', opts.progressColor);
      }

      var $rotate = $target.find('.rotate');
      setTimeout(function () {
        $rotate.css({
          'transition': 'transform ' + opts.duration + 'ms linear',
          'transform': 'rotate(' + opts.percent * 3.6 + 'deg)'
        });
      }, 1);

      if (opts.percent > 50) {
        var animationRight = 'toggle ' + (opts.duration / opts.percent * 50) + 'ms step-end';
        var animationLeft = 'toggle ' + (opts.duration / opts.percent * 50) + 'ms step-start';
        $target.find('.right').css({
          animation: animationRight,
          opacity: 1
        });
        $target.find('.left').css({
          animation: animationLeft,
          opacity: 0
        });
      }
    });
  }
})(jQuery);
