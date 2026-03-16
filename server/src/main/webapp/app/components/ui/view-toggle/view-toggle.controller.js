/**
 * View Toggle Component
 * Toggle between table and card views
 */

angular.module('headwind-kiosk').controller('ViewToggleController',
['$scope', '$rootScope', function($scope, $rootScope) {

    const STORAGE_KEY = 'hmdm-view-preference';

    // View types
    $scope.VIEWS = {
        TABLE: 'table',
        CARD: 'card'
    };

    // Current view
    $scope.currentView = $scope.VIEWS.TABLE;

    /**
     * Initialize
     */
    function init() {
        var savedView = localStorage.getItem(STORAGE_KEY);
        if (savedView && (savedView === $scope.VIEWS.TABLE || savedView === $scope.VIEWS.CARD)) {
            $scope.currentView =savedView;
        // Broadcast event for parent controllers
        $rootScope.$broadcast('viewChanged', view);
        }
    };

    /**
     * Check if view is active
     */
    $scope.isActive = function(view) {
        return $scope.currentView === view;
    };

    init();
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
