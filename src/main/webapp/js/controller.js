'use strict';

var individualId = '4';

var serviceBaseUrl = 'http://localhost:8080/project-business-time-recording/rest/';

function NaviCtrl($scope, $location) {
    $scope.isActive = function(route) {
        return ($location.path().indexOf(route) > -1);
    };
}
;

function ProjectListCtrl($scope, $http) {
    $http.get(serviceBaseUrl + 'projects/list').success(function(data) {
        console.log(data);
        $scope.projects = data;
    });
}

function ProjectCtrl($scope, $routeParams, $http) {
    var serviceURL = serviceBaseUrl + 'projects/v0.1/' + $routeParams.projectKey;

    console.log('in ProjectCtrl');

    //init click events
    $('#project-start-area').datetimepicker().on('changeDate', function(e) {
        console.log(e.date);
        datepicker2model(e, $scope);
    });
    $('#project-end-area').datetimepicker().on('changeDate', function(e) {
        datepicker2model(e, $scope);
    });

    $http.get(serviceURL).success(function(data) {
        console.log(data);
        //convert date
        data.projectStart = new Date(data.projectStart).getText();
        data.projectEnd = new Date(data.projectEnd).getText();
        $scope.project = data;
    });

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

function PerDiemsSelectorCtrl($scope, $http) {

}

function PerDiemsCtrl($scope, $routeParams, $http) {
    var serviceURL = serviceBaseUrl + 'per-diem/' + individualId + '/' + $routeParams.yearMonth;

    $http.get(serviceURL).success(function(data) {
        console.log(data);
        $scope.perDiemsData = data;
    });

    //load destination list
    //FIXME cache
    $scope.travelExpensesRatesById = {};
    $http.get(serviceBaseUrl + 'per-diem/travel-expenses-rates').success(function(data) {
        console.log(data);
        $scope.travelExpensesRates = data;
        $.map(data, function(entry) { //use also as map object;
            $scope.travelExpensesRatesById[entry.id] = entry;
        });
        console.log($scope.travelExpensesRatesById);
    });
    $http.get(serviceBaseUrl + 'projects/list').success(function(data) {
        console.log(data);
        $scope.projects= data;
    });
    
    $scope.calcPerDiem = function(perDiem) {
        var terId = perDiem.travelExpensesRateId;
        var fullTime = perDiem.fullTime;
        if (fullTime) {
            perDiem.charges = $scope.travelExpensesRatesById[terId].rate24h;
        }
        
    };

    $scope.sendToServer = function() {
        console.log('->sendToServer');
        console.log($scope.perDiemsData);

        $http.put(serviceURL, $scope.perDiemsData).success(function(data) {
            console.log('<--fromServer');
            console.log(data);
            $scope.perDiemsData = data;
        });
    };
}



function TravelCostsListCtrl($scope, $http) {
    $http.get(serviceBaseUrl + 'travel-costs').success(function(data) {
        console.log(data);
        $scope.travelCosts = data;
    });
}

function TravelCostsCtrl($scope, $http) {
    //selectCurrentNavi('travel-costs');
    //$http.get(serviceBaseUrl + 'travel-costs').success(function(data) {
    //    console.log(data);
    //    $scope.travelCosts = data;
    //});
}




$(document).ready(function() {

    $(".has_submenu > a").click(function(e) {
        e.preventDefault();
        var menu_li = $(this).parent("li");
        var menu_ul = $(this).next("ul");

        if (menu_li.hasClass("open")) {
            menu_ul.slideUp(350);
            menu_li.removeClass("open");
        }
        else {
            $(".navi > li > ul").slideUp(350);
            $(".navi > li").removeClass("open");
            menu_ul.slideDown(350);
            menu_li.addClass("open");
        }
    });

    $('.mainbar').on("click", '.wminimize', function(e) {
        e.preventDefault();
        var $wcontent = $(this).parent().parent().next('.widget-content');
        if ($wcontent.is(':visible'))
        {
            $(this).children('i').removeClass('icon-chevron-up');
            $(this).children('i').addClass('icon-chevron-down');
        }
        else
        {
            $(this).children('i').removeClass('icon-chevron-down');
            $(this).children('i').addClass('icon-chevron-up');
        }
        $wcontent.toggle(500);
    });

});

