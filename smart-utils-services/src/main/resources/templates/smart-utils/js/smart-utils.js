

function loadList() {

    var logFileList = ${logFileList};
    var $list = $('.log_list');

    for(var i=0;i<logFileList.length; i++) {

        var link = $('<a>');
        link.attr('href', '/services/smart-utils/logger?file='+logFileList[i])
        link.attr('target', '_blank');
        link.text(logFileList[i]);
        $list.append($('<li>').append(link));
    }
}

$(loadList);
