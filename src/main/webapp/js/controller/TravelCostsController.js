
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