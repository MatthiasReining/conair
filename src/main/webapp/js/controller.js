'use strict';

var serviceBaseUrl = 'http://localhost:8080/project-business-time-recording/rest/';

function ProjectListCtrl($scope, $http) {
    $http.get(serviceBaseUrl + 'projects/list').success(function(data) {
        console.log(data);
        $scope.projects = data;
    });
}

function ProjectCtrl($scope, $routeParams, $http) {
    var serviceURL = serviceBaseUrl + 'projects/v0.1/' + $routeParams.projectKey;

    console.log('in ProjectCtrl');

    //init click events
    $('#project-start-area').datetimepicker().on('changeDate', function(e) {
        console.log(e.date);
        datepicker2model(e, $scope);
    });
    $('#project-end-area').datetimepicker().on('changeDate', function(e) {
        datepicker2model(e, $scope);
    });

    $http.get(serviceURL).success(function(data) {
        console.log(data);
        //convert date
        data.projectStart = new Date(data.projectStart).getText();
        data.projectEnd = new Date(data.projectEnd).getText();
        $scope.project = data;
    });
    
    $scope.selectWP = function(workPackage) {
        console.log(workPackage);
        $scope.currentWP = workPackage;
    };

    $scope.sendToServer = function() {
        console.log('->sendToServer');
        console.log($scope.project);

        $http.put(serviceURL, $scope.project).success(function(data) {
            console.log('<--fromServer');        
            console.log(data);
            $scope.project = data;
        });
    };
}