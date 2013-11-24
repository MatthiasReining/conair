
function AccountingListCtrl($scope, $routeParams, $http) {
    $('.panel-heading').css('background-color', 'orange');
    $('.panel-heading').css('color', 'white');

    //init
    $http.get(serviceBaseUrl + 'projects/' + $routeParams.projectKey).success(function(data) {
        console.log(data);
        $scope.project = data;
    });
    
    $http.get(serviceBaseUrl + 'accounting/' + $routeParams.projectKey).success(function(data) {
        console.log(data);
        $scope.accountingPeriods = data;
    });
    
    
    
    


}
