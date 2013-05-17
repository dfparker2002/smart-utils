

function loadList() {

    var logFileList = ${logFileList};
    var $list = $('.log_list');

    for(var i=0;i<logFileList.length; i++) {
        $list.append('<a href="#">'+logFileList[i]+'</a>').append('<br>');
    }

}

$(loadList);
