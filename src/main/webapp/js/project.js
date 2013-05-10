Project = function() {
    var that = this;
    var template;

    var postConstruct = function() {
        template = loadTemplate('snippets/project.html');
    }();

    this.load = function(htmlTarget, param) {
        alert('param: ' + param);
        $.get('rest/projects' + param, {
            'cache': false
        }).done(function(data) {
            var htmlContent = template(data);
            htmlTarget.html(htmlContent);
        });

    };
};

