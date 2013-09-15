
function IndividualsCtrl($scope, $http) {
    $('.panel-heading').css('background-color', 'mediumvioletred');
    $('.panel-heading').css('color', 'white');

    $http.get(serviceBaseUrl + 'resources/individuals').success(function(data) {
        console.log(data);
        $scope.individuals = data;
    });
    $scope.selectIndividual = function(individual) {
        $scope.currentIndividual = individual;
    };
    $scope.sendToServer = function() {
        console.log($scope.currentIndividual);
        $http.put(serviceBaseUrl + 'resources/individuals', $scope.currentIndividual).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            alert('info auf dem server...');
        });
    };
}