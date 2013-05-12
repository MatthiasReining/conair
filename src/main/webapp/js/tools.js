String.prototype.toCamel = function() {
    return this.replace(/(\-[a-z])/g, function($1) {
        return $1.toUpperCase().replace('-', '');
    });
};


loadTemplate = function(path) {
    var template;
    $.ajax(path, {
        'cache': false,
        'async': false
    }).done(function(data) {
        var htmlTemplate = data;
        _.templateSettings.variable = "data";
        // Grab the HTML out of our template tag and pre-compile it.
        template = _.template(htmlTemplate);
    });
    return template;
};


loadObject = function(href) {

    if (href.indexOf('#') === -1)
        return; //TODO load default page
    var pageName = href.substring(href.indexOf('#'));
    var param;
    if (pageName.indexOf('/') > -1) {
        param=pageName.substring(pageName.indexOf('/'));
        pageName = pageName.substring(0, pageName.indexOf('/'));
    }
    pageName = pageName.substring(1);
    var className = pageName.toCamel();
    className = className.substring(0, 1).toUpperCase() + className.substring(1);

    var htmlTarget = $('#main-content');
    //TODO build cache
    eval('new ' + className + '().load(htmlTarget, param)');

};


$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};
