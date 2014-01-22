'use strict';

function VacationManagerCtrl($scope, $routeParams, $http, $location) {
    $('.panel-heading').css('background-color', 'mediumvioletred');
    $('.panel-heading').css('color', 'white');

    var params = {};
    if ($location.search().year !== undefined)
        params.year = $location.search().year;
        
    $http.get(serviceBaseUrl + 'vacations', {params: params}).success(function(data) {
        console.log(data);
        $scope.vacations = data;
        $scope.vacationYear = $scope.vacations[0].vacationYear;
        var progess100percent = 45; //totalDays + residualLeaveYearBefore, means ca. 28 + puffer

        angular.forEach($scope.vacations, function(value) {

            var vacationDaysPerYear = value.numberOfVacationDays;
            var residualLeaveYearBefore = value.residualLeaveYearBefore;
            var approvedVacationDays = value.approvedVacationDays;
            var vacationDaysPerYearProgress = Math.floor(vacationDaysPerYear / progess100percent * 100);
            //this year
            var thisYearApproved = Math.min(vacationDaysPerYear, approvedVacationDays);
            var thisYearApprovedProgress = Math.floor(thisYearApproved / progess100percent * 100);
            var thisYearResidualLeave = Math.min((vacationDaysPerYear - approvedVacationDays), vacationDaysPerYear);
            var thisYearResidualLeaveProgress = vacationDaysPerYearProgress - thisYearApprovedProgress;
            //appendix
            var appendixApproved = Math.max((approvedVacationDays - vacationDaysPerYear), 0);
            var appendixApprovedProgress = Math.floor(appendixApproved / progess100percent * 100);
            var appendixResidualLeave = (residualLeaveYearBefore) - appendixApproved;
            var appendixResidualLeaveProgress = Math.floor(appendixResidualLeave / progess100percent * 100);
            value.progress = [
                {"type": "this-year-approved", "typeText": "approved", "value": thisYearApproved, "percent": thisYearApprovedProgress, "color": "green"},
                {"type": "this-year-residual-leave", "typeText": "residual leave", "value": thisYearResidualLeave, "percent": thisYearResidualLeaveProgress, "color": "red"},
                {"type": "appendix-approved", "typeText": "approved", "value": appendixApproved, "percent": appendixApprovedProgress, "color": "orangered"},
                {"type": "appendix-residual-leave", "typeText": "residual leave (year before)", "value": appendixResidualLeave, "percent": appendixResidualLeaveProgress, "color": "yellowgreen"}
            ];
            console.log('--> ' + vacationDaysPerYear);
            console.log(value.progress);

        });
    });
    
    $scope.changeYear = function() {    
        console.log($location.path());
        console.log($location.search());
        $location.search('year', $scope.vacationYear);
    };

    $scope.changeNumberOfVacationDays = function(vacation) {
        var url = serviceBaseUrl + 'vacations/' + vacation.individualId + '/admin/' + vacation.vacationYear + '/change-number-of-vacation-days';
        console.log(url);
        $http.put(url, vacation).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
        });
    };
}
