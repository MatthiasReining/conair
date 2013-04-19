/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(function() {
    var timeRecording;
    $('#show-timerecording').click(function() {
        var from = $('#from').val();
        var until = $('#until').val();
        var startDate = $('#startDate').val();
        var endDate = $('#endDate').val();

        var individualId = 4;

        $.get('rest/working-hours/' + individualId + '/range?qStart=' + from + '&qEnd=' + until)
                .done(function(data) {
            timeRecording = new TimeRecording(individualId, startDate, endDate, data);
            timeRecording.paint();
        });

    });

    $('#send-timerecording').click(function() {
        timeRecording.updateServer();
    });


});

TimeRecording = function(individualIdParam, startDateParam, endDateParam, jsonDataParam) {
    var individualId = individualIdParam;
    var startDate = startDateParam;
    var endDate = endDateParam;
    var jsonData = jsonDataParam;
    var dateArray = new Array();

    buildDateArray = function() {
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

    this.paint = function() {
        console.log(jsonData);

        var html = this.renderTable();

        $('#timerecording').html(html);

        this.updateTableData();
    };

    this.show = function() {
        alert(dateArray);
    };

    renderWorkingTimeTextField = function(dateText, descrId) {
        return '<input type="text" class="wt-field active ' + dateText + '" name="' + dateText + '-' + descrId + '" id="' + dateText + '-' + descrId + '"/>';
    };

    renderDescriptionTextField = function(descrId, description) {
        return '<input type="text" class="descr" name="descr-' + descrId + '" value="' + description + '"/>';
    };

    this.renderTable = function() {
        var wpDescrObj = jsonData.workPackageDescription;

        var html = '<table border="1">';

        html += '<tr>';
        html += '<td>description</td>';
        $.each(dateArray, function(index, dateText) {
            html += '<td>' + dateText + '</td>';
        });
        html += '</tr>';


        //build description and fields
        $.each(wpDescrObj, function(key, value) {
            html += '<tr>';
            html += '<td>' + renderDescriptionTextField(value.id, value.description) + '</td>';
            $.each(dateArray, function(index, dateText) {
                html += '<td>' + renderWorkingTimeTextField(dateText, value.id) + '</td>';
            });
            html += '</tr>';
        });

        html += '</table>';
        return html;
    };


    this.renderDIVTable = function() {
        var wpDescrObj = jsonData.workPackageDescription;

        var html = '';
        html += '<div id="descr-block">';
        html += '<div>description</div>';
        $.each(wpDescrObj, function(key, value) {
            html += '<div>' + renderDescriptionTextField(value.id, value.description) + '</div>';
        });
        html += '</div>';
        $.each(dateArray, function(index, dateText) {
            html += '<div class="time-block time-block-' + dateText + '">';
            html += '<div>' + dateText + '</div>';
            $.each(wpDescrObj, function(key, value) {
                html += '<div>' + renderWorkingTimeTextField(dateText, value.id) + '</div>';
            });
            html += '</div>';
        });
        return html;
    };

    this.updateTableData = function() {
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
        $.each(($('.descr')), function(id, value) {
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

    //call after obj is initalized
    buildDateArray();
};