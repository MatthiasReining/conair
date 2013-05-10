ProjectList = function() {
    var that = this;
    var template;

    var postConstruct = function() {
        template = loadTemplate('snippets/project-list.html');
    }();

    this.load = function(htmlTarget) {
        $.get('rest/projects/list/', {
            'cache': false
        }).done(function(data) {
            var htmlContent = template(data);
            htmlTarget.html(htmlContent);
            
            $('#project-list-data a').click( function() {
                
                loadObject($(this).attr('href'));
                return false;
            });
        });

    };
};
