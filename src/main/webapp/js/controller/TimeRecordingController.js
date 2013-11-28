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
        console.log($scope.timeRecording.workingDayRange);

        $scope.workingDaySum = {};
        angular.copy($scope.timeRecording.workingDayRange, $scope.workingDaySum);
        console.log($scope.workingDaySum);
        $scope.calculateWorkingDaySum();
    });
    $scope.addRow = function() {
        console.log('->addRow');
        var days = {};
        angular.forEach($scope.workingDaySum, function(value, key) {
            days[key] = {
                "workingTime": null,
                "status": 0
            };
        });
        $scope.timeRecording.workingHours.push({
            "days": days
        });
    };

    $scope.calculateWorkingDaySum = function() {
        //XXX check if a performance optimisation is possible, at the moment everthing is everythime calculated
        var tmpSum = {};
        angular.forEach($scope.workingDaySum, function(value, key) {
            tmpSum[key] = 0.0;
        });

        angular.forEach($scope.timeRecording.workingHours, function(workingHour) {
            angular.forEach($scope.workingDaySum, function(value, key) {
                if (!angular.isUndefined(workingHour.days[key])) {
                    if (angular.isNumber(workingHour.days[key].workingTime)) {
                        tmpSum[key] = tmpSum[key] + workingHour.days[key].workingTime;
                    }
                }
            });
        });

        $scope.workingDaySum = tmpSum;
    };

    $scope.printToolTip = function(show) {
        if (show)
            return 'This working time is already collected!';
        return '';
    };

    $scope.sendToServer = function() {
        console.log('->sendToServer');
        console.log($scope.timeRecording);
        $http.put(serviceBaseUrl + "time-recording", $scope.timeRecording).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            //$scope.project = data;
        });
    };
}
