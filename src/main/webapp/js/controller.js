'use strict';
var serviceBaseUrl = 'rest/';
function NaviCtrl($scope, $location) {
    $scope.isActive = function(route) {
        return ($location.path().indexOf(route) > -1);
    };
    $scope.test = function(path) {
        alert($scope.isActive(path));
    };
}
;
function ProjectListCtrl($scope, $http) {
    $http.get(serviceBaseUrl + 'projects/list').success(function(data) {
        console.log(data);
        $scope.projects = data;
    });
}

function ProjectCtrl($scope, $routeParams, $http) {
    var serviceURL = serviceBaseUrl + 'projects/v0.1/' + $routeParams.projectKey;
    console.log('in ProjectCtrl');
    //init click events
    $('#project-start-area').datetimepicker().on('changeDate', function(e) {
        console.log(e.date);
        datepicker2model(e, $scope);
    });
    $('#project-end-area').datetimepicker().on('changeDate', function(e) {
        datepicker2model(e, $scope);
    });
    $http.get(serviceURL).success(function(data) {
        console.log(data);
        //convert date
        data.projectStart = new Date(data.projectStart).getText();
        data.projectEnd = new Date(data.projectEnd).getText();
        $scope.project = data;
    });
    $scope.selectWP = function(workPackage) {
        console.log(workPackage);
        $scope.currentWP = workPackage;
    };
    $scope.sendToServer = function() {
        console.log('->sendToServer');
        console.log($scope.project);
        $http.put(serviceURL, $scope.project).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            $scope.project = data;
        });
    };
}

function PerDiemsSelectorCtrl($scope, $http) {

}

function PerDiemsCtrl($scope, $routeParams, $http, $rootScope) {
    var serviceURL = serviceBaseUrl + 'per-diem/' + $rootScope.user.id + '/' + $routeParams.yearMonth;
    $http.get(serviceURL).success(function(data) {
        console.log(data);
        $scope.perDiemsData = data;
        calcSum();
    });
    //load destination list
    //FIXME cache
    $scope.travelExpensesRatesById = {};
    $http.get(serviceBaseUrl + 'per-diem/travel-expenses-rates').success(function(data) {
        $scope.travelExpensesRates = data;
        $.map(data, function(entry) { //use also as map object;
            $scope.travelExpensesRatesById[entry.id] = entry;
        });
        console.log($scope.travelExpensesRatesById);
    });
    $http.get(serviceBaseUrl + 'projects/list').success(function(data) {
        console.log(data);
        $scope.projects = data;
    });
    $scope.removePerDiem = function(perDiem) {
        perDiem.projectId = '';
        perDiem.travelExpensesRateId = '';
        perDiem.fullTime = '';
        perDiem.inServiceFrom = '';
        perDiem.inServiceTo = '';
        perDiem.charges = '';
        calcSum();
    };
    $scope.copyPerDiem = function(targetPerDiem, sourcePerDiem) {
        targetPerDiem.projectId = sourcePerDiem.projectId;
        targetPerDiem.travelExpensesRateId = sourcePerDiem.travelExpensesRateId;
        targetPerDiem.fullTime = sourcePerDiem.fullTime;
        targetPerDiem.inServiceFrom = sourcePerDiem.inServiceFrom;
        targetPerDiem.inServiceTo = sourcePerDiem.inServiceTo;
        targetPerDiem.charges = sourcePerDiem.charges;
        calcSum();
    }

    $scope.calcPerDiem = function(perDiem) {
        perDiem.charges = '';
        var terId = perDiem.travelExpensesRateId;
        var fullTime = perDiem.fullTime;
        if (fullTime) {
            perDiem.charges = $scope.travelExpensesRatesById[terId].rate24h;
            perDiem.inServiceFrom = '';
            perDiem.inServiceTo = '';
        } else {
            if (perDiem.inServiceFrom !== null && perDiem.inServiceTo !== null) {
                var from = perDiem.inServiceFrom.split(':');
                var to = perDiem.inServiceTo.split(':');
                var fromMinutes = (parseInt(from[0]) * 60) + (parseInt(from[1]));
                var toMinutes = (parseInt(to[0]) * 60) + (parseInt(to[1]));
                var length = (toMinutes - fromMinutes) / 60;
                if (length >= 14)
                    perDiem.charges = $scope.travelExpensesRatesById[terId].rateFrom14To24;
                else if (length > 8 && length <= 14) //TODO >8 or >=8?
                    perDiem.charges = $scope.travelExpensesRatesById[terId].rateFrom8To14;
                else
                    perDiem.charges = '';
            }

        }
        calcSum();
    };
    var calcSum = function() {
        var sum = 0;
        angular.forEach($scope.perDiemsData.perDiems, function(value, key) {
            if (angular.isNumber(value.charges))
                sum += value.charges;
        });
        $scope.perDiemsData.sum = sum;
    };
    $scope.sendToServer = function() {
        console.log('->sendToServer');
        console.log($scope.perDiemsData);
        $http.put(serviceURL, $scope.perDiemsData).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            $scope.perDiemsData = data;
            calcSum();
        });
    };
}



function TravelCostsListCtrl($scope, $http) {
    $http.get(serviceBaseUrl + 'travel-costs').success(function(data) {
        console.log(data);
        $scope.travelCosts = data;
    });
}

function TravelCostsCtrl($scope, $http) {
//selectCurrentNavi('travel-costs');
//$http.get(serviceBaseUrl + 'travel-costs').success(function(data) {
//    console.log(data);
//    $scope.travelCosts = data;
//});
}


function VacationCtrl($scope, $http, $routeParams, $modal) {
    $('.panel-heading').css('background-color', 'mediumvioletred');
    $('.panel-heading').css('color', 'white');
    var serviceURL = serviceBaseUrl + 'vacations/' + $routeParams.individualId;
    $scope.stateText = {
        0: 'new',
        1: 'for approval',
        5: 'approved',
        9: 'rejected'
    };
    $scope.vacation = {};
    $('#vacationFromUntil').daterangepicker({
        showWeekNumbers: true,
        format: 'dd, L',
        separator: '    -    ',
        locale: {firstDay: 1}
    },
    function(start, end) {
        $scope.vacation.vacationFrom = start.format('YYYY-MM-DD');
        $scope.vacation.vacationUntil = end.format('YYYY-MM-DD');
    });
    var refresh = function() {
        $http.get(serviceURL).success(function(data) {
            console.log(data);
            //convert date

            $scope.vacations = data;
            var vacationDays = $scope.vacations.vacationDays;
            var vacationDaysByMonth = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
            for (var i = 0; i < vacationDays.length; i++) {
                var d = vacationDays[i].convert2Date();
                vacationDaysByMonth[d.getMonth()] = vacationDaysByMonth[d.getMonth()] + 1;
            }
            $('#vacation-bar-chart').sparkline(vacationDaysByMonth, {type: 'bar', height: '80px', barWidth: 46, barSpacing: 8});
        });
    };
    //initalize load
    refresh();
    $scope.removeVacationRecord = function(vacationRecord) {

        var title = 'Remove vacation request';
        var message = 'Should vacation request removed? (from ' + vacationRecord.vacationFrom + ')';
        MsgBox($modal, {title: title, message: message}, function() {
            console.log('remove record ' + vacationRecord);
            $http.delete(serviceURL + '/' + vacationRecord.id).success(function(data) {
                refresh();
            });
        });
    };
    $scope.calculateVacationDays = function() {
        $http.get(serviceURL + '/calculateVacationDays',
                {params: {'vacationFrom': $scope.vacation.vacationFrom,
                        'vacationUntil': $scope.vacation.vacationUntil
                    }}
        ).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            $scope.vacation.calculateVacationDays = data.vacationDays;
            $scope.vacation.numberOfDays = data.vacationDays;
        });
    };
    $scope.issueRequestForTimeOff = function() {
        console.log($scope.requestForm.$valid);
        if (!$scope.requestForm.$valid)
            return;
        console.log('->sendToServer');
        console.log($scope.vacation);
        $scope.vacation.individualId = $routeParams.individualId;
        $http.post(serviceURL, $scope.vacation)
                .success(function(data) {
            refresh();
            $scope.vacation.vacationFrom = null;
            $scope.vacation.vacationUntil = null;
            $scope.vacation.calculateVacationDays = null;
            $scope.vacation.numberOfDays = null;
            $('#vacationFromUntil').val('');

            MsgBox($modal, {title: 'Request for Time Off', message: 'Your request for time off was submitted', hideCancelBtn: true});
        }).error(function(data) {
            console.log(data);
            // called asynchronously if an error occurs
            // or server returns response with status
            // code outside of the <200, 400) range
        });
    };
    $scope.selectVacationRecord = function(vacationRecord) {
        console.log(vacationRecord);
        $scope.vacation = vacationRecord;
        $scope.calculateVacationDays();
        $scope.vacation = vacationRecord;
    };
}



$(document).ready(function() {

    $(".has_submenu > a").click(function(e) {
        //e.preventDefault();
        var menu_li = $(this).parent("li");
        var menu_ul = $(this).next("ul");
        if (menu_li.hasClass("open")) {
            menu_ul.slideUp(350);
            menu_li.removeClass("open");
        }
        else {
            $(".navi > li > ul").slideUp(350);
            $(".navi > li").removeClass("open");
            menu_ul.slideDown(350);
            menu_li.addClass("open");
        }
    });
    $('.mainbar').on("click", '.wminimize', function(e) {
        e.preventDefault();
        var $wcontent = $(this).parent().parent().next('.widget-content');
        if ($wcontent.is(':visible'))
        {
            $(this).children('i').removeClass('icon-chevron-up');
            $(this).children('i').addClass('icon-chevron-down');
        }
        else
        {
            $(this).children('i').removeClass('icon-chevron-down');
            $(this).children('i').addClass('icon-chevron-up');
        }
        $wcontent.toggle(500);
    });
});
function TimeRecordingCtrl($scope, $http, $routeParams) {
    var weeks = $routeParams.weeks;
    if (angular.isUndefined(weeks))
        weeks = '2';
    $http.get(serviceBaseUrl + 'projects').success(function(data) {
        console.log(data);
        $scope.projects = data;
    });
    $http.get(serviceBaseUrl + 'time-recording/range/' + weeks).success(function(data) {
        console.log(data);
        $scope.timeRecording = data;
    });
    $scope.addRow = function() {
        console.log('->addRow');
        $scope.timeRecording.workingHours.push({
            "days": {}
        });
    };
    $scope.sendToServer = function() {
        console.log('->sendToServer');
        console.log($scope.timeRecording);
        $http.put(serviceBaseUrl + "time-recording", $scope.timeRecording).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            $scope.project = data;
        });
    };
}


function TaskVacationApprovalListCtrl($scope, $http) {
    $http.get(serviceBaseUrl + 'vacations/tasks').success(function(data) {
        console.log(data);
        $scope.tasks = data;
    });
}

function TaskVacationApprovalCtrl($scope, $routeParams, $http) {
    $http.get(serviceBaseUrl + 'vacations/tasks/' + $routeParams.vacationRecordId).success(function(data) {
        console.log(data);
        $scope.vacation = data;
    });
    $scope.rejectRequestForTimeOff = function() {
        $http.put(serviceBaseUrl + 'vacations/tasks/' + $scope.vacation.id + '/reject', $scope.vacation).success(function(data) {
            alert("abgelehnt");
        });
    };
    $scope.approveRequestForTimeOff = function() {
        $http.put(serviceBaseUrl + 'vacations/tasks/' + $scope.vacation.id + '/approve', $scope.vacation).success(function(data) {
            alert("genehmigt");
        });
    };
}


function VacationManagerCtrl($scope, $routeParams, $http) {
    $('.panel-heading').css('background-color', 'mediumvioletred');
    $('.panel-heading').css('color', 'white');

    $http.get(serviceBaseUrl + 'vacations').success(function(data) {
        console.log(data);
        $scope.vacations = data;
        var progess100percent = 45; //totalDays + residualLeaveYearBefore, means ca. 28 + puffer

        angular.forEach($scope.vacations, function(value) {

            var vacationDaysPerYear = value.numberOfVacationDays;
            var residualLeaveYearBefore = value.residualLeaveYearBefore;
            var approvedVacationDays = value.approvedVacationDays;
            var vacationDaysPerYearProgress = Math.floor(vacationDaysPerYear / progess100percent * 100);
            //this year
            var thisYearApproved = Math.min(vacationDaysPerYear, approvedVacationDays);
            var thisYearApprovedProgress = Math.floor(thisYearApproved / progess100percent * 100);
            var thisYearResidualLeave = Math.min( (vacationDaysPerYear-approvedVacationDays), vacationDaysPerYear);
            var thisYearResidualLeaveProgress = vacationDaysPerYearProgress - thisYearApprovedProgress;
            //appendix
            var appendixApproved = Math.max((approvedVacationDays - vacationDaysPerYear), 0);
            var appendixApprovedProgress = Math.floor(appendixApproved / progess100percent * 100);
            var appendixResidualLeave = (residualLeaveYearBefore) - appendixApproved;
            var appendixResidualLeaveProgress = Math.floor(appendixResidualLeave / progess100percent * 100);
            value.progress = [
                {"type": "this-year-approved",       "typeText": "approved",                     "value": thisYearApproved, "percent": thisYearApprovedProgress, "color": "green"},
                {"type": "this-year-residual-leave", "typeText": "residual leave",               "value": thisYearResidualLeave, "percent": thisYearResidualLeaveProgress, "color": "red"},
                {"type": "appendix-approved",        "typeText": "approved",                     "value": appendixApproved, "percent": appendixApprovedProgress, "color": "orangered"},
                {"type": "appendix-residual-leave",  "typeText": "residual leave (year before)", "value": appendixResidualLeave, "percent": appendixResidualLeaveProgress, "color": "yellowgreen"}
            ];
            console.log('--> ' + vacationDaysPerYear);
            console.log(value.progress);

        });

    });
}
;


function IndividualsCtrl($scope, $http) {
    $http.get(serviceBaseUrl + 'resources/individuals').success(function(data) {
        console.log(data);
        $scope.individuals = data;
    });
    $scope.selectIndividual = function(individual) {
        $scope.currentIndividual = individual;
    };
    $scope.sendToServer = function() {
        console.log($scope.currentIndividual);
        $http.put(serviceBaseUrl + 'resources/individuals', $scope.currentIndividual).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            alert('info auf dem server...');
        });
    };
}
;
var MsgBoxCtrl = function($scope, $modalInstance, content) {

    $scope.content = content;

    if (angular.isUndefined($scope.content.title))
        $scope.content.titel = '';
    if (angular.isUndefined($scope.content.message))
        $scope.content.message = '';
    if (angular.isUndefined($scope.content.okBtn))
        $scope.content.okBtn = 'OK';
    if (angular.isUndefined($scope.content.cancelBtn))
        $scope.content.cancelBtn = 'Cancel';
    if (angular.isUndefined($scope.content.hideCancelBtn))
        $scope.content.hideCancelBtn = false;

    $scope.ok = function() {
        $modalInstance.close();
    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};
function MsgBox($modal, param, fnYesCallback) {
    $modal.open({
        templateUrl: 'snippets/msgbox.html',
        controller: MsgBoxCtrl,
        resolve: {
            content: function() {
                return param;
            }
        }
    }).result.then(fnYesCallback);
}
;