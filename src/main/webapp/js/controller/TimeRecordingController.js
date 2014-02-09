'use strict';

function TimeRecordingCtrl($scope, $http, $routeParams) {
    $('.panel-heading').css('background-color', 'lightblue');
    $('.panel-heading').css('color', 'white');

    var weeks = $routeParams.weeks;
    var qStart = $routeParams.qStart;
    var qEnd = $routeParams.qEnd;

    if (angular.isUndefined(weeks))
        weeks = '2';

    $('#showTimeRecordFromUntil').daterangepicker({
        showWeekNumbers: true,
        format: 'dd, L',
        separator: '    -    ',
        locale: {firstDay: 1}
    }, function(start, end) {
        $scope.displayRangeFrom = start.format('YYYY-MM-DD');
        $scope.displayRangeUntil = end.format('YYYY-MM-DD');
    });

    $http.get(serviceBaseUrl + 'projects').success(function(data) {
        console.log(data);
        $scope.projects = data;
    });


    $scope.showWeeks = function(weeks) {
        $http.get(serviceBaseUrl + 'time-recording/range/' + weeks).success(function(data) {
            showData(data);
        });
    };
    $scope.showRange = function() {
        $http.get(serviceBaseUrl + 'time-recording/range?qStart=' + $scope.displayRangeFrom + '&qEnd=' + $scope.displayRangeUntil).success(function(data) {
            showData(data);
        });
    };

    function showData(data) {
        console.log(data);
        $scope.timeRecording = data;
        console.log($scope.timeRecording.workingDayRange);

        $scope.workingDaySum = {};
        angular.copy($scope.timeRecording.workingDayRange, $scope.workingDaySum);
        console.log($scope.workingDaySum);
        $scope.calculateWorkingDaySum();
    }

    if (angular.isUndefined(qStart)) {
        console.log("do a week query");
        $scope.showWeeks(weeks);        
    } else {
        //use range query
        console.log("do a range query");
        $scope.displayRangeFrom = qStart;
        $scope.displayRangeUntil = qEnd;
        $scope.showRange();
    }
    
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
            console.log(data);
            //$scope.project = data;
        });
    };
}
