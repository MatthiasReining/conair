'use strict';

function CashDisbursementSelectorCtrl($scope) {
    $('.panel-heading').css('background-color', 'olive');
    $('.panel-heading').css('color', 'white');

    $scope.cashDisbursementYear = '' + new Date().getFullYear();

};

function CashDisbursementCtrl($scope, $routeParams, $http, $rootScope, msgbox) {

    $('.panel-heading').css('background-color', 'olive');
    $('.panel-heading').css('color', 'white');

    var serviceURL = serviceBaseUrl + 'cash-disbursement/' + $rootScope.user.id + '/' + $routeParams.yearMonth;
    $http.get(serviceURL).success(function(data) {
         
        $scope.dateSelector = buildDateSelector(data.yearMonth);
        $scope.cashDisbursementData = data;
        $scope.addRows(4);
        
        $scope.calcSum();
    });
    
    var buildDateSelector = function(yearMonth) {
        var result = [];
        var year = parseInt(yearMonth.split("-")[0]);
        var month = parseInt(yearMonth.split("-")[1]);
        
        var firstDate = new Date(year, month-1, 1);
        for(var i=1; i<=31; i++) {
            var currentDate = new Date(year, month-1, i);
            if (currentDate.getMonth() !== firstDate.getMonth() )
                break;

            var dayText = String( currentDate.getDate() );
            if (dayText.length === 1) dayText = '0' + dayText;
            
            result.push( {'date': yearMonth+'-'+dayText, 'dateObj': currentDate});            
        }
        return result;
        
    };

    $scope.remove = function(index) {
        $scope.cashDisbursementData.cashDisbursementList.splice(index, 1);
        $scope.calcSum();
    };
    
    $scope.addRows = function(numberOfRows) {
        for(var i=0; i<numberOfRows; i++) {
            $scope.cashDisbursementData.cashDisbursementList.push({});        
        }
    };
   

    $scope.calcSum = function() {
        var sum = 0;
        angular.forEach($scope.cashDisbursementData.cashDisbursementList, function(value, key) {
            if (angular.isNumber(value.amount))
                sum += value.amount;
        });
        $scope.cashDisbursementData.sum = sum;
        
    };
    
    $scope.sendToServer = function() {
        console.log('->sendToServer');
        console.log($scope.cashDisbursementData);

        $http.put(serviceBaseUrl + 'cash-disbursement/' + $rootScope.user.id, $scope.cashDisbursementData).success(function(data) {
            msgbox.open({title: 'Server feedback', message: 'Cash disbursement sucessfully saved!', hideCancelBtn: true});
            $scope.cashDisbursementData = data;
            $scope.calcSum();
        });
    };
};
