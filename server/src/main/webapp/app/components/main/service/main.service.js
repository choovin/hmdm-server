// Localization completed
angular.module('headwind-kiosk')
    .factory('deviceService', function ($resource) {
        return $resource('', {}, {
            getAllDevices: {url: 'rest/private/devices/search', method: 'POST'},
            updateDevice: {url: 'rest/private/devices', method: 'PUT'},
            updateDeviceDesc: {url: 'rest/private/devices/:id/description', method: 'POST'},
            removeDevice: {url: 'rest/private/devices/:id', method: 'DELETE'},
            removeDeviceBulk: {url: 'rest/private/devices/deleteBulk', method: 'POST'},
            updateDeviceGroupBulk: {url: 'rest/private/devices/groupBulk', method: 'POST'},
            getDeviceApplicationSettings: {url: 'rest/private/devices/:id/applicationSettings', method: 'GET'},
            saveDeviceApplicationSettings: {url: 'rest/private/devices/:id/applicationSettings', method: 'POST'},
            notifyDeviceOnAppSettingsUpdate: {url: 'rest/private/devices/:id/applicationSettings/notify', method: 'POST'},
            rebootDevice: {url: 'rest/private/devices/:id/reboot', method: 'POST'},
            lockDevice: {url: 'rest/private/devices/:id/lock', method: 'POST'},
            factoryResetDevice: {url: 'rest/private/devices/:id/factoryReset', method: 'POST'},
            remoteCommandBulk: {url: 'rest/private/devices/remoteCommandBulk', method: 'POST'},
            requestPhotoUpload: {url: 'rest/private/photos/request/:deviceId', method: 'POST'}
        });
    })
    .factory('configurationService', function ($resource) {
        return $resource('', {}, {
            getAllConfigurations: {url: 'rest/private/configurations/search/:value', method: 'GET'},
            getAllConfigNames: {url: 'rest/private/configurations/list', method: 'GET'},
            getById: {url: 'rest/private/configurations/:id', method: 'GET'},
            getAllTypicalConfigurations: {url: 'rest/private/configurations/typical/search/:value', method: 'GET'},
            updateConfiguration: {url: 'rest/private/configurations', method: 'PUT'},
            copyConfiguration: {url: 'rest/private/configurations/copy', method: 'PUT'},
            upgradeConfigurationApplication: {url: 'rest/private/configurations/:id/application/:appId/upgrade', method: 'PUT'},
            removeConfiguration: {url: 'rest/private/configurations/:id', method: 'DELETE'},
            getApplications: {url: 'rest/private/configurations/applications/:id', method: 'GET'},
        });
    })
    .factory('customerService', function ($resource) {
        return $resource('', {}, {
            getAllCustomers: {url: 'rest/private/customers/search', method: 'POST'},
            getForUpdate: {url: 'rest/private/customers/:id/edit', method: 'GET'},
            updateCustomer: {url: 'rest/private/customers', method: 'PUT'},
            removeCustomer: {url: 'rest/private/customers/:id', method: 'DELETE'},
            loginAs: {url: 'rest/private/customers/impersonate/:id', method: 'GET'},
            isUsedPrefix: {url: 'rest/private/customers/prefix/:prefix/used', method: 'GET'},
        });
    })
    .factory('iconService', function ($resource) {
        return $resource('', {}, {
            getAllIcons: {url: 'rest/private/icons/search/:value', method: 'GET'},
            createIcon: {url: 'rest/private/icons', method: 'PUT'},
            removeIcon: {url: 'rest/private/icons/:id', method: 'DELETE'},
        });
    })
    .factory('settingsService', function ($resource) {
        return $resource('', {}, {
            getSettings: {url: 'rest/private/settings', method: 'GET'},
            getUserRoleSettings: {url: 'rest/private/settings/userRole/:roleId', method: 'GET'},
            updateDefaultDesignSettings: {url: 'rest/private/settings/design', method: 'POST'},
            updateUserRolesCommonSettings: {url: 'rest/private/settings/userRoles/common', method: 'POST'},
            updateMiscSettings: {url: 'rest/private/settings/misc', method: 'POST'},
            updateLanguageSettings: {url: 'rest/private/settings/lang', method: 'POST'},
        })
    })
    .factory('updatesService', function ($resource) {
        return $resource('', {}, {
            checkUpdates: {url: 'rest/private/update/check', method: 'GET'},
            getUpdates: {url: 'rest/private/update', method: 'POST'},
        })
    })
    .factory('passwordResetService', function ($resource) {
        return $resource('', {}, {
            getSettings: {url: 'rest/public/passwordReset/settings/:token', method: 'GET'},
            resetPassword: {url: 'rest/public/passwordReset/reset', method: 'POST'},
            canRecover: {url: 'rest/public/passwordReset/canRecover', method: 'GET'},
            recoverPassword: {url: 'rest/public/passwordReset/recover/:username', method: 'GET'}
        })
    })
    .factory('twoFactorAuthService', function ($resource) {
        return $resource('', {}, {
            verify: {url: 'rest/private/twofactor/verify/:user/:code', method: 'GET'},
            set: {url: 'rest/private/twofactor/set', method: 'GET'},
            reset: {url: 'rest/private/twofactor/reset', method: 'GET'}
        })
    })
    .factory('signupService', function ($resource) {
        return $resource('', {}, {
            canSignup: {url: 'rest/public/signup/canSignup', method: 'GET'},
            verifyEmail: {url: 'rest/public/signup/verifyEmail', method: 'POST'},
            verifyToken: {url: 'rest/public/signup/verifyToken/:token', method: 'GET'},
            complete: {url: 'rest/public/signup/complete', method: 'POST'}
        })
    })
    .factory('groupService', function ($resource) {
        return $resource('', {}, {
            getAllGroups: {url: 'rest/private/groups/search/:value', method: 'GET'},
            getGroup: {url: 'rest/private/groups/:id', method: 'GET'},
            updateGroup: {url: 'rest/private/groups', method: 'PUT'},
            removeGroup: {url: 'rest/private/groups/:id', method: 'DELETE'}
        })
    })
    .factory('roleService', function ($resource) {
        return $resource('', {}, {
            getPermissions: {url: 'rest/private/roles/permissions', method: 'GET'},
            getRoles: {url: 'rest/private/roles/all', method: 'GET'},
            updateRole: {url: 'rest/private/roles', method: 'PUT'},
            removeRole: {url: 'rest/private/roles/:id', method: 'DELETE'}
        })
    })
    .factory('pluginService', function ($resource) {
        return $resource('', {}, {
            getAvailablePlugins: {url: 'rest/plugin/main/private/available', method: 'GET'},
            getRegisteredPlugins: {url: 'rest/plugin/main/public/registered', method: 'GET'},
            getActivePlugins: {url: 'rest/plugin/main/private/active', method: 'GET'},
            disablePlugins: {url: 'rest/plugin/main/private/disabled', method: 'POST'},
        })
    })
    .factory('applicationService', function ($resource) {
        return $resource('', {}, {
            getAllApplications: {url: 'rest/private/applications/search/:value', method: 'GET'},
            getApplicationVersions: {url: 'rest/private/applications/:id/versions', method: 'GET'},
            getAllAdminApplications: {url: 'rest/private/applications/admin/search/:value', method: 'GET'},
            getApplication: {url: 'rest/private/applications/:id', method: 'GET'},
            updateApplication: {url: 'rest/private/applications/android', method: 'PUT'},
            updateWebApplication: {url: 'rest/private/applications/web', method: 'PUT'},
            validateApplicationPackage: {url: 'rest/private/applications/validatePkg', method: 'PUT'},
            updateApplicationVersion: {url: 'rest/private/applications/versions', method: 'PUT'},
            removeApplication: {url: 'rest/private/applications/:id', method: 'DELETE'},
            removeApplicationVersion: {url: 'rest/private/applications/versions/:id', method: 'DELETE'},
            turnIntoCommonApplication: {url: 'rest/private/applications/admin/common/:id', method: 'GET'},
            getConfigurations: {url: 'rest/private/applications/configurations/:id', method: 'GET'},
            getVersionConfigurations: {url: 'rest/private/applications/version/:id/configurations', method: 'GET'},
            updateApplicationConfigurations: {url: 'rest/private/applications/configurations', method: 'POST'},
            updateApplicationVersionConfigurations: {url: 'rest/private/applications/version/configurations', method: 'POST'}
        })
    })
    .factory('fileService', function ($resource) {
        return $resource('', {}, {
            getAllFiles: {url: 'rest/private/web-ui-files/search/:value', method: 'GET'},
            getApps: {url: 'rest/private/web-ui-files/apps/:value', method: 'GET'},
            removeFile: {url: 'rest/private/web-ui-files/remove', method: 'POST'},
            updateFile: {url: 'rest/private/web-ui-files/update', method: 'POST'},
            getLimit: {url: 'rest/private/web-ui-files/limit', method: 'GET'},
            getConfigurations: {url: 'rest/private/web-ui-files/configurations/:id', method: 'GET'},
            updateConfigurations: {url: 'rest/private/web-ui-files/configurations', method: 'POST'}
        })
    })
    .factory('storageService', function () {
        return {
            readableSize: function(size) {
                var r;
                if (size < 104857) {
                    r = 0.1;
                } else if (size < 10 * 1048576) {
                    r = Math.ceil(size / 104857.6) / 10;
                } else {
                    r = Math.ceil(size / 1048576);
                }
                return r;
            }
        }
    })
    .factory('photoService', function ($resource) {
        return $resource('', {}, {
            getAllPhotos: {url: 'rest/private/photos/all', method: 'GET'},
            getPhotosByDevice: {url: 'rest/private/photos/device/:deviceId', method: 'GET'},
            searchPhotos: {url: 'rest/private/photos/search', method: 'GET'},
            getPhotoById: {url: 'rest/private/photos/:id', method: 'GET'},
            updatePhoto: {url: 'rest/private/photos', method: 'PUT'},
            deletePhoto: {url: 'rest/private/photos/:id', method: 'DELETE'},
            requestPhotoUpload: {url: 'rest/private/photos/request/:deviceId', method: 'POST'},
            getPhotoUploadRequests: {url: 'rest/private/photos/requests', method: 'GET'}
        });
    })
    .factory('remoteControlService', function ($resource) {
        return $resource('', {}, {
            getAllSessions: {url: 'rest/private/remote-control/sessions', method: 'GET'},
            getSessionsByDevice: {url: 'rest/private/remote-control/sessions/device/:deviceId', method: 'GET'},
            getActiveSessionsByDevice: {url: 'rest/private/remote-control/sessions/device/:deviceId/active', method: 'GET'},
            getSessionById: {url: 'rest/private/remote-control/sessions/:id', method: 'GET'},
            getSessionByToken: {url: 'rest/private/remote-control/sessions/token/:token', method: 'GET'},
            createSession: {url: 'rest/private/remote-control/sessions', method: 'POST'},
            updateSession: {url: 'rest/private/remote-control/sessions/:id', method: 'PUT'},
            updateSessionStatus: {url: 'rest/private/remote-control/sessions/:id/status', method: 'PUT'},
            markSessionConnecting: {url: 'rest/private/remote-control/sessions/:id/connecting', method: 'PUT'},
            markSessionConnected: {url: 'rest/private/remote-control/sessions/:id/connected', method: 'PUT'},
            markSessionDisconnected: {url: 'rest/private/remote-control/sessions/:id/disconnected', method: 'PUT'},
            markSessionError: {url: 'rest/private/remote-control/sessions/:id/error', method: 'PUT'},
            deleteSession: {url: 'rest/private/remote-control/sessions/:id', method: 'DELETE'},
            hasActiveSessions: {url: 'rest/private/remote-control/sessions/device/:deviceId/has-active', method: 'GET'}
        });
    })
    .factory('contactService', function ($resource) {
        return $resource('', {}, {
            getContactsByDevice: {url: 'rest/private/device-contacts/device/:deviceId', method: 'GET'},
            getContactById: {url: 'rest/private/device-contacts/:id', method: 'GET'},
            createContact: {url: 'rest/private/device-contacts', method: 'POST'},
            updateContact: {url: 'rest/private/device-contacts/:id', method: 'PUT'},
            deleteContact: {url: 'rest/private/device-contacts/:id', method: 'DELETE'},
            searchContacts: {url: 'rest/private/device-contacts/search', method: 'GET'},
            syncContactsToDevice: {url: 'rest/private/device-contacts/sync/:deviceId', method: 'POST'},
            syncContactsToDevices: {url: 'rest/private/device-contacts/sync', method: 'POST'}
        });
    })
    .factory('commandService', function ($resource) {
        return $resource('', {}, {
            getCommands: {url: 'rest/private/commands/list', method: 'GET'},
            getCommandHistory: {url: 'rest/private/commands/history', method: 'GET'},
            executeCommand: {url: 'rest/private/commands/execute', method: 'POST'}
        });
    })
    .factory('networkFilterService', function ($resource) {
        return $resource('', {}, {
            getAllFilters: {url: 'rest/private/network-filter/list', method: 'GET'},
            getRules: {url: 'rest/private/network-filter/rules', method: 'GET'},
            saveFilter: {url: 'rest/private/network-filter/save', method: 'POST'},
            deleteFilter: {url: 'rest/private/network-filter/delete/:id', method: 'DELETE'},
            applyFilter: {url: 'rest/private/network-filter/apply', method: 'POST'},
            getWhitelist: {url: 'rest/private/network-filter/whitelist', method: 'GET'},
            updateWhitelist: {url: 'rest/private/network-filter/whitelist', method: 'PUT'}
        });
    })
    .factory('exportImportService', function ($resource) {
        return $resource('', {}, {
            getImportJobs: {url: 'rest/private/export-import/import/jobs', method: 'GET'},
            getExportJobs: {url: 'rest/private/export-import/export/jobs', method: 'GET'},
            deleteImportJob: {url: 'rest/private/export-import/import/jobs/:id', method: 'DELETE'},
            deleteExportJob: {url: 'rest/private/export-import/export/jobs/:id', method: 'DELETE'},
            downloadExport: {url: 'rest/private/export-import/export/download/:id', method: 'GET'},
            importDevices: {url: 'rest/private/export-import/import', method: 'POST'},
            exportDevices: {url: 'rest/private/export-import/export', method: 'POST'},
            getJobs: {url: 'rest/private/export-import/jobs', method: 'GET'},
            createJob: {url: 'rest/private/export-import/jobs', method: 'POST'},
            getJobStatus: {url: 'rest/private/export-import/jobs/:id', method: 'GET'},
            downloadTemplate: {url: 'rest/private/export-import/template', method: 'GET'}
        });
    })
    .factory('whiteLabelService', function ($resource) {
        return $resource('', {}, {
            getConfig: {url: 'rest/private/white-label/config', method: 'GET'},
            saveConfig: {url: 'rest/private/white-label/config', method: 'POST'},
            resetConfig: {url: 'rest/private/white-label/config/reset', method: 'POST'},
            generateMobileApp: {url: 'rest/private/white-label/generate', method: 'POST'},
            uploadLogo: {url: 'rest/private/white-label/upload/logo', method: 'POST'},
            uploadFavicon: {url: 'rest/private/white-label/upload/favicon', method: 'POST'},
            uploadAppIcon: {url: 'rest/private/white-label/upload/app-icon', method: 'POST'},
            uploadSplashScreen: {url: 'rest/private/white-label/upload/splash', method: 'POST'},
            getRebrandingSettings: {url: 'rest/private/white-label/rebranding', method: 'GET'},
            updateRebrandingSettings: {url: 'rest/private/white-label/rebranding', method: 'PUT'},
            getAllBuilds: {url: 'rest/private/white-label/builds', method: 'GET'},
            getBuildById: {url: 'rest/private/white-label/builds/:id', method: 'GET'},
            createBuild: {url: 'rest/private/white-label/builds', method: 'POST'},
            updateBuild: {url: 'rest/private/white-label/builds/:id', method: 'PUT'},
            deleteBuild: {url: 'rest/private/white-label/builds/:id', method: 'DELETE'},
            triggerBuild: {url: 'rest/private/white-label/builds/:id/trigger', method: 'POST'},
            getAllEmailTemplates: {url: 'rest/private/white-label/email-templates', method: 'GET'},
            getEmailTemplate: {url: 'rest/private/white-label/email-templates/:type', method: 'GET'},
            saveEmailTemplate: {url: 'rest/private/white-label/email-templates', method: 'PUT'},
            deleteEmailTemplate: {url: 'rest/private/white-label/email-templates/:id', method: 'DELETE'}
        });
    })
    .factory('summaryService', function ($resource) {
        return $resource('', {}, {
            getDeviceStat: {url: 'rest/private/summary/devices', method: 'GET'}
        });
    })
    .factory('appVersionComparisonService', function () {

        var mdmAppVersionComparisonIndex = function (versionText) {
            if (versionText === null || versionText === undefined || versionText.trim().length === 0) {
                return "-1000000";
            }

            var result = '';
            var parts = versionText.split('.');
            var N = parts.length;
            for (var i = 0; i < N; i++) {
                var part = parts[i].replace(/[^0-9]+/g, '');

                if (part.trim().length === 0) {
                    part = '0';
                }

                while (part.length < 10) {
                    part = '0' + part;
                }

                result += part;
            }

            return result;
        };

        return {
            compare: function (v1, v2) {
                var i1 = mdmAppVersionComparisonIndex(v1);
                var i2 = mdmAppVersionComparisonIndex(v2);

                if (i1 === i2) {
                    return 0;
                } else if (i1 < i2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    })
;
