
(function($) {

    function LogList(opts) {
        var self = this;
        this.opts = $.extend(true, {}, self.defaults, opts);

        var $logList = $('.log_list');

        function loadList(logList) {

            for(var i=0;i<logList.length; i++) {
                $logList.append($('<option></option>').addClass('log_list__item').attr('value', logList[i]).html(logList[i]));
            }

//            $(".log_list__item").click(
//                function (e) {
//                    e.preventDefault();
//
//                    $('ul.log_item_menu').find('li:first').find('a').attr('href', '/services/smart-utils/logger?file='
//                        + $(this).attr('logname'));
//
//                    $('ul.log_item_menu').animate({
//                        'left': e.pageX-10,
//                        'top': e.pageY-10
//                    }).slideDown('fast');
//                }
//            );

//            $('ul.log_item_menu').mouseleave(function() {
//                $(this).slideUp('fast');
//            });
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
