
function IndividualsCtrl($scope, $http, msgbox) {
    $('.panel-heading').css('background-color', 'mediumvioletred');
    $('.panel-heading').css('color', 'white');

    $http.get(serviceBaseUrl + 'resources/individuals').success(function(data) {
        console.log(data);
        $scope.individuals = data;
    });
    
    
    $scope.selectIndividual = function(individual) {
        $scope.currentIndividual = individual;
    };
    $scope.addNewIndividual = function() {
        var newIndividual = {
            linkedInId: "set-linkedIn-Id",
            id: -1
        };
        $scope.individuals.push(newIndividual);
        $scope.currentIndividual = newIndividual;
    };
    $scope.sendToServer = function() {
        console.log($scope.currentIndividual);
        $http.put(serviceBaseUrl + 'resources/individuals', $scope.currentIndividual).success(function(data) {
            msgbox.open({title: 'Server feedback', message: 'Modifcations sucessfully saved!', hideCancelBtn: true});
        });
    };
}