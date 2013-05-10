String.prototype.toCamel = function() {
    return this.replace(/(\-[a-z])/g, function($1) {
        return $1.toUpperCase().replace('-', '');
    });
};


$(function() {
    //load pages - controller
    $('.sidebar li').click(function() {

        $('.navi li').removeClass('current');
        $(this).addClass('current');

        loadObject($(this).find('a').attr('href'));
        return false;
    });


    //page load
    loadObject(window.location.href);
});
