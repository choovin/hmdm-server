/**
 * Sidebar Controller
 * Features:
 * - Collapsible sidebar (expanded/collapsed)
 * - Menu item management
 * - Active state tracking
 * - Persist collapsed state to localStorage
 */

angular.module('headwind-kiosk').controller('SidebarController',
['$scope', '$location', '$rootScope',
function($scope, $location, $rootScope) {

    const STORAGE_KEY = 'hmdm-sidebar-collapsed';

    // Sidebar state
    $scope.isCollapsed = false;
    $scope.activeMenu = '';

    // Menu structure
    $scope.menuItems = [
        {
            id: 'devices',
            icon: 'smartphone',
            label: 'menu.devices',
            route: '/devices',
            permission: 'devices'
        },
        {
            id: 'applications',
            icon: 'apps',
            label: 'menu.applications',
            route: '/applications',
            permission: 'applications'
        },
        {
            id: 'configurations',
            icon: 'settings_applications',
            label: 'menu.configurations',
            route: '/configurations',
            permission: 'configurations'
        },
        {
            id: 'files',
            icon: 'folder',
            label: 'menu.files',
            route: '/files',
            permission: 'files'
        },
        {
            id: 'users',
            icon: 'people',
            label: 'menu.users',
            route: '/settings/users',
            permission: 'settings'
        },
        {
            id: 'groups',
            icon: 'account_tree',
            label: 'menu.groups',
            route: '/settings/groups',
            permission: null
        },
        {
            id: 'plugins',
            icon: 'extension',
            label: 'menu.plugins',
            route: '/plugins',
            permission: 'plugins_customer_access_management'
        }
    ];

    /**
     * Initialize sidebar
     */
    function init() {
        // Load collapsed state from storage
        var savedState = localStorage.getItem(STORAGE_KEY);
        if (savedState !== null) {
            $scope.isCollapsed = savedState === 'true';
        }

        // Set active menu based on current route
        updateActiveMenu();

        // Listen for route changes
        $rootScope.$on('$locationChangeSuccess', function() {
            updateActiveMenu();
        });
    }

    /**
     * Update active menu based on current route
     */
    function updateActiveMenu() {
        var path = $location.path();

        // Find matching menu item
        var activeItem = $scope.menuItems.find(function(item) {
            return path.indexOf(item.route) !== -1;
        });

        $scope.activeMenu = activeItem ? activeItem.id : '';
    }

    /**
     * Toggle sidebar collapsed state
     */
    $scope.toggleSidebar = function() {
        $scope.isCollapsed = !$scope.isCollapsed;
        localStorage.setItem(STORAGE_KEY, $scope.isCollapsed);

        // Broadcast event for other components
        $rootScope.$broadcast('sidebarToggle', $scope.isCollapsed);
    };

    /**
     * Navigate to menu item
     */
    $scope.navigate = function(item) {
        if (item.route) {
            $location.path(item.route);
        }
    };

    /**
     * Check if menu item is active
     */
    $scope.isActive = function(itemId) {
        return $scope.activeMenu === itemId;
    };

    /**
     * Check if user has permission for menu item
     */
    $scope.hasPermission = function(permission) {
        if (!permission) return true;
        // Delegate to existing permission check
        return $scope.$parent.hasPermission ? $scope.$parent.hasPermission(permission) : true;
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
