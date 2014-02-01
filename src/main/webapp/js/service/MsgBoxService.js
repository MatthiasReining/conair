var MsgBoxService = function($modal) {

    this.open = function(param, fnYesCallback) {
        $modal.open({
            templateUrl: 'snippets/msgbox.html',
            controller: function($scope, $modalInstance, content) {

                $scope.content = content;

                if (angular.isUndefined($scope.content.title))
                    $scope.content.titel = '';
                if (angular.isUndefined($scope.content.message))
                    $scope.content.message = '';
                if (angular.isUndefined($scope.content.okBtn))
                    $scope.content.okBtn = 'OK';
                if (angular.isUndefined($scope.content.cancelBtn))
                    $scope.content.cancelBtn = 'Cancel';
                if (angular.isUndefined($scope.content.hideCancelBtn))
                    $scope.content.hideCancelBtn = false;

                $scope.ok = function() {
                    $modalInstance.close();
                };
                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };
            },
            resolve: {
                content: function() {
                    return param;
                }
            }
        }).result.then(fnYesCallback);
    };
};