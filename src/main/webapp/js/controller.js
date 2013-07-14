'use strict';

var serviceBaseUrl = 'http://localhost:8080/project-business-time-recording/rest/';

function NaviCtrl($scope, $location) {
    $scope.isActive = function(route) {
        return ($location.path().indexOf(route) > -1);
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

function VacationCtrl($scope, $http, $rootScope) {
    var serviceURL = serviceBaseUrl + 'vacations';
    
    $scope.stateText = {
      0: 'requested'  
    };

    $scope.vacation = {};
    //init click events
    $('#vacation-input-from').datetimepicker().on('changeDate', function(e) {
        console.log(e.date);
        console.log($scope);
        datepicker2model(e, $scope);
    });
    $('#vacation-input-until').datetimepicker().on('changeDate', function(e) {
        datepicker2model(e, $scope);
    });

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
        $('#vacation-bar-chart').sparkline(vacationDaysByMonth, {type: 'bar', height: '80px', barWidth: 48, barSpacing: 8});
    });

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
        console.log('->sendToServer');
        console.log($scope.vacation);

        $scope.vacation.individualId = $rootScope.user.id;

        $http.post(serviceURL, $scope.vacation).success(function(data) {
            alert('Urlaubsantrag eingereicht');
        });
    };
    
    $scope.selectVacationRecord = function(vacationRecord) {
        console.log(vacationRecord);
        $scope.vacation = vacationRecord;
        $scope.calculateVacationDays();
        $scope.vacation = vacationRecord;     
    };
    
    $scope.removeVacationRecord = function(vacationRecord) {
        console.log('remove record ' + vacationRecord);
        $http.delete(serviceURL + '/' + vacationRecord.id).success(function(data) {
            alert('Urlaubsantrag gelöscht');
        });
    };
}



$(document).ready(function() {

    $(".has_submenu > a").click(function(e) {
        e.preventDefault();
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


