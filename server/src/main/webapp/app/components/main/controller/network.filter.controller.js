// Localization completed
angular.module('headwind-kiosk')
    .controller('NetworkFilterController', function ($scope, $modal, localization, deviceService, networkFilterService, alertService) {

        $scope.localization = localization;
        $scope.devices = [];
        $scope.selectedDevices = [];
        $scope.searchQuery = '';
        $scope.selectAll = false;
        $scope.filters = [];

        // Filter types
        $scope.filterTypes = [
            {id: 'whitelist', name: 'filter.type.whitelist', icon: 'check_circle'},
            {id: 'blacklist', name: 'filter.type.blacklist', icon: 'block'}
        ];

        // Paging
        $scope.paging = {
            currentPage: 1,
            pageSize: 20,
            totalItems: 0,
            totalPages: 0
        };

        // Load devices
        $scope.loadDevices = function() {
            var params = {
                page: $scope.paging.currentPage - 1,
                pageSize: $scope.paging.pageSize
            };

            if ($scope.searchQuery) {
                params.search = $scope.searchQuery;
            }

            deviceService.getAllDevices(function(response) {
                if (response.status === 'OK') {
                    $scope.devices = response.data || [];
                    $scope.paging.totalItems = response.data.length || 0;

                    // Reset selection
                    $scope.devices.forEach(function(device) {
                        device.selected = false;
                    });
                    $scope.selectedDevices = [];
                    $scope.selectAll = false;
                } else {
                    alertService.showAlertMessage(
                        localization.localize('error.loading.devices'),
                        localization.localize(response.message || 'error.general')
                    );
                }
            });
        };

        // Search devices
        $scope.searchDevices = function() {
            $scope.paging.currentPage = 1;
            $scope.loadDevices();
        };

        // Page changed
        $scope.pageChanged = function() {
            $scope.loadDevices();
        };

        // Toggle select all
        $scope.toggleSelectAll = function() {
            $scope.devices.forEach(function(device) {
                device.selected = $scope.selectAll;
            });
            $scope.onDeviceSelect();
        };

        // On device select
        $scope.onDeviceSelect = function() {
            $scope.selectedDevices = $scope.devices.filter(function(d) {
                return d.selected;
            });
            $scope.selectAll = $scope.selectedDevices.length === $scope.devices.length && $scope.devices.length > 0;
        };

        // Load network filters
        $scope.loadFilters = function() {
            networkFilterService.getAllFilters({}, function(response) {
                if (response.status === 'OK') {
                    $scope.filters = response.data || [];
                }
            });
        };

        // Open filter modal
        $scope.openFilterModal = function(filter) {
            var modalInstance = $modal.open({
                templateUrl: 'app/components/main/view/modals/filter-modal.html',
                controller: 'FilterModalController',
                resolve: {
                    filter: function() {
                        return filter || {};
                    },
                    devices: function() {
                        return $scope.selectedDevices;
                    }
                }
            });

            modalInstance.result.then(function(result) {
                $scope.loadFilters();
            });
        };

        // Delete filter
        $scope.deleteFilter = function(filter) {
            if (confirm(localization.localize('confirm.delete.filter'))) {
                networkFilterService.deleteFilter({id: filter.id}, function(response) {
                    if (response.status === 'OK') {
                        $scope.loadFilters();
                        alertService.showSuccessMessage(
                            localization.localize('success.filter.deleted')
                        );
                    }
                });
            }
        };

        // Apply filter to devices
        $scope.applyFilter = function(filter) {
            if ($scope.selectedDevices.length === 0) {
                alertService.showWarningMessage(
                    localization.localize('warning.select.devices')
                );
                return;
            }

            var deviceIds = $scope.selectedDevices.map(function(d) {
                return d.id;
            });

            networkFilterService.applyFilter({
                filterId: filter.id,
                deviceIds: deviceIds
            }, function(response) {
                if (response.status === 'OK') {
                    alertService.showSuccessMessage(
                        localization.localize('success.filter.applied')
                    );
                }
            });
        };

        // Initialize
        $scope.loadDevices();
        $scope.loadFilters();
    })
    .controller('FilterModalController', function ($scope, $modalInstance, filter, devices, networkFilterService, localization, alertService) {
        $scope.localization = localization;
        $scope.filter = angular.copy(filter) || {};
        $scope.devices = devices;
        $scope.isSaving = false;
        $scope.filterTypes = [
            {id: 'whitelist', name: 'filter.type.whitelist'},
            {id: 'blacklist', name: 'filter.type.blacklist'}
        ];

        // Initialize filter rules
        if (!$scope.filter.rules) {
            $scope.filter.rules = [{type: 'domain', value: '', action: 'allow'}];
        }

        // Add rule
        $scope.addRule = function() {
            $scope.filter.rules.push({type: 'domain', value: '', action: 'allow'});
        };

        // Remove rule
        $scope.removeRule = function(index) {
            $scope.filter.rules.splice(index, 1);
        };

        // Save filter
        $scope.saveFilter = function() {
            $scope.isSaving = true;

            var params = {
                filter: $scope.filter,
                deviceIds: $scope.devices.map(function(d) { return d.id; })
            };

            networkFilterService.saveFilter(params, function(response) {
                $scope.isSaving = false;
                if (response.status === 'OK') {
                    $modalInstance.close(response.data);
                    alertService.showSuccessMessage(
                        localization.localize('success.filter.saved')
                    );
                } else {
                    alertService.showErrorMessage(
                        localization.localize('error.filter.save')
                    );
                }
            });
        };

        // Cancel
        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    });
