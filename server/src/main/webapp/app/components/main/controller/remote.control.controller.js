// Localization completed
angular.module('headwind-kiosk')
    .controller('RemoteControlController', function ($scope, $interval, localization, deviceService, remoteControlService, alertService) {

        $scope.localization = localization;
        $scope.devices = [];
        $scope.selectedDevice = null;
        $scope.searchQuery = '';
        $scope.isConnecting = false;
        $scope.connectionStatus = null;
        $scope.remoteSession = null;
        $scope.screenStream = null;

        // Paging
        $scope.paging = {
            currentPage: 1,
            pageSize: 10,
            totalItems: 0,
            totalPages: 0
        };

        // Load devices with remote control capability
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

        // Select device for remote control
        $scope.selectDevice = function(device) {
            $scope.selectedDevice = device;
            $scope.connectionStatus = null;
            $scope.remoteSession = null;
        };

        // Start remote control session
        $scope.startRemoteControl = function() {
            if (!$scope.selectedDevice) {
                alertService.showWarningMessage(
                    localization.localize('warning.select.device')
                );
                return;
            }

            $scope.isConnecting = true;
            $scope.connectionStatus = 'connecting';

            var params = {
                deviceId: $scope.selectedDevice.id
            };

            remoteControlService.startSession(params, function(response) {
                $scope.isConnecting = false;
                if (response.status === 'OK') {
                    $scope.connectionStatus = 'connected';
                    $scope.remoteSession = response.data;
                    alertService.showSuccessMessage(
                        localization.localize('success.remote.control.started')
                    );
                } else {
                    $scope.connectionStatus = 'error';
                    alertService.showErrorMessage(
                        localization.localize('error.remote.control.failed')
                    );
                }
            });
        };

        // Stop remote control session
        $scope.stopRemoteControl = function() {
            if (!$scope.remoteSession) return;

            remoteControlService.stopSession({
                sessionId: $scope.remoteSession.sessionId
            }, function(response) {
                $scope.connectionStatus = null;
                $scope.remoteSession = null;
                $scope.screenStream = null;
                alertService.showSuccessMessage(
                    localization.localize('success.remote.control.stopped')
                );
            });
        };

        // Send remote action (click, swipe, etc.)
        $scope.sendRemoteAction = function(action, data) {
            if (!$scope.remoteSession) return;

            var params = {
                sessionId: $scope.remoteSession.sessionId,
                action: action,
                data: data
            };

            remoteControlService.sendAction(params, function(response) {
                if (response.status !== 'OK') {
                    console.error('Remote action failed:', response.message);
                }
            });
        };

        // Take remote screenshot
        $scope.takeScreenshot = function() {
            if (!$scope.remoteSession) return;

            remoteControlService.takeScreenshot({
                sessionId: $scope.remoteSession.sessionId
            }, function(response) {
                if (response.status === 'OK') {
                    $scope.screenStream = response.data.imageUrl;
                }
            });
        };

        // Initialize
        $scope.loadDevices();
    });
