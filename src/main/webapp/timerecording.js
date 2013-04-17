/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(function() {
    $('#show-timerecording').click(function() {
        var from = $('#from').val();
        var until = $('#until').val();
        var startDate = $('#startDate').val();
        var endDate = $('#endDate').val();

        $.get('rest/working-hours/4/range?qStart=' + from + '&qEnd=' + until)
                .done(function(data) {
            var p = new printData(startDate, endDate, data);
            p.paint();


        });

    });
});

printData = function(startDateParam, endDateParam, jsonDataParam) {
    var startDate = startDateParam;
    var endDate = endDateParam;
    this.jsonData = jsonDataParam;
    this.dateArray = new Array();


    this.buildDateArray = function() {
        var tmp = startDate.split("-");
        var startDateObj = new Date(tmp[0], tmp[1] - 1, tmp[2], 0, 0, 0);
        tmp = endDate.split("-");
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

printData.prototype.paint = function() {
    console.log(this.jsonData);
    var dateArray = this.dateArray;
    var wpDescrObj = this.jsonData.workPackageDescription;
    console.log(wpDescrObj);

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
        html += '<td><input type="text" name="descr' + value.id + '" value="' + value.description + '"/></td>';
        $.each(dateArray, function(index, dateText) {
            html += '<td><input type="text" name="' + dateText + '-' + value.id + '" id="' + dateText + '-' + value.id + '"/></td>';
        });
        html += '</tr>';
    });

    html += '</table>';
    $('#timerecording').html(html);

    //set existing values
    var wdList = this.jsonData.workDayList;
    $.each(wdList, function(dateText, value) {
        var state = value.state; //TODO set state
        $.each(value.workingTimeByDescriptionId, function(descrId, workingTime) {
            var fieldId = dateText + '-' +  descrId;
            console.log('change ' + fieldId + ' to ' + workingTime);
            $('#' + fieldId).val(workingTime);
        });
    });
};

printData.prototype.show = function() {
    alert(this.dateArray);
};

