
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
    
    
    $http.get(serviceBaseUrl + 'resources/individuals').success(function(data) {
        console.log(data);
        $scope.individuals = data;
    });


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
    } else {
        $http.get(serviceURL, {cache: false}).success(function(data) {
            console.log(data);
            
            $scope.project = data;
            //init datepicker
            $('#projectStart').datepicker('update', $scope.project.projectStart);
            $('#projectEnd').datepicker('update', $scope.project.projectEnd);
        });
    }
    $('#projectStart').datepicker({
        format: "yyyy-mm-dd",
        language: "de"
    }).on('changeDate', function(e) {
        $scope.project.projectStart = e.format('yyyy-mm-dd');
    });
    $('#projectEnd').datepicker({
        format: "yyyy-mm-dd",
        language: "de"
    }).on('changeDate', function(e) {
        $scope.project.projectEnd = e.format('yyyy-mm-dd');
    });

    $scope.newWP = function() {
        if (!angular.isArray($scope.project.workPackages))
            $scope.project.workPackages = [];
        var newWP = {
            'wpName': '<new work packages>'
        };
        $scope.project.workPackages.push(newWP);
        $scope.currentWP = newWP;
    };
    $scope.removeWP = function(workPackage) {
        console.log(workPackage);
        var i = $scope.project.workPackages.indexOf(workPackage);
        console.log('index: ' + i);
        if (i !== -1)
            $scope.project.workPackages.splice(i, 1);
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