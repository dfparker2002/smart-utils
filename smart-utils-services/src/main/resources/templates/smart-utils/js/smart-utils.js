(function($) {

    function LogList() {

        var $logList = $('.log_list');

        function loadList(logList) {

            for (var i = 0; i < logList.length; i++) {
                $logList.append($('<option></option>').addClass(
                    'log_list__item').attr('value', logList[i])
                    .html(logList[i]));
            }
        }
        var status = $('#hide_old').attr('checked') ? "true" : "false";
        $.ajax({
            url: '/services/smart-utils/smartlogs?action=list&hideOld=' + status,
            dataType: 'json',
            cache: false,
            success: function(result) {
                if (result) {
                    $logList.empty();
                    loadList(result.fileList);
                }
            }
        });
    }

    $.fn.LogList = LogList;
}($));

(function($) {
    $('#hide_old').live("change", function() {
        $.fn.LogList();
    });
}($));

$(document).ready($.fn.LogList);
