
function NaviCtrl($scope, $location) {
    $scope.isActive = function(route) {
        return ($location.path().indexOf(route) > -1);
    };
    $scope.test = function(path) {
        alert($scope.isActive(path));
    };
}

// Menu Controller by jQuery selector and Angular actions
$(document).ready(function() {
    $(".has_submenu > a").click(function(e) {
        //e.preventDefault();
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
    /**
    $('.mainbar').on("click", function(e) {
        e.preventDefault();
        var wcontent = $(this).parent().parent().next('.widget-content');
        if (wcontent.is(':visible'))
        {
            $(this).children('i').removeClass('icon-chevron-up');
            $(this).children('i').addClass('icon-chevron-down');
        }
        else
        {
            $(this).children('i').removeClass('icon-chevron-down');
            $(this).children('i').addClass('icon-chevron-up');
        }
        wcontent.toggle(500);
    }); */
});