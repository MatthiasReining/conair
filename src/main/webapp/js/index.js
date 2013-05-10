ProjectModel = Backbone.Model.extend({
    url: "rest/projects/[projectKey]",
    refresh: function(projectKey) {
        this.url = this.url.replace('[projectKey]', projectKey);
        this.fetch({
            success: function(data) {
                window.projectView.render();
            }
        });
    }
});

ProjectView = Backbone.View.extend({
    template: loadTemplate('snippets/project.html'),
    render: function() {
        this.$el.html(this.template(this.model.toJSON()));
        return this;
    }
});


ProjectListModel = Backbone.Model.extend({
    url: "rest/projects/list",
    refresh: function() {
        this.fetch({
            success: function(data) {
                window.projectListView.render();
            }
        });
    }
});

ProjectListView = Backbone.View.extend({
    template: loadTemplate('snippets/project-list.html'),
    initialize: function() {
        //this.render();
    },
    render: function() {
        _.templateSettings.variable = "data";
        this.$el.html(this.template(this.model.toJSON()));
    }
});



//Routing

var AppRouter = Backbone.Router.extend({
    routes: {
        ":page": "loadPage",
        "project/:projectKey": "loadProject",
        "project-list": "loadProjectList",
        "*actions": "defaultRoute" // matches http://example.com/#anything-here
    }
});
// Initiate the router
var app_router = new AppRouter;

app_router.on('route:defaultRoute', function(actions) {
    alert(actions);
});

app_router.on('route:loadPage', function(page) {
    //select navigation entry
    $('.navi li').removeClass('current');
    $.each($('.navi li'), function(index, key) {
        var href = $(key).find('a').attr('href');
        if (href.indexOf('#/' + page) > -1) {
            $(key).addClass('current');
        }
    });

    projectList.refresh();
    //loadObject(page);
});


app_router.on('route:loadProjectList', function() {
    window.projectList.refresh();
});

app_router.on('route:loadProject', function(projectKey) {
    alert(projectKey);
    window.project.refresh(projectKey);
});


// Start Backbone history a necessary step for bookmarkable URL's
Backbone.history.start();


$(function() {
    window.project = new ProjectModel();
    window.projectList = new ProjectListModel();


    window.projectView = new ProjectView({
        el: $("#main-content"),
        model: window.project
    });

    window.projectListView = new ProjectListView({
        el: $("#main-content"),
        model: window.projectList
    });


});