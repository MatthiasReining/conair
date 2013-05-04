/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(function() {
    var timeRecording = new TimeRecording();
    var individualId = 4;

    projectInfo = new ProjectInfo(); //global scope
    projectInfo.load();


    $('#show-timerecording').click(function() {

        timeRecording.load(individualId, $('#startDate').val(), $('#endDate').val());
    });

    $('#send-timerecording').click(function() {
        timeRecording.updateServer();
    });

    $('.week-selector').click(function() {
        var endDateText = timeRecording.getEndDate().split('-');
        var week = -7 * parseInt($(this).attr('data-week'));
        var endDateObj = new Date(endDateText[0], endDateText[1] - 1, endDateText[2], 0, 0, 0);
        var startDateObj = new Date(endDateText[0], endDateText[1] - 1, endDateObj.getDate() + week, 0, 0, 0);


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

    $('#timerecording').on('change', '.projectInfoSelecter', function(event) {
        var wpdId = $(this).attr('id').split('-')[3];
        var projectId = $(this).val();
        
        var wpSelectField =  $('#workpackage-wpd-id-' + wpdId);
        var workPackages = projectInfo.projectInfo[projectId].workPackages;
   
        timeRecording.createOptions(workPackages, wpSelectField);
    });

    $('#today-text').html(new Date().getText());

    //initial load
    timeRecording.loadCurrentWorkingTime(individualId, 3);
    //timeRecording.load(individualId, $('#startDate').val(), $('#endDate').val());

});

TimeRecording = function() {
    var that = this;
    var individualId;
    var startDate;
    var endDate;
    var jsonData;
    var dateArray;

    this.loadCurrentWorkingTime = function(individualIdParam, weeks) {
        var now = new Date();
        var endDate = new Date(now.getFullYear(), now.getMonth(), now.getDate() - now.getDay() + 8); //+8 starts with son
        var startDate = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate() - (7*weeks));
        
        that.load(individualIdParam, startDate.getText(), endDate.getText());
    }

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

    function renderTableUnderscore() {

        var wpDescrObj = jsonData.workPackageDescription;

        //build description and fields
        //add two new descr. fields
        var newDescrId = _.uniqueId('new');
        wpDescrObj[newDescrId] = {'description': '', 'id': newDescrId, 'wpId': 5}; //FIXME WorkPackage hard coded
        newDescrId = _.uniqueId('new');
        wpDescrObj[newDescrId] = {'description': '', 'id': newDescrId, 'wpId': 5}; //FIXME WorkPackage hard coded


        _.templateSettings.variable = "data";

        // Grab the HTML out of our template tag and pre-compile it.
        var template = _.template(
                $("#underscore-template").html()
                );

        var container = {};
        container.jsonData = jsonData;
        container.dateArray = dateArray;
        container.workPackageDescription = jsonData.workPackageDescription;


        return template(that);
    }

    this.getStartDate = function() {
        return startDate;
    };
    
    this.getEndDate = function() {
        return endDate;
    };

    this.getDateArray = function() {
        return dateArray;
    };

    this.getWorkPackageDescription = function() {
        return jsonData.workPackageDescription;
    };

    this.getWorkPackage = function() {
        return jsonData.workPackage;
    };

    this.getProjectInformation = function() {
        return jsonData.projectInformation;
    };

    this.getWorkingTimeMonths = function() {
        var wtMonth = {};
        $.each(dateArray, function(index, dateText) {
            var m = dateText.split('-')[1];
            if (!(m in wtMonth))
                wtMonth[m] = 0;
            wtMonth[m] = wtMonth[m] + 1;
        });
        return wtMonth;


    };

    this.renderMonthName = function(monthText) {
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

    buildDateArray = function() {
        dateArray = new Array();
        console.log(startDate);
        var tmp = startDate.split("-");
        var startDateObj = new Date(tmp[0], tmp[1] - 1, tmp[2], 0, 0, 0);
        tmp = endDate.split("-");
        var endDateObj = new Date(tmp[0], tmp[1] - 1, tmp[2], 0, 0, 0);

        while (startDateObj.getTime() < endDateObj.getTime()) {
            var year = (1900 + startDateObj.getYear());
            var month = ('0' + (startDateObj.getMonth() + 1)).right(2);
            var day = ('0' + startDateObj.getDate()).right(2);
            var dateText = year + '-' + month + '-' + day;
            dateArray.push(dateText);
            startDateObj.setDate(startDateObj.getDate() + 1);
        }
    };

    function paint() {
        var html = renderTableUnderscore();

        //$('#timerecording').hide();
        $('#timerecording').html(html);
        updateProjectWorkPackageSelectFields();

        //$('#timerecording').show();

        var trWidth = parseFloat($('#working-time-table').css('width'));
        $('#timerecording').css('width', (trWidth + 600) + 'px');

        updateTableData();
    }
    ;

    updateProjectWorkPackageSelectFields = function() {
        $.each(jsonData.workPackageDescription, function(wpdKey, wpdValue) {

            var wpId = wpdValue.wpId;
            var projectId = jsonData.workPackage[wpId].projectId;

            that.createOptions(projectInfo.projectInfo, $('#project-wpd-id-' + wpdKey), projectId);
            
            var workPackages = projectInfo.projectInfo[projectId].workPackages;
            that.createOptions(workPackages, $('#workpackage-wpd-id-' + wpdKey), wpId);
   
        });

    };

    this.createOptions = function(mapValues, selectField, selectedValue) {

        $(selectField).empty();
        var l = new Array();
        $.each(mapValues, function(key, value) {
            var optElem = $("<option/>").val(key).text(value.name);
            if (key === ''+selectedValue)
                optElem.attr('selected',true);
            l.push(optElem);
        });
        l.sort(function(a, b) {
            return a.text() > b.text();
        });
        $.each(l, function(index, option) {
            option.appendTo(selectField);
        });

    };

    this.show = function() {
        alert(dateArray);
    };

    this.renderWorkingTableDateFormat = function(dateText) {
        var tmp = dateText.split('-');
        var dateObj = new Date(tmp[0], tmp[1] - 1, tmp[2], 0, 0, 0);
        var weekDayNumber = dateObj.getDay();

        return '(' + renderWeekyDayAbbreviation(weekDayNumber) + ') ' + tmp[2];
    };


    function renderWeekyDayAbbreviation(weekDayNumber) {
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
    }
    ;

    this.getMonthName = function(monthText) {
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


    function updateTableData() {
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
    }
    ;

    recalcSums = function() {
        var wdList = jsonData.workDayList;
        var sumByDate = {};

        $.each(dateArray, function(index, dateText) {
            $.each($('.column-' + dateText + ' .wt-field'), function(index, field) {
                var workingTime = $(field).val();
                if (workingTime !== '') {
                    //TODO check for number;
                    workingTime = parseInt(workingTime);
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

    function readData() {
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
    }
    ;

    this.updateServer = function() {
        var jsonData = readData();

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


String.prototype.right = function(length) {
    var str = this;
    var pos = (length > str.length) ? 0 : (str.length - length);
    return str.substring(pos);
};

Date.prototype.getText = function() {
    var year = '' + (1900 + this.getYear());
    var month = ('0' + (this.getMonth() + 1)).right(2);
    var day = ('0' + (this.getDate())).right(2);
    return year + '-' + month + '-' + day;
};


ProjectInfo = function() {
    this.projectInfo;
    var that = this;

    var htmlProjectSelectField;

    this.load = function() {
        $.get('rest/projects', {
            'cache': true
        }).done(function(data) {
            console.log(data);
            that.projectInfo = data;
        });
    };

    this.renderProjectSelectField = function(wpdId, selectedProjectId) {
        //if (undefined !== htmlProjectSelectField)
        //    return htmlProjectSelectField;
        var html = '';
        selectedProjectId = '' + selectedProjectId;
        html += '<SELECT class="projectInfoSelecter" id="project-wpd-id-' + wpdId + '">';
        $.each(that.projectInfo, function(key, value) {
            var selected = '';
            if (key === selectedProjectId)
                selected = ' selected ';
            html += '<OPTION value="' + key + '"' + selected + '>' + value.name + '</OPTION>';
        });
        return html;
        //htmlProjectSelectField = html;

        //return htmlProjectSelectField;
    };

    this.renderWorkPackageSelectField = function(wpdId, projectId, selectedWpId) {
        $('#workpackage-wpd-id-' + wpdId).empty();

        var project = that.projectInfo[projectId];

        var l = new Array();
        $.each(project.workPackages, function(key, value) {
            l.push($("<option/>").val(key).text(value.name));
        });
        l.sort(function(a, b) {
            return a.text() > b.text();
        });
        $.each(l, function(index, option) {
            option.appendTo('#workpackage-wpd-id-' + wpdId);
        });
    };


};