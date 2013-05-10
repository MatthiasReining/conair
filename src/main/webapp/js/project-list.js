ProjectListModel = Backbone.Model.extend({
    url: "rest/projects/list",
    refresh: function() {
        this.fetch({
            success: function(data) {
                projectListView.render();
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


window.projectList = new ProjectListModel();

new ProjectListView({
    el: $("#main-content"),
    model: window.projectList
});
