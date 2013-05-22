
(function($) {

    function LogList(opts) {
        var self = this;
        this.opts = $.extend(true, {}, self.defaults, opts);

        var $logList = $('.log_list');

        function loadList(logList) {

            for(var i=0;i<logList.length; i++) {

                var $link = $('<a>');
                $link.attr('href', '/services/smart-utils/logger?file='+logList[i]);
                $link.attr('target', '_blank');
                $link.addClass('log_list__item_link');
                $link.text(logList[i]);
                $logList.append($('<li>').addClass('log_list__item').append($link));
            }
        }

        $.ajax({
              url: '/services/smart-utils/smartlogs?action=list'
            , dataType: 'json'
            , cache: false
            , success: function(result) {
                if (result) {
                    loadList(result.fileList);
                }
            }
        });
    }

    $.fn.LogList = LogList;
}($));

$(document).ready($.fn.LogList);
