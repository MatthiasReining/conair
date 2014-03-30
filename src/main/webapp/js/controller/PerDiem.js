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
        { key: '0'  , value: '00:00' },
        { key: '0.5', value: '00:30' },
        { key: '1'  , value: '01:00' },
        { key: '1.5', value: '01:30' },
        { key: '2'  , value: '02:00' },
        { key: '2.5', value: '02:30' },
        { key: '3'  , value: '03:00' },
        { key: '3.5', value: '03:30' },
        { key: '4'  , value: '04:00' },
        { key: '4.5', value: '04:30' },
        { key: '5'  , value: '05:00' },
        { key: '5.5', value: '05:30' },
        { key: '6'  , value: '06:00' },
        { key: '6.5', value: '06:30' },
        { key: '7'  , value: '07:00' },
        { key: '7.5', value: '07:30' },
        { key: '8'  , value: '08:00' },
        { key: '8.5', value: '08:30' },
        { key: '9'  , value: '09:00' },
        { key: '9.5', value: '09:30' },
        { key: '10'  , value: '10:00' },
        { key: '10.5', value: '10:30' },
        { key: '11'  , value: '11:00' },
        { key: '11.5', value: '11:30' },
        { key: '12'  , value: '12:00' },
        { key: '12.5', value: '12:30' },
        { key: '13'  , value: '13:00' },
        { key: '13.5', value: '13:30' },
        { key: '14'  , value: '14:00' },
        { key: '14.5', value: '14:30' },
        { key: '15'  , value: '15:00' },
        { key: '15.5', value: '15:30' },
        { key: '16'  , value: '16:00' },
        { key: '16.5', value: '16:30' },
        { key: '17'  , value: '17:00' },
        { key: '17.5', value: '17:30' },
        { key: '18'  , value: '18:00' },
        { key: '18.5', value: '18:30' },
        { key: '19'  , value: '19:00' },
        { key: '19.5', value: '19:30' },
        { key: '20'  , value: '20:00' },
        { key: '20.5', value: '20:30' },
        { key: '21'  , value: '21:00' },
        { key: '21.5', value: '21:30' },
        { key: '22'  , value: '22:00' },
        { key: '22.5', value: '22:30' },
        { key: '23'  , value: '23:00' },
        { key: '23.5', value: '23:30' },
        { key: '24'  , value: '24:00' }
    ];
    
    $scope.fullTime = function(perDiem) {
        perDiem.inServiceFrom = '0';
        perDiem.inServiceTo = '24';
        
        $scope.calcPerDiem(perDiem);
    };

    $scope.removePerDiem = function(perDiem) {
        perDiem.projectId = '';
        perDiem.travelExpensesRateId = '';
        perDiem.inServiceFrom = '';
        perDiem.inServiceTo = '';
        perDiem.charges = '';
        calcSum();
    };
    $scope.copyPerDiem = function(targetPerDiem, sourcePerDiem) {
        targetPerDiem.projectId = sourcePerDiem.projectId;
        targetPerDiem.travelExpensesRateId = sourcePerDiem.travelExpensesRateId;
        targetPerDiem.inServiceFrom = sourcePerDiem.inServiceFrom;
        targetPerDiem.inServiceTo = sourcePerDiem.inServiceTo;
        targetPerDiem.charges = sourcePerDiem.charges;
        calcSum();
    };

    $scope.calcPerDiem = function(perDiem) {
        perDiem.charges = '';
        var terId = perDiem.travelExpensesRateId;
        
        if (perDiem.inServiceFrom === '' || perDiem.inServiceTo === '' || perDiem.travelExpensesRateId === '') 
            return;
        
        var duration = parseFloat( perDiem.inServiceTo ) - parseFloat( perDiem.inServiceFrom );
        
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

