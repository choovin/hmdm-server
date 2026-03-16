/**
 * Search Filter Component
 * Search, quick filters, and advanced filters
 */

angular.module('headwind-kiosk').controller('SearchFilterController',
['$scope', '$timeout', function($scope, $timeout) {

    // Search state
    $scope.searchQuery = '';
    $scope.searchTimeout = null;

    // Filters
    $scope.activeFilters = {};
    $scope.showAdvancedFilter = false;

    // Quick filter tags
    $scope.quickFilters = $scope.quickFilters || [];

    /**
     * Initialize
     */
    function init() {
        // Load saved filters if any
        var savedFilters = localStorage.getItem('hmdm-filters-' + $scope.filterKey);
        if (savedFilters) {
            try {
                $scope.activeFilters = JSON.parse(savedFilters);
            } catch(e) {
                console.warn('Failed to load saved filters');
            }
        }

        // Cleanup on scope destroy
        $scope.$on('$destroy', function() {
            if ($scope.searchTimeout) {
                $timeout.cancel($scope.searchTimeout);
            }
        });
    }

    /**
     * Handle search input with debounce
     */
    $scope.onSearchChange = function() {
        if ($scope.searchTimeout) {
            $timeout.cancel($scope.searchTimeout);
        }

        $scope.searchTimeout = $timeout(function() {
            $scope.applyFilters();
        }, 300);
    };

    /**
     * Apply search and filters
     */
    $scope.applyFilters = function() {
        var filterData = {
            search: $scope.searchQuery,
            filters: $scope.activeFilters
        };

        // Call parent handler
        if ($scope.onFilterChange) {
            $scope.onFilterChange({filters: filterData});
        }

        // Save filters only if changed
        if ($scope.filterKey) {
            var storageKey = 'hmdm-filters-' + $scope.filterKey;
            var currentStored = localStorage.getItem(storageKey);
            var newValue = JSON.stringify($scope.activeFilters);
            if (currentStored !== newValue) {
                localStorage.setItem(storageKey, newValue);
            }
        }
    };

    /**
     * Toggle quick filter
     */
    $scope.toggleQuickFilter = function(filter) {
        filter.active = !filter.active;

        if (filter.active) {
            $scope.activeFilters[filter.key] = filter.value;
        } else {
            delete $scope.activeFilters[filter.key];
        }

        $scope.applyFilters();
    };

    /**
     * Toggle advanced filter panel
     */
    $scope.toggleAdvancedFilter = function() {
        $scope.showAdvancedFilter = !$scope.showAdvancedFilter;
    };

    /**
     * Clear all filters
     */
    $scope.clearFilters = function() {
        $scope.searchQuery = '';
        $scope.activeFilters = {};

        // Reset quick filters
        $scope.quickFilters.forEach(function(f) {
            f.active = false;
        });

        $scope.applyFilters();
    };

    /**
     * Check if any filter is active
     */
    $scope.hasActiveFilters = function() {
        return $scope.searchQuery || Object.keys($scope.activeFilters).length > 0;
    };

    /**
     * Get active filter count
     */
    $scope.getActiveFilterCount = function() {
        return Object.keys($scope.activeFilters).length;
    };

    init();
}])
.directive('searchFilter', function() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/ui/search-filter/search-filter.html',
        controller: 'SearchFilterController',
        controllerAs: 'filterCtrl',
        scope: {
            filterKey: '@?',           // Unique key for saving filters
            quickFilters: '=?',        // Array of quick filter objects
            onFilterChange: '&?',      // Callback when filters change
            placeholder: '@?'          // Search placeholder text
        }
    };
});
