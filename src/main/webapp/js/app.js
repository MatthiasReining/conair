'use strict';


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


angular.module('pbtr', []).
        config(['$routeProvider', function($routeProvider) {
        $routeProvider.
                when('/projects', {templateUrl: 'snippets/project-list.html', controller: ProjectListCtrl}).
                when('/projects/:projectKey', {templateUrl: 'snippets/project.html', controller: ProjectCtrl}).
                when('/travel-costs-list', {templateUrl: 'snippets/travel-costs-list.html', controller: TravelCostsListCtrl}).
                when('/per-diems-selector', {templateUrl: 'snippets/per-diems-selector.html', controller: PerDiemsSelectorCtrl}).
                when('/per-diems/:yearMonth', {templateUrl: 'snippets/per-diems.html', controller: PerDiemsCtrl}).
                when('/travel-costs', {templateUrl: 'snippets/travel-costs.html', controller: TravelCostsCtrl}).
                otherwise({redirectTo: '/projects'});

        
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