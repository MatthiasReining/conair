
function AccountingListCtrl($scope, $routeParams, $http) {
    $('.panel-heading').css('background-color', 'orange');
    $('.panel-heading').css('color', 'white');

    //init
    $http.get(serviceBaseUrl + 'projects/' + $routeParams.projectKey).success(function(data) {
        console.log(data);
        $scope.project = data;
    });

    $http.get(serviceBaseUrl + 'accounting/' + $routeParams.projectKey).success(function(data) {
        console.log(data);
        $scope.accountingPeriods = data;
    });

    $scope.newPeriod = {};
    $scope.showNewPeriod = false;
    $('.dpWorkingDay').datepicker({
        language: "de",
        format: "yyyy-mm-dd",
        calendarWeeks: true
    }).on('changeDate', function(e) {
        //update model via scope() plugin!
        var id = e.target.id;
        var element = angular.element($('#' + id));
        var scope = element.scope();
        scope.newPeriod[id] = e.format('yyyy-mm-dd');
    });

    $scope.createNewPeriod = function() {
        console.log($scope.newPeriod);

        $http.post(serviceBaseUrl + 'accounting/' + $routeParams.projectKey, $scope.newPeriod).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            $scope.accountingPeriods = data;
            $scope.showNewPeriod = false;
        });
    };
}



function AccountingCtrl($scope, $routeParams, $http) {
    $('.panel-heading').css('background-color', 'orange');
    $('.panel-heading').css('color', 'white');

    $http.get(serviceBaseUrl + 'resources/individuals').success(function(data) {
        console.log(data);
        $scope.individuals = data;

        //$('#projectStart').datepicker('update', $scope.project.projectStart);
        $('.dpWorkingDay').datepicker({
            language: "de",
            format: "yyyy-mm-dd",
            calendarWeeks: true
        }).on('changeDate', function(e) {
            //update model via scope() plugin!
            var id = e.target.id;
            var element = angular.element($('#' + id));
            var scope = element.scope();
            scope.atd.workingDay = e.format('yyyy-mm-dd');
        });
    });


    //init
    $http.get(serviceBaseUrl + 'accounting/' + $routeParams.projectKey + '/periods/' + $routeParams.accountingPeriodId).success(function(data) {
        console.log(data);
        $scope.ap = data;
    });

    var download = function download(url) {
        var hiddenIFrameID = 'hiddenDownloader';
        iframe = document.getElementById(hiddenIFrameID);
        if (iframe === null) {
            iframe = document.createElement('iframe');
            iframe.id = hiddenIFrameID;
            iframe.style.display = 'none';
            document.body.appendChild(iframe);
        }
        iframe.src = url;
    };

    $scope.updateAccountingPeriod = function() {
        alert('Under construction');
    };

    $scope.createInvoice = function() {
        var downloadURL = serviceBaseUrl + 'accounting/' + $routeParams.projectKey + '/periods/' + $routeParams.accountingPeriodId + '/xls';
        download(downloadURL);
    };
}