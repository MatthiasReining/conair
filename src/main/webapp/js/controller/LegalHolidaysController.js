
function LegalHolidaysCtrl($scope, $http) {
    $('.panel-heading').css('background-color', 'mediumvioletred');
    $('.panel-heading').css('color', 'white');

    $http.get(serviceBaseUrl + 'resources/legal-holidays').success(function(data) {
        $scope.legalHolidays = data;
    });
   
}