/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(function() {
    var timeRecording = new TimeRecording();
    $('#show-timerecording').click(function() {
        var individualId = 4;
        timeRecording.load(individualId, $('#from').val(), $('#until').val(), $('#startDate').val(), $('#endDate').val());
    });

    $('#send-timerecording').click(function() {
        timeRecording.updateServer();
    });
    
    
    $('#timerecording').on('focus', 'input', function(event) {
        $('#timerecording td').removeClass('focus-highlight');
        $('#timerecording td').removeClass('focus-highlight-field');
        var id = $(this).attr('id');
        var descrId = id.split('-')[3];
        var dateText = id.substring(0,10);
        $('.row-' + descrId).addClass('focus-highlight');
        $('.column-' + dateText).addClass('focus-highlight');
        $('#cell-'+id).addClass('focus-highlight-field');
    });

    //initial load
    var individualId = 4;
    timeRecording.load(individualId, $('#from').val(), $('#until').val(), $('#startDate').val(), $('#endDate').val());


});

TimeRecording = function() {
    var individualId;
    var startDate;
    var endDate;
    var jsonData;
    var dateArray;

    this.load = function(individualIdParam, fromParam, untilParam, startDateParam, endDateParam) {
        individualId = individualIdParam;
        startDate = startDateParam;
        endDate = endDateParam;

        $.get('rest/working-hours/' + individualId + '/range?qStart=' + fromParam + '&qEnd=' + untilParam, {
            'cache': false
        }).done(function(data) {
            jsonData = data;
            buildDateArray();
            paint();
        });


    };

    buildDateArray = function() {
        dateArray = new Array();
        console.log(startDate);
        var tmp = startDate.split("-");
        var startDateObj = new Date(tmp[0], tmp[1] - 1, tmp[2], 0, 0, 0);
        tmp = endDate.split("-");
        var endDateObj = new Date(tmp[0], tmp[1] - 1, tmp[2], 0, 0, 0);

        while (startDateObj.getTime() < endDateObj.getTime()) {
            var year = (1900 + startDateObj.getYear());
            var month = ('0' + (startDateObj.getMonth() + 1)).left(2);
            var day = ('0' + startDateObj.getDate()).left(2);
            var dateText = year + '-' + month + '-' + day;
            dateArray.push(dateText);
            startDateObj.setDate(startDateObj.getDate() + 1);
        }
    };

    String.prototype.left = function(length) {
        var str = this;
        var pos = (length > str.length) ? 0 : (str.length - length);
        return str.substring(pos);
    };

    paint = function() {
        var html = renderTable();
        
        //$('#timerecording').hide();
        $('#timerecording').html(html);
        //$('#timerecording').show();

        var trWidth = parseFloat($('#working-time-table').css('width'));
        $('#timerecording').css('width', (trWidth + 600) + 'px');

        updateTableData();
    };

    this.show = function() {
        alert(dateArray);
    };

    renderWorkingTimeTextField = function(dateText, descrId) {
        return '<input type="text" class="wt-field active" name="' + dateText + '-' + descrId + '" id="' + dateText + '-' + descrId + '"/>';
    };

    renderDescriptionTextField = function(descrId, description) {
        return '<input type="text" class="descr-field" name="descr-' + descrId + '" value="' + description + '"/>';
    };

    renderWorkingTableDateFormat = function(dateText) {
        var tmp = dateText.split('-');
        //var dateObj = new Date([0], tmp[1] - 1, tmp[2], 0, 0, 0);
        return tmp[2];
    };

    renderMonthName = function(monthText) {
        switch (monthText) {
            case '01':
                return 'January';
            case '02':
                return 'February';
            case '03':
                return 'March';
            case '04':
                return 'April';
            case '05':
                return 'May';
            case '06':
                return 'June';
            case '07':
                return 'July';
            case '08':
                return 'August';
            case '09':
                return 'September';
            case '10':
                return 'October';
            case '11':
                return 'November';
            case '12':
                return 'December';
        }
    };

    renderTable = function() {
        var wpDescrObj = jsonData.workPackageDescription;

        var html = '<table id="working-time-table">';

        console.log(dateArray);
        //table header 
        var wtMonth = {};
        $.each(dateArray, function(index, dateText) {
            var m = dateText.split('-')[1];
            if (!(m in wtMonth))
                wtMonth[m] = 0;
            wtMonth[m] = wtMonth[m] + 1;
        });

        html += '<tr><td class="wt-description"></td>';
        $.each(wtMonth, function(month, days) {
            html += '<td class="working-table-month" colspan="' + days + '">';
            html += renderMonthName(month);
            html += '</td>';
        });
        html += '</tr>';

        html += '<tr>';
        html += '<td class="wt-description">description</td>';
        $.each(dateArray, function(index, dateText) {
            html += '<td class="column-'+dateText+'">' + renderWorkingTableDateFormat(dateText) + '</td>';
        });
        html += '</tr>';

        //build description and fields
        //add two new descr. fields
        var newDescrId = uniqueId();
        wpDescrObj[newDescrId] = {'description': '', 'id': newDescrId, 'wpId': 5}; //FIXME WorkPackage hard coded
        newDescrId = uniqueId();
        wpDescrObj[newDescrId] = {'description': '', 'id': newDescrId, 'wpId': 5}; //FIXME WorkPackage hard coded
        console.log(wpDescrObj);
        $.each(wpDescrObj, function(key, value) {
            html += '<tr>';
            html += '<td class="wt-description row-'+value.id+'">' + renderDescriptionTextField(value.id, value.description) + '</td>';
            $.each(dateArray, function(index, dateText) {
                html += '<td id="cell-'+dateText+'-'+value.id+'" class="row-'+value.id+' column-'+dateText+'">' + renderWorkingTimeTextField(dateText, value.id) + '</td>';
            });
            html += '</tr>';
        });

        html += '</table>';

        return html;
    };


    renderDIVTable = function() {
        var wpDescrObj = jsonData.workPackageDescription;

        var html = '';
        html += '<div id="working-time-table2"> ';
        html += '<div id="descr-block block">';
        html += '<div class="descr label">description</div>';
        $.each(wpDescrObj, function(key, value) {
            html += '<div class="descr">' + renderDescriptionTextField(value.id, value.description) + '</div>';
        });
        html += '</div>';
        
        $.each(dateArray, function(index, dateText) {
            html += '<div class="time-block time-block-' + dateText + '">';
            html += '<div>' + renderWorkingTableDateFormat(dateText) + '</div>';
            $.each(wpDescrObj, function(key, value) {
                html += '<div>' + renderWorkingTimeTextField(dateText, value.id) + '</div>';
            });
            html += '</div>';
        });

        html += '</div>';
        return html;
    };

    updateTableData = function() {
        //set existing values
        var wdList = jsonData.workDayList;
        $.each(wdList, function(dateText, value) {
            var state = value.state; //TODO set state
            $.each(value.workingTimeByDescriptionId, function(descrId, workingTime) {
                var fieldId = dateText + '-' + descrId;
                $('#' + fieldId).val(workingTime);
            });
        });
    };

    this.readData = function() {
        //read work package description
        var workPackageDescription = {};
        $.each(($('.descr-field')), function(id, value) {
            var wpDescrId = value.name.split('-')[1];
            var descr = $(value).val();
            var wpId = 5;
            workPackageDescription[wpDescrId] = {"workPackageId": wpId, "description": descr};
        });

        //read all working hours by working day
        var workingDayMap = {};
        var wdList = jsonData.workDayList;
        $.each(dateArray, function(index, dateText) {

            //TODO state is a dummy value
            workingDayMap[dateText] = {"state": "ok"};
            workingDayMap[dateText]['workingTimeByDescriptionId'] = {};

            $.each(($('.wt-field.active.' + dateText)), function(id, value) {
                var wpDescrId = value.name.split('-')[3];
                var timeValue = $(value).val();
                if (!(timeValue === '')) {
                    timeValue = parseInt(timeValue);
                    workingDayMap[dateText]['workingTimeByDescriptionId'][wpDescrId] = timeValue;
                }
            });
        });

        var data = {
            "userId": individualId,
            "workingDayMap": workingDayMap,
            "workPackageDescription": workPackageDescription
        };
        return data;
    };

    this.updateServer = function() {
        var jsonData = this.readData();

        $.ajax({
            type: "PUT",
            url: "rest/working-hours",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(jsonData)
        }).done(function(data) {
            alert('am server');
        });

    };

};


var idCounter = 0;
uniqueId = function() {
    return '*' + (++idCounter);
};