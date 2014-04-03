'use strict';

function PerDiemsSelectorCtrl($scope, $http) {

}

function PerDiemsCtrl($scope, $routeParams, $http, $rootScope) {
    var serviceURL = serviceBaseUrl + 'per-diem/' + $rootScope.user.id + '/' + $routeParams.yearMonth;

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

        $http.get(serviceBaseUrl + 'projects').success(function(data) {
            console.log(data);
            $scope.projects = data;


            $http.get(serviceURL).success(function(data) {
                console.log(data);
                $scope.perDiemsData = data;
                calcSum();
            });
        });

    });

    $scope.timeSelection = [
        { key: '0.0'  , value: '00:00' },
        { key: '0.5', value: '00:30' },
        { key: '1.0'  , value: '01:00' },
        { key: '1.5', value: '01:30' },
        { key: '2.0'  , value: '02:00' },
        { key: '2.5', value: '02:30' },
        { key: '3.0'  , value: '03:00' },
        { key: '3.5', value: '03:30' },
        { key: '4.0'  , value: '04:00' },
        { key: '4.5', value: '04:30' },
        { key: '5.0'  , value: '05:00' },
        { key: '5.5', value: '05:30' },
        { key: '6.0'  , value: '06:00' },
        { key: '6.5', value: '06:30' },
        { key: '7.0'  , value: '07:00' },
        { key: '7.5', value: '07:30' },
        { key: '8.0'  , value: '08:00' },
        { key: '8.5', value: '08:30' },
        { key: '9.0'  , value: '09:00' },
        { key: '9.5', value: '09:30' },
        { key: '10.0'  , value: '10:00' },
        { key: '10.5', value: '10:30' },
        { key: '11.0'  , value: '11:00' },
        { key: '11.5', value: '11:30' },
        { key: '12.0'  , value: '12:00' },
        { key: '12.5', value: '12:30' },
        { key: '13.0'  , value: '13:00' },
        { key: '13.5', value: '13:30' },
        { key: '14.0'  , value: '14:00' },
        { key: '14.5', value: '14:30' },
        { key: '15.0'  , value: '15:00' },
        { key: '15.5', value: '15:30' },
        { key: '16.0'  , value: '16:00' },
        { key: '16.5', value: '16:30' },
        { key: '17.0'  , value: '17:00' },
        { key: '17.5', value: '17:30' },
        { key: '18.0'  , value: '18:00' },
        { key: '18.5', value: '18:30' },
        { key: '19.0'  , value: '19:00' },
        { key: '19.5', value: '19:30' },
        { key: '20.0'  , value: '20:00' },
        { key: '20.5', value: '20:30' },
        { key: '21.0'  , value: '21:00' },
        { key: '21.5', value: '21:30' },
        { key: '22.0'  , value: '22:00' },
        { key: '22.5', value: '22:30' },
        { key: '23.0'  , value: '23:00' },
        { key: '23.5', value: '23:30' },
        { key: '24.0'  , value: '24:00' }
    ];
    
    $scope.fullTime = function(perDiem) {
        perDiem.timeFrom = '0.0';
        perDiem.timeTo = '24.0';
        
        $scope.calcPerDiem(perDiem);
    };

    $scope.removePerDiem = function(perDiem) {
        perDiem.projectId = '';
        perDiem.travelExpenseRateId = '';
        perDiem.timeFrom = '';
        perDiem.timeTo = '';
        perDiem.charges = '';
        calcSum();
    };
    $scope.copyPerDiem = function(targetPerDiem, sourcePerDiem) {
        targetPerDiem.projectId = sourcePerDiem.projectId;
        targetPerDiem.travelExpenseRateId = sourcePerDiem.travelExpenseRateId;
        targetPerDiem.timeFrom = sourcePerDiem.timeFrom;
        targetPerDiem.timeTo = sourcePerDiem.timeTo;
        targetPerDiem.charges = sourcePerDiem.charges;
        calcSum();
    };

    $scope.calcPerDiem = function(perDiem) {
        perDiem.charges = '';
        var terId = perDiem.travelExpenseRateId;
        
        if (perDiem.timeFrom === '' || perDiem.timeTo === '' || perDiem.travelExpenseRateId === '') 
            return;
        
        var duration = parseFloat( perDiem.timeTo ) - parseFloat( perDiem.timeFrom );
        
        if (duration === 24) 
            perDiem.charges = $scope.travelExpensesRatesById[terId].rate24h;
        else if (duration > 8)
            perDiem.charges = $scope.travelExpensesRatesById[terId].rateFrom8To24;
        else
            perDiem.charges = 0;
    
        calcSum();
    };
    var calcSum = function() {
        var sum = 0;
        angular.forEach($scope.perDiemsData.perDiemList, function(value, key) {
            if (angular.isNumber(value.charges))
                sum += value.charges;
        });
        $scope.perDiemsData.sum = sum;
    };
    
    $scope.sendToServer = function() {
        console.log('->sendToServer');
        console.log($scope.perDiemsData);
        
        $http.put(serviceBaseUrl + 'per-diem/' + $rootScope.user.id, $scope.perDiemsData).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            $scope.perDiemsData = data;
            calcSum();
        });
    };
}

