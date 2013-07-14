'use strict';

var user;

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
    var month = parseInt(dateArray[1])-1;
    var day = parseInt(dateArray[2]);
    return new Date( year, month, day);
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


var app = angular.module('pbtr',  ['ui.bootstrap']);

app.config(['$routeProvider', function($routeProvider) {

        $routeProvider.
                when('/projects', {templateUrl: 'snippets/project-list.html', controller: ProjectListCtrl}).
                when('/projects/:projectKey', {templateUrl: 'snippets/project.html', controller: ProjectCtrl}).
                when('/time-recording', {templateUrl: 'snippets/time-recording.html', controller: TimeRecordingCtrl}).
                when('/time-recording/weeks/:weeks', {templateUrl: 'snippets/time-recording.html', controller: TimeRecordingCtrl}).
                when('/vacation', {templateUrl: 'snippets/vacation.html', controller: VacationCtrl}).
                when('/travel-costs-list', {templateUrl: 'snippets/travel-costs-list.html', controller: TravelCostsListCtrl}).
                when('/per-diems-selector', {templateUrl: 'snippets/per-diems-selector.html', controller: PerDiemsSelectorCtrl}).
                when('/per-diems/:yearMonth', {templateUrl: 'snippets/per-diems.html', controller: PerDiemsCtrl}).
                when('/travel-costs', {templateUrl: 'snippets/travel-costs.html', controller: TravelCostsCtrl}).
                otherwise({redirectTo: '/projects'});
    }]);

app.run(['$http', '$rootScope', function($http, $rootScope) {
        console.log('in run');
        $rootScope.user = user;
    }]);

var selectCurrentNavi = function(page) {
    $('.navi li').removeClass('current');
    $.each($('.navi li'), function(index, key) {
        var href = $(key).find('a').attr('href');
        if (href.indexOf('#/' + page) > -1) {
            $(key).addClass('current');
        }
    });
};


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
        user = data;
        angular.bootstrap(document, ['pbtr']);
    }).error(function(data, status) {
        console.log("auth error.. " + status);
        window.location = "login.html";
    });


});
    