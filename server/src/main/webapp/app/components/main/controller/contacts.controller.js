// Localization completed
angular.module('headwind-kiosk')
    .controller('ContactsController', function ($scope, $modal, $timeout, localization, contactService, deviceService,
                                               authService, alertService) {

        $scope.localization = localization;
        $scope.contacts = [];
        $scope.devices = [];
        $scope.selectedContacts = [];
        $scope.searchQuery = '';
        $scope.selectAll = false;
        $scope.showDeviceSelection = false;
        $scope.deviceSearch = '';

        // Paging
        $scope.paging = {
            currentPage: 1,
            pageSize: 20,
            totalItems: 0,
            totalPages: 0
        };

        // Load contacts
        $scope.loadContacts = function() {
            var params = {
                page: $scope.paging.currentPage - 1,
                pageSize: $scope.paging.pageSize
            };

            if ($scope.searchQuery) {
                params.search = $scope.searchQuery;
            }

            contactService.searchContacts(params, function(response) {
                if (response.status === 'OK') {
                    $scope.contacts = response.data.items || [];
                    $scope.paging.totalItems = response.data.totalItems || 0;
                    $scope.paging.totalPages = response.data.totalPages || 0;

                    // Reset selection
                    $scope.contacts.forEach(function(contact) {
                        contact.selected = false;
                    });
                    $scope.selectedContacts = [];
                    $scope.selectAll = false;
                } else {
                    alertService.showAlertMessage(
                        localization.localize('error.loading.contacts'),
                        localization.localize(response.message || 'error.general')
                    );
                }
            });
        };

        // Search contacts
        $scope.searchContacts = function() {
            $scope.paging.currentPage = 1;
            $scope.loadContacts();
        };

        // Refresh contacts
        $scope.refreshContacts = function() {
            $scope.searchQuery = '';
            $scope.paging.currentPage = 1;
            $scope.loadContacts();
        };

        // Page changed
        $scope.pageChanged = function() {
            $scope.loadContacts();
        };

        // Toggle select all
        $scope.toggleSelectAll = function() {
            $scope.contacts.forEach(function(contact) {
                contact.selected = $scope.selectAll;
            });
            $scope.onContactSelect();
        };

        // On contact select
        $scope.onContactSelect = function() {
            $scope.selectedContacts = $scope.contacts.filter(function(c) {
                return c.selected;
            });
            $scope.selectAll = $scope.selectedContacts.length === $scope.contacts.length && $scope.contacts.length > 0;
        };

        // Open add contact modal
        $scope.openAddContactModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'app/components/main/view/modals/contact-modal.html',
                controller: 'ContactModalController',
                resolve: {
                    contact: function() {
                        return null;
                    }
                }
            });

            modalInstance.result.then(function() {
                $scope.loadContacts();
            });
        };

        // Edit contact
        $scope.editContact = function(contact) {
            var modalInstance = $modal.open({
                templateUrl: 'app/components/main/view/modals/contact-modal.html',
                controller: 'ContactModalController',
                resolve: {
                    contact: function() {
                        return angular.copy(contact);
                    }
                }
            });

            modalInstance.result.then(function() {
                $scope.loadContacts();
            });
        };

        // Delete contact
        $scope.deleteContact = function(contact) {
            alertService.confirmDelete(
                localization.localize('confirm.delete.contact'),
                localization.localize('confirm.delete.contact.message', contact.name),
                function() {
                    contactService.deleteContact({id: contact.id}, function(response) {
                        if (response.status === 'OK') {
                            $scope.loadContacts();
                            alertService.showSuccessMessage(
                                localization.localize('success.contact.deleted')
                            );
                        } else {
                            alertService.showErrorMessage(
                                localization.localize('error.deleting.contact')
                            );
                        }
                    });
                }
            );
        };

        // Sync to single device
        $scope.syncToDevice = function(contact) {
            $scope.selectedContacts = [contact];
            $scope.showDeviceSelectionModal();
        };

        // Sync contacts to devices
        $scope.syncContactsToDevices = function() {
            if ($scope.selectedContacts.length === 0) {
                alertService.showWarningMessage(
                    localization.localize('warning.no.contacts.selected')
                );
                return;
            }
            $scope.showDeviceSelectionModal();
        };

        // Show device selection modal
        $scope.showDeviceSelectionModal = function() {
            $scope.showDeviceSelection = true;
            $scope.loadDevices();
        };

        // Cancel device selection
        $scope.cancelDeviceSelection = function() {
            $scope.showDeviceSelection = false;
            $scope.devices.forEach(function(device) {
                device.selected = false;
            });
        };

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

        // Confirm sync to devices
        $scope.confirmSyncToDevices = function() {
            var selectedDevices = $scope.devices.filter(function(d) {
                return d.selected;
            });

            if (selectedDevices.length === 0) {
                alertService.showWarningMessage(
                    localization.localize('warning.no.devices.selected')
                );
                return;
            }

            var deviceIds = selectedDevices.map(function(d) {
                return d.id;
            });

            var contactIds = $scope.selectedContacts.map(function(c) {
                return c.id;
            });

            var syncData = {
                deviceIds: deviceIds,
                contactIds: contactIds
            };

            contactService.syncContactsToDevices(syncData, function(response) {
                if (response.status === 'OK') {
                    alertService.showSuccessMessage(
                        localization.localize('success.contacts.synced')
                    );
                    $scope.cancelDeviceSelection();
                } else {
                    alertService.showErrorMessage(
                        localization.localize('error.syncing.contacts')
                    );
                }
            });
        };

        // Initialize
        $scope.loadContacts();
    })
    .controller('ContactModalController', function ($scope, $modalInstance, contact, contactService, deviceService,
                                                   localization, alertService) {
        $scope.localization = localization;
        $scope.isEditing = !!contact;
        $scope.contact = contact || {
            name: '',
            phone: '',
            email: '',
            company: '',
            title: '',
            deviceIds: []
        };
        $scope.devices = [];

        // Load devices
        $scope.loadDevices = function() {
            deviceService.getAllDevices(function(response) {
                if (response.status === 'OK') {
                    $scope.devices = response.data || [];
                }
            });
        };

        // Save contact
        $scope.saveContact = function() {
            if (!$scope.contact.name) {
                alertService.showWarningMessage(
                    localization.localize('warning.contact.name.required')
                );
                return;
            }

            var saveFunc = $scope.isEditing ?
                contactService.updateContact :
                contactService.createContact;

            var params = $scope.isEditing ?
                {id: $scope.contact.id} :
                {};

            saveFunc(params, $scope.contact, function(response) {
                if (response.status === 'OK') {
                    $modalInstance.close(response.data);
                    alertService.showSuccessMessage(
                        $scope.isEditing ?
                            localization.localize('success.contact.updated') :
                            localization.localize('success.contact.created')
                    );
                } else {
                    alertService.showErrorMessage(
                        localization.localize('error.saving.contact')
                    );
                }
            });
        };

        // Cancel
        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.loadDevices();
    });
