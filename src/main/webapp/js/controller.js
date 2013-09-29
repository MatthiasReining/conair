'use strict';
var serviceBaseUrl = 'rest/';

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