/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(function() {
    var timeRecording = new TimeRecording();
    var individualId = 4;

    $('#show-timerecording').click(function() {

        timeRecording.load(individualId, $('#startDate').val(), $('#endDate').val());
    });

    $('#send-timerecording').click(function() {
        timeRecording.updateServer();
    });

    $('.week-selector').click(function() {
        var startDateText = timeRecording.getStartDate().split('-');
        var week = 7 * parseInt($(this).attr('data-week'));
        var startDateObj = new Date(startDateText[0], startDateText[1] - 1, startDateText[2], 0, 0, 0);
        var endDateObj = new Date(startDateText[0], startDateText[1] - 1, startDateObj.getDate() + week, 0, 0, 0);


        timeRecording.load(individualId, startDateObj.getText(), endDateObj.getText());

    });


    $('#timerecording').on('blur', 'input', function(event) {
        recalcSums();
    });

    $('#timerecording').on('focus', 'input', function(event) {
        $('#timerecording td').removeClass('focus-highlight');
        $('#timerecording td').removeClass('focus-highlight-field');
        var id = $(this).attr('id');
        var descrId = id.split('-')[3];
        var dateText = id.substring(0, 10);
        $('.row-' + descrId).addClass('focus-highlight');
        $('.column-' + dateText).addClass('focus-highlight');
        $('#cell-' + id).addClass('focus-highlight-field');
    });

    $('#today-text').html(new Date().getText());

    //initial load
    var individualId = 4;
    timeRecording.load(individualId, $('#startDate').val(), $('#endDate').val());



});

TimeRecording = function() {
    var individualId;
    var startDate;
    var endDate;
    var jsonData;
    var dateArray;

    this.load = function(individualIdParam, startDateParam, endDateParam) {
        individualId = individualIdParam;
        startDate = startDateParam;
        endDate = endDateParam;

        $.get('rest/working-hours/' + individualId + '/range?qStart=' + startDate + '&qEnd=' + endDate, {
            'cache': false
        }).done(function(data) {
            jsonData = data;
            buildDateArray();
            paint();
        });


    };

    this.getStartDate = function() {
        return startDate;
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
        var dateObj = new Date(tmp[0], tmp[1] - 1, tmp[2], 0, 0, 0);
        var weekDayNumber = dateObj.getDay();

        return '(' + renderWeekyDayAbbreviation(weekDayNumber) + ') ' + tmp[2];
    };


    renderWeekyDayAbbreviation = function(weekDayNumber) {
        switch (weekDayNumber) {
            case 0:
                return 'SU';
            case 1:
                return 'MO';
            case 2:
                return 'TU';
            case 3:
                return 'WE';
            case 4:
                return 'TH';
            case 5:
                return 'FR';
            case 6:
                return 'SA';
        }
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
            html += '<td class="column-' + dateText + '">' + renderWorkingTableDateFormat(dateText) + '</td>';
        });
        html += '</tr>';

        html += '<tr>';
        html += '<td>sum</td>';
        $.each(dateArray, function(index, dateText) {
            html += '<td id="sum-' + dateText + '"></td>';
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
            html += '<td class="wt-description row-' + value.id + '">' + renderDescriptionTextField(value.id, value.description) + '</td>';
            $.each(dateArray, function(index, dateText) {
                html += '<td id="cell-' + dateText + '-' + value.id + '" class="row-' + value.id + ' column-' + dateText + '">' + renderWorkingTimeTextField(dateText, value.id) + '</td>';
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
        recalcSums();
    };

    recalcSums = function() {
        var wdList = jsonData.workDayList;
        var sumByDate = {};

        $.each(dateArray, function(index, dateText) {
            $.each($('.column-' + dateText + ' .wt-field'), function(index, field) {
                var workingTime = $(field).val();
                if (workingTime !== '') {
                    //TODO check for number;
                    workingTime = parseInt( workingTime);
                    if (!sumByDate[dateText])
                        sumByDate[dateText] = workingTime;
                    else
                        sumByDate[dateText] = sumByDate[dateText] + workingTime;
                }
            });
        });

        $.each(wdList, function(dateText, value) {
            console.log(dateText + ' ' + sumByDate[dateText]);
            $('#sum-' + dateText).html(sumByDate[dateText]);
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

            $.each(($('.descr-field')), function(id, value) {
                var wpDescrId = value.name.split('-')[1];

                //$.each(($('.wt-field .active .' + dateText)), function(id, value) {
                //    var wpDescrId = value.name.split('-')[3];
                var timeValue = $('#' + dateText + '-' + wpDescrId).val();
                //var timeValue = $(value).val();
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
    return 'new' + (++idCounter);
};


String.prototype.left = function(length) {
    var str = this;
    var pos = (length > str.length) ? 0 : (str.length - length);
    return str.substring(pos);
};

Date.prototype.getText = function() {
    var year = '' + (1900 + this.getYear());
    var month = ('0' + (this.getMonth() + 1)).left(2);
    var day = ('0' + (this.getDate())).left(2);
    return year + '-' + month + '-' + day;
};
