/**
 * View Toggle Component
 * Toggle between table and card views
 * Note: View persistence is handled by tabs.controller.js
 */

angular.module('headwind-kiosk').controller('ViewToggleController',
['$scope', '$rootScope', function($scope, $rootScope) {

    // View types
    $scope.VIEWS = {
        TABLE: 'table',
        CARD: 'card'
    };

    // Current view - default to table, will be updated by parent
    $scope.currentView = $scope.VIEWS.TABLE;

    /**
     * Listen for view mode changes from parent controller
     */
    $scope.$on('viewModeChanged', function(event, mode) {
        if (mode === $scope.VIEWS.TABLE || mode === $scope.VIEWS.CARD) {
            $scope.currentView = mode;
        }
    });

    /**
     * Set view
     */
    $scope.setView = function(view) {
        if (view !== $scope.currentView) {
            $scope.currentView = view;
            // Broadcast event for parent controllers (tabs.controller.js handles persistence)
            $rootScope.$broadcast('viewChanged', view);
            // Call callback if provided
            if ($scope.onViewChange) {
                $scope.onViewChange({view: view});
            }
        }
    };

    /**
     * Check if view is active
     */
    $scope.isActive = function(view) {
        return $scope.currentView === view;
    };
}])
.directive('viewToggle', function() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/ui/view-toggle/view-toggle.html',
        controller: 'ViewToggleController',
        controllerAs: 'viewCtrl',
        scope: {
            // Optional: callback when view changes
            onViewChange: '&?'
        }
    };
});
