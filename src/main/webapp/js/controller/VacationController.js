'use strict';


function VacationCtrl($scope, $http, $routeParams, $location,  msgbox) {
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
        var params = {};
        console.log( $location.search().year );
        if ($location.search().year !== undefined)
            params.year = $location.search().year;
        console.log( params);
        $http.get(serviceURL,{params: params} ).success(function(data) {
            console.log(data);
            //convert date

            $scope.vacations = data;
            $scope.vacationYear = $scope.vacations.vacationYear;
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
    
    $scope.changeYear = function() {        
        $location.search('year', $scope.vacationYear);
    };
    
    $scope.showHideComment = function(index) {
        console.log($scope.showComment);
        console.log(index);
        if ($scope.showComment === index)
            $scope.showComment = -1;
        else
            $scope.showComment = index;        
    };
    $scope.removeVacationRecord = function(vacationRecord) {
        
        var title = 'Remove vacation request';
        var message = 'Should vacation request removed? (from ' + vacationRecord.vacationFrom + ')';
        
        
        msgbox.open({title: title, message: message}, function() {
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
            $scope.vacation.calculateVacationDays = data.vacationDays;
            $scope.vacation.numberOfDays = data.vacationDays;
            $scope.vacation.legalHolidays = data.legalHolidays;
        });
    };
    $scope.issueRequestForTimeOff = function() {
        console.log($scope.requestForm.$valid);
        if (!$scope.requestForm.$valid)
            return;
        $scope.vacation.individualId = $routeParams.individualId;
        $http.post(serviceURL, $scope.vacation)
                .success(function(data) {
            refresh();
            $scope.vacation.vacationFrom = null;
            $scope.vacation.vacationUntil = null;
            $scope.vacation.calculateVacationDays = null;
            $scope.vacation.numberOfDays = null;
            $('#vacationFromUntil').val('');
      
            var title = 'Request for Time Off';
            var message = 'Your request for time off was submitted';
        
            msgbox.open({title: title, message: message, hideCancelBtn: true} );  
            
        }).error(function(data) {
            console.log(data);
            // called asynchronously if an error occurs
            // or server returns response with status
            // code outside of the <200, 400) range
        });
    };
    
    $scope.excelDownload = function() {
        
    }
}
