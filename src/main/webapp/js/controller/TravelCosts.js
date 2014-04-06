'use strict';

function TravelCostsSelectorCtrl($scope) {
    $('.panel-heading').css('background-color', 'green');
    $('.panel-heading').css('color', 'white');
    
    $scope.travelCostsYear = '' + new Date().getFullYear();

};

function TravelCostsCtrl($scope, $routeParams, $http, $rootScope, msgbox) {
    $('.panel-heading').css('background-color', 'green');
    $('.panel-heading').css('color', 'white');
    
    
    var serviceURL = serviceBaseUrl + 'travel-costs/' + $rootScope.user.id + '/' + $routeParams.yearMonth;

    //load destination list
    //FIXME cache
    //FIXME hard coded travel year
    $scope.travelExpensesRatesById = {};
    $http.get(serviceBaseUrl + 'travel-costs/travel-expenses-rates/2014').success(function(data) {
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
                $scope.travelCostsData = data;
                calcSum();
            });
        });

    });

    $scope.timeSelection = [
        {key: '0.0', value: '00:00'},
        {key: '0.5', value: '00:30'},
        {key: '1.0', value: '01:00'},
        {key: '1.5', value: '01:30'},
        {key: '2.0', value: '02:00'},
        {key: '2.5', value: '02:30'},
        {key: '3.0', value: '03:00'},
        {key: '3.5', value: '03:30'},
        {key: '4.0', value: '04:00'},
        {key: '4.5', value: '04:30'},
        {key: '5.0', value: '05:00'},
        {key: '5.5', value: '05:30'},
        {key: '6.0', value: '06:00'},
        {key: '6.5', value: '06:30'},
        {key: '7.0', value: '07:00'},
        {key: '7.5', value: '07:30'},
        {key: '8.0', value: '08:00'},
        {key: '8.5', value: '08:30'},
        {key: '9.0', value: '09:00'},
        {key: '9.5', value: '09:30'},
        {key: '10.0', value: '10:00'},
        {key: '10.5', value: '10:30'},
        {key: '11.0', value: '11:00'},
        {key: '11.5', value: '11:30'},
        {key: '12.0', value: '12:00'},
        {key: '12.5', value: '12:30'},
        {key: '13.0', value: '13:00'},
        {key: '13.5', value: '13:30'},
        {key: '14.0', value: '14:00'},
        {key: '14.5', value: '14:30'},
        {key: '15.0', value: '15:00'},
        {key: '15.5', value: '15:30'},
        {key: '16.0', value: '16:00'},
        {key: '16.5', value: '16:30'},
        {key: '17.0', value: '17:00'},
        {key: '17.5', value: '17:30'},
        {key: '18.0', value: '18:00'},
        {key: '18.5', value: '18:30'},
        {key: '19.0', value: '19:00'},
        {key: '19.5', value: '19:30'},
        {key: '20.0', value: '20:00'},
        {key: '20.5', value: '20:30'},
        {key: '21.0', value: '21:00'},
        {key: '21.5', value: '21:30'},
        {key: '22.0', value: '22:00'},
        {key: '22.5', value: '22:30'},
        {key: '23.0', value: '23:00'},
        {key: '23.5', value: '23:30'},
        {key: '24.0', value: '24:00'}
    ];

    $scope.fullTime = function(travelCosts) {
        travelCosts.timeFrom = '0.0';
        travelCosts.timeTo = '24.0';

        $scope.calcTravelCosts(travelCosts);
    };

    $scope.removeTravelCosts = function(travelCosts) {
        travelCosts.projectId = null;
        travelCosts.travelExpenseRateId = null;
        travelCosts.timeFrom = null;
        travelCosts.timeTo = null;
        travelCosts.breakfast = null;
        travelCosts.lunch = null;
        travelCosts.dinner = null;
        travelCosts.charges = '';        
        calcSum();
    };
    $scope.copyTravelCosts = function(targetTravelCosts, sourceTravelCosts) {
        targetTravelCosts.projectId = sourceTravelCosts.projectId;
        targetTravelCosts.travelExpenseRateId = sourceTravelCosts.travelExpenseRateId;
        targetTravelCosts.timeFrom = sourceTravelCosts.timeFrom;
        targetTravelCosts.timeTo = sourceTravelCosts.timeTo;
        targetTravelCosts.breakfast = sourceTravelCosts.breakfast;
        targetTravelCosts.lunch = sourceTravelCosts.lunch;
        targetTravelCosts.dinner = sourceTravelCosts.dinner;
        targetTravelCosts.charges = sourceTravelCosts.charges;
        calcSum();
    };

    $scope.calcTravelCosts = function(travelCosts) {
        travelCosts.charges = '';
        var terId = travelCosts.travelExpenseRateId;

        if (travelCosts.timeFrom === null || travelCosts.timeTo === null || travelCosts.travelExpenseRateId === null)
            return;

        var duration = parseFloat(travelCosts.timeTo) - parseFloat(travelCosts.timeFrom);

        var food =(travelCosts.breakfast ? $scope.travelExpensesRatesById[terId].breakfast : 0);
        food += (travelCosts.lunch ? $scope.travelExpensesRatesById[terId].lunch : 0);
        food += (travelCosts.dinner ? $scope.travelExpensesRatesById[terId].dinner : 0);

        if (duration === 24)
            travelCosts.charges = $scope.travelExpensesRatesById[terId].rate24h;
        else if (duration > 8)
            travelCosts.charges = $scope.travelExpensesRatesById[terId].rateFrom8To24;
        else
            travelCosts.charges = 0;

        travelCosts.charges = travelCosts.charges - food;
        if (travelCosts.charges < 0)
            travelCosts.charges = 0;

        calcSum();
    };
    var calcSum = function() {
        var sum = 0;
        angular.forEach($scope.travelCostsData.travelCostsList, function(value, key) {
            if (angular.isNumber(value.charges))
                sum += value.charges;
        });
        $scope.travelCostsData.sum = sum;
    };

    $scope.sendToServer = function() {
        console.log('->sendToServer');
        console.log($scope.travelCostsData);

        $http.put(serviceBaseUrl + 'travel-costs/' + $rootScope.user.id, $scope.travelCostsData).success(function(data) {           
            msgbox.open({title: 'Server feedback', message: 'Travel costs sucessfully saved!', hideCancelBtn: true});        
            $scope.travelCostsData = data;
            calcSum();
        });
    };
}

