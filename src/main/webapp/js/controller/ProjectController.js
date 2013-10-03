
function ProjectListCtrl($scope, $http) {
    $('.panel-heading').css('background-color', 'orange');
    $('.panel-heading').css('color', 'white');

    $http.get(serviceBaseUrl + 'projects').success(function(data) {
        console.log(data);
        $scope.projects = data;
    });
}

function ProjectCtrl($scope, $routeParams, $http) {
    $('.panel-heading').css('background-color', 'orange');
    $('.panel-heading').css('color', 'white');


    var serviceURL = serviceBaseUrl + 'projects/' + $routeParams.projectKey;
    console.log('in ProjectCtrl');
    //init click events
    /**
        $('#project-start-area').datetimepicker().on('changeDate', function(e) {
        console.log(e.date);
        datepicker2model(e, $scope);
    });
    $('#project-end-area').datetimepicker().on('changeDate', function(e) {
        datepicker2model(e, $scope);
    });
    */

    if ($routeParams.projectKey == 'new') {
        $scope.project = {};
        $scope.project.projectKey = 'blub';
    } else {
        $http.get(serviceURL, { cache: false}).success(function(data) {
            console.log(data);
            //convert date
            data.projectStart = new Date(data.projectStart).getText();
            data.projectEnd = new Date(data.projectEnd).getText();
            $scope.project = data;
        });
    }
    
    $scope.newWP = function() {
        if (! angular.isArray ( $scope.project.workPackages ) ) $scope.project.workPackages = [];
        var newWP = {
            'wpName': '<new work packages>'
        };
        $scope.project.workPackages.push( newWP );
        $scope.currentWP = newWP;   
    };
    $scope.removeWP = function(workPackage) {
        console.log(workPackage);
        var i = $scope.project.workPackages.indexOf(workPackage);
        console.log('index: ' + i);
        if (i!==-1) $scope.project.workPackages.splice(i, 1);
        //FIXME remove work package
    };    
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