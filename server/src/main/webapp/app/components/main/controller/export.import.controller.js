// Localization completed
angular.module('headwind-kiosk')
    .controller('ExportImportController', function ($scope, $modal, localization, exportImportService, alertService) {

        $scope.localization = localization;
        $scope.importJobs = [];
        $scope.exportJobs = [];
        $scope.activeTab = 'import';
        $scope.isLoading = false;

        // Load import jobs
        $scope.loadImportJobs = function() {
            $scope.isLoading = true;
            exportImportService.getImportJobs({}, function(response) {
                $scope.isLoading = false;
                if (response.status === 'OK') {
                    $scope.importJobs = response.data || [];
                }
            });
        };

        // Load export jobs
        $scope.loadExportJobs = function() {
            $scope.isLoading = true;
            exportImportService.getExportJobs({}, function(response) {
                $scope.isLoading = false;
                if (response.status === 'OK') {
                    $scope.exportJobs = response.data || [];
                }
            });
        };

        // Switch tab
        $scope.switchTab = function(tab) {
            $scope.activeTab = tab;
            if (tab === 'import') {
                $scope.loadImportJobs();
            } else {
                $scope.loadExportJobs();
            }
        };

        // Open import modal
        $scope.openImportModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'app/components/main/view/modals/import-modal.html',
                controller: 'ImportModalController',
                resolve: {
                    localization: function() {
                        return localization;
                    }
                }
            });

            modalInstance.result.then(function(result) {
                $scope.loadImportJobs();
                alertService.showSuccessMessage(
                    localization.localize('success.import.started')
                );
            });
        };

        // Open export modal
        $scope.openExportModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'app/components/main/view/modals/export-modal.html',
                controller: 'ExportModalController',
                resolve: {
                    localization: function() {
                        return localization;
                    }
                }
            });

            modalInstance.result.then(function(result) {
                $scope.loadExportJobs();
                alertService.showSuccessMessage(
                    localization.localize('success.export.started')
                );
            });
        };

        // Download export file
        $scope.downloadExport = function(job) {
            exportImportService.downloadExport({id: job.id}, function(response) {
                if (response.status === 'OK' && response.data.url) {
                    window.open(response.data.url, '_blank');
                }
            });
        };

        // View job details
        $scope.viewJobDetails = function(job) {
            $modal.open({
                templateUrl: 'app/components/main/view/modals/job-details-modal.html',
                controller: 'JobDetailsModalController',
                resolve: {
                    job: function() {
                        return job;
                    },
                    localization: function() {
                        return localization;
                    }
                }
            });
        };

        // Delete job
        $scope.deleteJob = function(job, type) {
            if (confirm(localization.localize('confirm.delete.job'))) {
                var deleteFn = type === 'import' ?
                    exportImportService.deleteImportJob :
                    exportImportService.deleteExportJob;

                deleteFn({id: job.id}, function(response) {
                    if (response.status === 'OK') {
                        if (type === 'import') {
                            $scope.loadImportJobs();
                        } else {
                            $scope.loadExportJobs();
                        }
                        alertService.showSuccessMessage(
                            localization.localize('success.job.deleted')
                        );
                    }
                });
            }
        };

        // Initialize
        $scope.loadImportJobs();
    })
    .controller('ImportModalController', function ($scope, $modalInstance, localization, exportImportService, alertService) {
        $scope.localization = localization;
        $scope.importData = {
            file: null,
            format: 'csv',
            mode: 'create',
            skipErrors: true
        };
        $scope.isUploading = false;
        $scope.uploadProgress = 0;

        $scope.formats = [
            {id: 'csv', name: 'format.csv'},
            {id: 'xlsx', name: 'format.xlsx'},
            {id: 'json', name: 'format.json'}
        ];

        $scope.modes = [
            {id: 'create', name: 'import.mode.create'},
            {id: 'update', name: 'import.mode.update'},
            {id: 'upsert', name: 'import.mode.upsert'}
        ];

        // Handle file select
        $scope.onFileSelect = function(files) {
            if (files && files.length > 0) {
                $scope.importData.file = files[0];
            }
        };

        // Start import
        $scope.startImport = function() {
            if (!$scope.importData.file) {
                alertService.showWarningMessage(
                    localization.localize('warning.select.file')
                );
                return;
            }

            $scope.isUploading = true;

            var params = {
                file: $scope.importData.file,
                format: $scope.importData.format,
                mode: $scope.importData.mode,
                skipErrors: $scope.importData.skipErrors
            };

            exportImportService.importDevices(params, function(response) {
                $scope.isUploading = false;
                if (response.status === 'OK') {
                    $modalInstance.close(response.data);
                } else {
                    alertService.showErrorMessage(
                        localization.localize(response.message || 'error.import.failed')
                    );
                }
            }, function(progress) {
                $scope.uploadProgress = progress;
            });
        };

        // Cancel
        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    })
    .controller('ExportModalController', function ($scope, $modalInstance, localization, deviceService, exportImportService, alertService) {
        $scope.localization = localization;
        $scope.exportData = {
            format: 'csv',
            includeConfig: true,
            includeApps: true,
            includeLocation: false,
            selectedDevices: [],
            filter: {}
        };
        $scope.isExporting = false;
        $scope.devices = [];
        $scope.selectAll = false;

        $scope.formats = [
            {id: 'csv', name: 'format.csv'},
            {id: 'xlsx', name: 'format.xlsx'},
            {id: 'json', name: 'format.json'},
            {id: 'pdf', name: 'format.pdf'}
        ];

        // Load devices
        $scope.loadDevices = function() {
            deviceService.getAllDevices(function(response) {
                if (response.status === 'OK') {
                    $scope.devices = response.data || [];
                    $scope.devices.forEach(function(device) {
                        device.selected = false;
                    });
                }
            });
        };

        // Toggle select all
        $scope.toggleSelectAll = function() {
            $scope.devices.forEach(function(device) {
                device.selected = $scope.selectAll;
            });
        };

        // Start export
        $scope.startExport = function() {
            $scope.isExporting = true;

            var selectedDevices = $scope.devices.filter(function(d) {
                return d.selected;
            });

            var params = {
                format: $scope.exportData.format,
                includeConfig: $scope.exportData.includeConfig,
                includeApps: $scope.exportData.includeApps,
                includeLocation: $scope.exportData.includeLocation,
                deviceIds: selectedDevices.map(function(d) { return d.id; }),
                filter: $scope.exportData.filter
            };

            exportImportService.exportDevices(params, function(response) {
                $scope.isExporting = false;
                if (response.status === 'OK') {
                    $modalInstance.close(response.data);
                } else {
                    alertService.showErrorMessage(
                        localization.localize(response.message || 'error.export.failed')
                    );
                }
            });
        };

        // Cancel
        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.loadDevices();
    })
    .controller('JobDetailsModalController', function ($scope, $modalInstance, job, localization) {
        $scope.localization = localization;
        $scope.job = job;

        $scope.close = function() {
            $modalInstance.dismiss('close');
        };
    });
