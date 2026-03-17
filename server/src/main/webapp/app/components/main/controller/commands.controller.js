// Localization completed
angular.module('headwind-kiosk')
    .controller('CommandsController', function ($scope, $modal, localization, deviceService, commandService, alertService) {

        $scope.localization = localization;
        $scope.devices = [];
        $scope.selectedDevices = [];
        $scope.searchQuery = '';
        $scope.selectAll = false;
        $scope.commandHistory = [];

        // Available commands
        $scope.availableCommands = [
            {id: 'reboot', name: 'command.reboot', icon: 'restart_alt', description: 'command.desc.reboot'},
            {id: 'lock', name: 'command.lock', icon: 'lock', description: 'command.desc.lock'},
            {id: 'unlock', name: 'command.unlock', icon: 'lock_open', description: 'command.desc.unlock'},
            {id: 'reset', name: 'command.reset', icon: 'factory', description: 'command.desc.reset'},
            {id: 'clearData', name: 'command.clear.data', icon: 'delete_forever', description: 'command.desc.clear.data'},
            {id: 'installApp', name: 'command.install.app', icon: 'get_app', description: 'command.desc.install.app'},
            {id: 'uninstallApp', name: 'command.uninstall.app', icon: 'delete', description: 'command.desc.uninstall.app'},
            {id: 'runApp', name: 'command.run.app', icon: 'play_arrow', description: 'command.desc.run.app'},
            {id: 'updateConfig', name: 'command.update.config', icon: 'sync', description: 'command.desc.update.config'}
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

        // Open command modal
        $scope.openCommandModal = function(command) {
            if ($scope.selectedDevices.length === 0) {
                alertService.showWarningMessage(
                    localization.localize('warning.no.devices.selected')
                );
                return;
            }

            var modalInstance = $modal.open({
                templateUrl: 'app/components/main/view/modals/command-modal.html',
                controller: 'CommandModalController',
                resolve: {
                    command: function() {
                        return command;
                    },
                    devices: function() {
                        return $scope.selectedDevices;
                    }
                }
            });

            modalInstance.result.then(function(result) {
                $scope.loadCommandHistory();
            });
        };

        // Load command history
        $scope.loadCommandHistory = function() {
            commandService.getCommandHistory({}, function(response) {
                if (response.status === 'OK') {
                    $scope.commandHistory = response.data || [];
                }
            });
        };

        // Initialize
        $scope.loadDevices();
        $scope.loadCommandHistory();
    })
    .controller('CommandModalController', function ($scope, $modalInstance, command, devices, commandService, localization, alertService) {
        $scope.localization = localization;
        $scope.command = command;
        $scope.devices = devices;
        $scope.commandParams = {};
        $scope.isExecuting = false;

        // Initialize command params based on command type
        $scope.initParams = function() {
            switch(command.id) {
                case 'installApp':
                case 'uninstallApp':
                case 'runApp':
                    $scope.commandParams.packageId = '';
                    break;
                case 'lock':
                    $scope.commandParams.message = '';
                    break;
            }
        };

        // Execute command
        $scope.executeCommand = function() {
            $scope.isExecuting = true;

            var deviceIds = $scope.devices.map(function(d) {
                return d.id;
            });

            var params = {
                command: $scope.command.id,
                deviceIds: deviceIds,
                params: $scope.commandParams
            };

            commandService.executeCommand(params, function(response) {
                $scope.isExecuting = false;
                if (response.status === 'OK') {
                    $modalInstance.close(response.data);
                    alertService.showSuccessMessage(
                        localization.localize('success.command.executed')
                    );
                } else {
                    alertService.showErrorMessage(
                        localization.localize('error.command.failed')
                    );
                }
            });
        };

        // Cancel
        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.initParams();
    });
