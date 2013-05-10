ProjectModel = Backbone.Model.extend({
    url: "rest/projects/[projectKey]",
    refresh: function(projectKey) {
        this.url = this.url.replace('[projectKey]', projectKey);
        this.fetch({
            success: function(data) {
                projectView.render();
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

window.project = new ProjectModel();



new ProjectView({
    el: $("#main-content"),
    model: window.project
});
