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

        $.get('rest/working-hours/4/range?qStart=' + from + '&qEnd=' + until)
                .done(function(data) {
            timeRecording = new TimeRecording(startDate, endDate, data);
            timeRecording.paint();
        });

    });

    $('#send-timerecording').click(function() {
        timeRecording.readData();
    });


});

TimeRecording = function(startDateParam, endDateParam, jsonDataParam) {
    this.startDate = startDateParam;
    this.endDate = endDateParam;
    this.jsonData = jsonDataParam;
    this.dateArray = new Array();

    this.buildDateArray = function() {
        var tmp = this.startDate.split("-");
        var startDateObj = new Date(tmp[0], tmp[1] - 1, tmp[2], 0, 0, 0);
        tmp = this.endDate.split("-");
        var endDateObj = new Date(tmp[0], tmp[1] - 1, tmp[2], 0, 0, 0);

        while (startDateObj.getTime() < endDateObj.getTime()) {
            var year = (1900 + startDateObj.getYear());
            var month = ('0' + (startDateObj.getMonth() + 1)).left(2);
            var day = ('0' + startDateObj.getDate()).left(2);
            var dateText = year + '-' + month + '-' + day;
            this.dateArray.push(dateText);
            startDateObj.setDate(startDateObj.getDate() + 1);
        }
    };

    String.prototype.left = function(length) {
        var str = this;
        var pos = (length > str.length) ? 0 : (str.length - length);
        return str.substring(pos);
    };


    //call after obj is initalized
    this.buildDateArray();
};

TimeRecording.prototype.paint = function() {
    console.log(this.jsonData);

    var html = this.renderTable();

    $('#timerecording').html(html);

    this.updateTableData();
};

TimeRecording.prototype.show = function() {
    alert(this.dateArray);
};

TimeRecording.prototype.renderWorkingTimeTextField = function(dateText, descrId) {
    return '<input type="text" name="' + dateText + '-' + descrId + '" id="' + dateText + '-' + descrId + '"/>';
};

TimeRecording.prototype.renderTable = function() {
    var dateArray = this.dateArray;
    var wpDescrObj = this.jsonData.workPackageDescription;

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
        html += '<td><input type="text" class="descr" name="descr-' + value.id + '" value="' + value.description + '"/></td>';
        $.each(dateArray, function(index, dateText) {
            //var fieldHTML = this.renderWorkingTimeTextField(dateText, value.id);
            //console.log(fieldHTML);
            //html += '<td>' + fieldHTML + '</td>';
            html += '<td><input type="text" name="' + dateText + '-' + value.id + '" id="' + dateText + '-' + value.id + '"/></td>';
        });
        html += '</tr>';
    });

    html += '</table>';
    return html;
};


TimeRecording.prototype.renderDIVTable = function() {
    var dateArray = this.dateArray;
    var wpDescrObj = this.jsonData.workPackageDescription;

    var html = '';
    html += '<div id="descr-block">';
    html += '<div>description</div>';
    $.each(wpDescrObj, function(key, value) {
        html += '<div><input type="text" name="descr' + value.id + '" value="' + value.description + '"/></div>';
    });
    html += '</div>';
    $.each(dateArray, function(index, dateText) {
        html += '<div class="time-block time-block-' + dateText + '">';
        html += '<div>' + dateText + '</div>';
        $.each(wpDescrObj, function(key, value) {
            //html += '<div>' + this.renderWorkingTimeTextField(dateText, value.id) + '</div>';
            html += '<div><input type="text" name="' + dateText + '-' + value.id + '" id="' + dateText + '-' + value.id + '"/></div>';
        });
        html += '</div>';
    });
    return html;
};

TimeRecording.prototype.updateTableData = function() {
    //set existing values
    var wdList = this.jsonData.workDayList;
    $.each(wdList, function(dateText, value) {
        var state = value.state; //TODO set state
        $.each(value.workingTimeByDescriptionId, function(descrId, workingTime) {
            var fieldId = dateText + '-' + descrId;
            console.log('change ' + fieldId + ' to ' + workingTime);
            $('#' + fieldId).val(workingTime);
        });
    });
};

TimeRecording.prototype.readData = function() {

    var workPackageDescription = {};

    $.each(($('.descr')), function(id, value) {
        var wpDescrId = value.name;
        var descr = $(value).val();
        var wpId = 5;
        workPackageDescription[wpDescrId] = {'workPackageId': 5, 'description': descr};
    });

    //set existing values
    console.log(workPackageDescription);
};