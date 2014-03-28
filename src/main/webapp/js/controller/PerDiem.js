'use strict';

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
    //FIXME hard coded travel year
    $scope.travelExpensesRatesById = {};
    $http.get(serviceBaseUrl + 'per-diem/travel-expenses-rates/2014').success(function(data) {
        $scope.travelExpensesRates = data;
        $.map(data, function(entry) { //use also as map object;
            $scope.travelExpensesRatesById[entry.id] = entry;
        });
        console.log($scope.travelExpensesRatesById);
    });
    $http.get(serviceBaseUrl + 'projects').success(function(data) {
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
    };

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
                console.log( perDiem.inServiceFrom );
                console.log( perDiem.inServiceTo );
                console.log( (perDiem.inServiceTo - perDiem.inServiceFrom) / 1000 / 60 / 60 );
                var hour = (perDiem.inServiceTo - perDiem.inServiceFrom) / 1000 / 60 / 60;
                
                if (hour >= 8)
                    perDiem.charges = $scope.travelExpensesRatesById[terId].rateFrom8To24;
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

