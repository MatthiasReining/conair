'use strict';


function VacationCtrl($scope, $http, $routeParams, $modal) {
    $('.panel-heading').css('background-color', 'mediumvioletred');
    $('.panel-heading').css('color', 'white');
    var serviceURL = serviceBaseUrl + 'vacations/' + $routeParams.individualId;
    $scope.stateText = {
        0: 'new',
        1: 'for approval',
        5: 'approved',
        9: 'rejected'
    };
    $scope.vacation = {};
    $('#vacationFromUntil').daterangepicker({
        showWeekNumbers: true,
        format: 'dd, L',
        separator: '    -    ',
        locale: {firstDay: 1}
    },
    function(start, end) {
        $scope.vacation.vacationFrom = start.format('YYYY-MM-DD');
        $scope.vacation.vacationUntil = end.format('YYYY-MM-DD');
    });
    var refresh = function() {
        $http.get(serviceURL).success(function(data) {
            console.log(data);
            //convert date

            $scope.vacations = data;
            var vacationDays = $scope.vacations.vacationDays;
            var vacationDaysByMonth = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
            for (var i = 0; i < vacationDays.length; i++) {
                var d = vacationDays[i].convert2Date();
                vacationDaysByMonth[d.getMonth()] = vacationDaysByMonth[d.getMonth()] + 1;
            }
            $('#vacation-bar-chart').sparkline(vacationDaysByMonth, {type: 'bar', height: '80px', barWidth: 46, barSpacing: 8});
        });
    };
    //initalize load
    refresh();
    $scope.removeVacationRecord = function(vacationRecord) {

        var title = 'Remove vacation request';
        var message = 'Should vacation request removed? (from ' + vacationRecord.vacationFrom + ')';
        MsgBox($modal, {title: title, message: message}, function() {
            console.log('remove record ' + vacationRecord);
            $http.delete(serviceURL + '/' + vacationRecord.id).success(function(data) {
                refresh();
            });
        });
    };
    $scope.calculateVacationDays = function() {
        $http.get(serviceURL + '/calculateVacationDays',
                {params: {'vacationFrom': $scope.vacation.vacationFrom,
                        'vacationUntil': $scope.vacation.vacationUntil
                    }}
        ).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            $scope.vacation.calculateVacationDays = data.vacationDays;
            $scope.vacation.numberOfDays = data.vacationDays;
        });
    };
    $scope.issueRequestForTimeOff = function() {
        console.log($scope.requestForm.$valid);
        if (!$scope.requestForm.$valid)
            return;
        console.log('->sendToServer');
        console.log($scope.vacation);
        $scope.vacation.individualId = $routeParams.individualId;
        $http.post(serviceURL, $scope.vacation)
                .success(function(data) {
            refresh();
            $scope.vacation.vacationFrom = null;
            $scope.vacation.vacationUntil = null;
            $scope.vacation.calculateVacationDays = null;
            $scope.vacation.numberOfDays = null;
            $('#vacationFromUntil').val('');

            MsgBox($modal, {title: 'Request for Time Off', message: 'Your request for time off was submitted', hideCancelBtn: true});
        }).error(function(data) {
            console.log(data);
            // called asynchronously if an error occurs
            // or server returns response with status
            // code outside of the <200, 400) range
        });
    };
    $scope.selectVacationRecord = function(vacationRecord) {
        console.log(vacationRecord);
        $scope.vacation = vacationRecord;
        $scope.calculateVacationDays();
        $scope.vacation = vacationRecord;
    };
}
