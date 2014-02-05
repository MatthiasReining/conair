function TaskVacationApprovalListCtrl($scope, $http) {
    $('.panel-heading').css('background-color', 'wheat');
    $('.panel-heading').css('color', 'black');

    $http.get(serviceBaseUrl + 'vacations/tasks').success(function(data) {
        console.log(data);
        $scope.tasks = data;
    });
};

function TaskVacationApprovalCtrl($scope, $routeParams, $http, msgbox) {
    $('.panel-heading').css('background-color', 'wheat');
    $('.panel-heading').css('color', 'black');


    $http.get(serviceBaseUrl + 'vacations/tasks/' + $routeParams.vacationRecordId).success(function(data) {
        console.log(data);
        $scope.vacation = data;
    });
    $scope.rejectRequestForTimeOff = function() {
        $http.put(serviceBaseUrl + 'vacations/tasks/' + $scope.vacation.id + '/reject', $scope.vacation).success(function(data) {
            msgbox.open({title: 'Server feedback', message: 'Request for time off was successfully rejected!', hideCancelBtn: true});
        });
    };
    $scope.approveRequestForTimeOff = function() {
        $http.put(serviceBaseUrl + 'vacations/tasks/' + $scope.vacation.id + '/approve', $scope.vacation).success(function(data) {
            msgbox.open({title: 'Server feedback', message: 'Request for time off was successfully approved!', hideCancelBtn: true});
        });
    };
};

