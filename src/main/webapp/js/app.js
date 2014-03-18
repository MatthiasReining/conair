'use strict';

var user;
var version;

String.prototype.right = function(length) {
    var str = this;
    var pos = (length > str.length) ? 0 : (str.length - length);
    return str.substring(pos);
};

Date.prototype.getText = function() {
    var year = '' + (1900 + this.getYear());
    var month = ('0' + (this.getMonth() + 1)).right(2);
    var day = ('0' + (this.getDate())).right(2);
    return year + '-' + month + '-' + day;
};

String.prototype.convert2Date = function() {
    var dateArray = this.valueOf().split('-');
    var year = parseInt(dateArray[0]);
    var month = parseInt(dateArray[1]) - 1;
    var day = parseInt(dateArray[2]);
    return new Date(year, month, day);
};

function datepicker2model(e, $scope) {
    var inputObj = $(e.target).find('input');
    var modelPath = inputObj.attr('ng-model');
    if (modelPath === undefined)
        modelPath = inputObj.attr('data-ng-model');
    if (modelPath === undefined)
        return;
    eval('$scope.' + modelPath + '="' + e.date.getText() + '";');
}
;


var app = angular.module('conair', ['ngRoute', 'ui.bootstrap']);

app.service('msgbox', ['$modal', MsgBoxService]);
  
app.config(['$routeProvider', function($routeProvider) {

        $routeProvider.
                when('/resources/individuals', {templateUrl: 'snippets/individuals.html', controller: IndividualsCtrl}).
                when('/resources/legal-holidays', {templateUrl: 'snippets/legal-holidays.html', controller: LegalHolidaysCtrl}).
                when('/projects', {templateUrl: 'snippets/project-list.html', controller: ProjectListCtrl}).
                when('/projects/:projectKey', {templateUrl: 'snippets/project.html', controller: ProjectCtrl}).
                when('/accounting/:projectKey', {templateUrl: 'snippets/accounting-list.html', controller: AccountingListCtrl}).
                when('/accounting/:projectKey/periods/:accountingPeriodId', {templateUrl: 'snippets/accounting-period.html', controller: AccountingCtrl}).
                when('/time-recording', {templateUrl: 'snippets/time-recording.html', controller: TimeRecordingCtrl}).
                when('/time-recording/weeks/:weeks', {templateUrl: 'snippets/time-recording.html', controller: TimeRecordingCtrl}).
                when('/time-recording/range', {templateUrl: 'snippets/time-recording.html', controller: TimeRecordingCtrl}).
                when('/vacations/:individualId', {templateUrl: 'snippets/vacation.html', controller: VacationCtrl}).
                when('/vacation-manager', {templateUrl: 'snippets/vacation-manager.html', controller: VacationManagerCtrl}).
                when('/travel-costs-list', {templateUrl: 'snippets/travel-costs-list.html', controller: TravelCostsListCtrl}).
                when('/per-diems-selector', {templateUrl: 'snippets/per-diems-selector.html', controller: PerDiemsSelectorCtrl}).
                when('/per-diems/:yearMonth', {templateUrl: 'snippets/per-diems.html', controller: PerDiemsCtrl}).
                when('/travel-costs', {templateUrl: 'snippets/travel-costs.html', controller: TravelCostsCtrl}).
                when('/tasks-vacation-approval-list', {templateUrl: 'snippets/tasks-vacation-approval-list.html', controller: TaskVacationApprovalListCtrl}).
                when('/tasks-vacation-approval/:vacationRecordId', {templateUrl: 'snippets/tasks-vacation-approval.html', controller: TaskVacationApprovalCtrl}).
                otherwise({redirectTo: '/projects'});
    }]);

app.run(['$http', '$rootScope', function($http, $rootScope) {
        console.log('in run');
        $rootScope.user = user;
        $rootScope.version = version;
    }]);


/**
 * Bootstraping
 * @returns {undefined}
 */
// Handler for .ready() called.
angular.element(document).ready(function() {
    console.log("start..");
    $.get(serviceBaseUrl + 'auth', {
        'cache': false
    }).done(function(data) {
        console.log("auth done..");
        user = data.user;
        version = data.version;
        
        angular.bootstrap(document, ['conair']);
    }).error(function(data, status) {

        console.log("auth error.. " + status);
        console.log(data);

        window.location = "login.html";
    });
});

moment.lang('de');
