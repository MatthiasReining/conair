'use strict';

function CashDisbursementSelectorCtrl($scope) {
    $('.panel-heading').css('background-color', 'olive');
    $('.panel-heading').css('color', 'white');

    $scope.cashDisbursementYear = '' + new Date().getFullYear();

}
;

function CashDisbursementCtrl($scope, $routeParams, $http, $rootScope, msgbox) {

    $('.panel-heading').css('background-color', 'olive');
    $('.panel-heading').css('color', 'white');

    $scope.passt = false;

    var serviceURL = serviceBaseUrl + 'cash-disbursement/' + $rootScope.user.id + '/' + $routeParams.yearMonth;
    $http.get(serviceURL).success(function(data) {
        
         
        $scope.dateSelector = [{'date':'2014-04-01'}, {'date': '2014-04-02'}];
        $scope.cashDisbursementData = data;
        $scope.addRows(4);
       
        
        $scope.calcSum();
    });

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
}
