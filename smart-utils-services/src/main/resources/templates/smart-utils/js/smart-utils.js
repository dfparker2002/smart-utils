(function($) {

    function LogList(opts) {
        var self = this;
        var $logList = $('.log_list');

        function loadList(logList) {

            for (var i = 0; i < logList.length; i++) {
                $logList.append($('<option></option>').addClass(
                    'log_list__item').attr('value', logList[i])
                    .html(logList[i]));
            }
        }

        $.ajax({
            url: '/services/smart-utils/smartlogs?action=list',
            dataType: 'json',
            cache: false,
            success: function(result) {
                if (result) {
                    loadList(result.fileList);
                }
            }
        });
    }

    $.fn.LogList = LogList;
}($));

$(document).ready($.fn.LogList);
