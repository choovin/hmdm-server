/**
 * Sidebar Controller
 * Features:
 * - Collapsible sidebar (expanded/collapsed)
 * - Menu item management
 * - Active state tracking
 * - Persist collapsed state to localStorage
 */

angular.module('headwind-kiosk').controller('SidebarController',
['$scope', '$state', '$rootScope', 'authService', 'localization', 'pluginService',
function($scope, $state, $rootScope, authService, localization, pluginService) {

    const STORAGE_KEY = 'hmdm-sidebar-collapsed';

    // Sidebar state - use sidebarCollapsed to match main.html
    $scope.sidebarCollapsed = false;
    $scope.activeMenu = '';
    $scope.localization = localization;
    $scope.functionsPlugins = [];

    // Expose auth service methods
    $scope.hasPermission = authService.hasPermission;
    $scope.canManageRoles = function() {
        return authService.isSingleCustomer() || authService.isSuperAdmin();
    };

    /**
     * Initialize sidebar
     */
    function init() {
        // Load collapsed state from storage
        var savedState = localStorage.getItem(STORAGE_KEY);
        if (savedState !== null) {
            $scope.sidebarCollapsed = savedState === 'true';
        }

        // Set active menu based on current route
        updateActiveMenu();

        // Listen for route changes
        $rootScope.$on('$stateChangeSuccess', function() {
            updateActiveMenu();
        });

        // Watch for sidebar toggle from header
        $scope.$on('sidebarToggle', function(event, collapsed) {
            $scope.sidebarCollapsed = collapsed;
        });

        // Load plugin functions
        loadPlugins();
    }

    /**
     * Load plugin functions
     */
    function loadPlugins() {
        pluginService.getAvailablePlugins(function(response) {
            if (response.status === 'OK' && response.data) {
                $scope.functionsPlugins = response.data.filter(function(plugin) {
                    return plugin.functionsViewTemplate !== undefined && plugin.functionsViewTemplate !== null;
                });
            }
        });
    }

    /**
     * Update active menu based on current route
     */
    function updateActiveMenu() {
        $scope.activeMenu = $state.current.name;
    }

    /**
     * Toggle sidebar collapsed state
     */
    $scope.toggleSidebar = function() {
        $scope.sidebarCollapsed = !$scope.sidebarCollapsed;
        localStorage.setItem(STORAGE_KEY, $scope.sidebarCollapsed);

        // Broadcast event for other components
        $rootScope.$broadcast('sidebarToggle', $scope.sidebarCollapsed);
    };

    /**
     * Navigate to a state
     */
    $scope.navigateTo = function(stateName) {
        if (stateName && $state.current.name !== stateName) {
            $state.transitionTo(stateName);
        }
    };

    /**
     * Navigate to plugin function
     */
    $scope.navigateToPlugin = function(pluginId) {
        var stateName = 'plugin-' + pluginId;
        if ($state.current.name !== stateName) {
            $state.transitionTo(stateName);
        }
    };

    /**
     * Check if a state is active
     */
    $scope.isActive = function(stateName) {
        return $state.current.name === stateName;
    };

    /**
     * Check if a plugin is active
     */
    $scope.isPluginActive = function(pluginId) {
        return $state.current.name === 'plugin-' + pluginId;
    };

    // Initialize
    init();
}])
.directive('hmdmSidebar', function() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/ui/sidebar/sidebar.html',
        controller: 'SidebarController',
        controllerAs: 'sidebarCtrl',
        scope: {
            // Optional: pass in custom menu items
            customMenuItems: '=?'
        }
    };
});
