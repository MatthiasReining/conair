'use strict';

function TimeRecordingCtrl($scope, $http, $routeParams) {
    $('.panel-heading').css('background-color', 'lightblue');
    $('.panel-heading').css('color', 'white');
    
    var weeks = $routeParams.weeks;
    if (angular.isUndefined(weeks))
        weeks = '2';
    
    $http.get(serviceBaseUrl + 'projects').success(function(data) {
        console.log(data);
        $scope.projects = data;
    });
    $http.get(serviceBaseUrl + 'time-recording/range/' + weeks).success(function(data) {
        console.log(data);
        $scope.timeRecording = data;
    });
    $scope.addRow = function() {
        console.log('->addRow');
        $scope.timeRecording.workingHours.push({
            "days": {}
        });
    };
    $scope.sendToServer = function() {
        console.log('->sendToServer');
        console.log($scope.timeRecording);
        $http.put(serviceBaseUrl + "time-recording", $scope.timeRecording).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            $scope.project = data;
        });
    };
}
