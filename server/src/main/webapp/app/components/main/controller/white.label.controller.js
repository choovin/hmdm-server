// Localization completed
angular.module('headwind-kiosk')
    .controller('WhiteLabelController', function ($scope, localization, whiteLabelService, alertService) {

        $scope.localization = localization;
        $scope.isLoading = false;
        $scope.isSaving = false;
        $scope.previewMode = false;

        // White label configuration
        $scope.config = {
            // Branding
            appName: 'Headwind MDM',
            companyName: '',
            supportEmail: '',
            supportPhone: '',
            websiteUrl: '',

            // Logo
            logoUrl: null,
            faviconUrl: null,
            loginLogoUrl: null,

            // Colors
            primaryColor: '#2196F3',
            secondaryColor: '#FFC107',
            accentColor: '#4CAF50',
            backgroundColor: '#FFFFFF',
            textColor: '#333333',

            // Custom CSS
            customCss: '',
            customJs: '',

            // Email templates
            emailSubjectPrefix: '',
            emailHeaderHtml: '',
            emailFooterHtml: '',

            // Mobile app
            mobileAppName: '',
            mobileAppIcon: null,
            splashScreen: null,

            // Features
            showPoweredBy: true,
            showHelpLinks: true,
            enableCustomDomain: false,
            customDomain: ''
        };

        // Default color presets
        $scope.colorPresets = [
            {name: 'Blue', primary: '#2196F3', secondary: '#FFC107', accent: '#4CAF50'},
            {name: 'Green', primary: '#4CAF50', secondary: '#FF9800', accent: '#2196F3'},
            {name: 'Purple', primary: '#9C27B0', secondary: '#00BCD4', accent: '#FF5722'},
            {name: 'Red', primary: '#F44336', secondary: '#FFEB3B', accent: '#03A9F4'},
            {name: 'Dark', primary: '#37474F', secondary: '#FFC107', accent: '#4CAF50'}
        ];

        // Load white label config
        $scope.loadConfig = function() {
            $scope.isLoading = true;
            whiteLabelService.getConfig({}, function(response) {
                $scope.isLoading = false;
                if (response.status === 'OK' && response.data) {
                    $scope.config = angular.extend($scope.config, response.data);
                }
            });
        };

        // Save white label config
        $scope.saveConfig = function() {
            $scope.isSaving = true;
            whiteLabelService.saveConfig($scope.config, function(response) {
                $scope.isSaving = false;
                if (response.status === 'OK') {
                    alertService.showSuccessMessage(
                        localization.localize('success.config.saved')
                    );
                } else {
                    alertService.showErrorMessage(
                        localization.localize(response.message || 'error.config.save')
                    );
                }
            });
        };

        // Apply color preset
        $scope.applyPreset = function(preset) {
            $scope.config.primaryColor = preset.primary;
            $scope.config.secondaryColor = preset.secondary;
            $scope.config.accentColor = preset.accent;
        };

        // Upload logo
        $scope.uploadLogo = function(files) {
            if (!files || files.length === 0) return;

            var file = files[0];
            whiteLabelService.uploadLogo({file: file}, function(response) {
                if (response.status === 'OK') {
                    $scope.config.logoUrl = response.data.url;
                }
            });
        };

        // Upload favicon
        $scope.uploadFavicon = function(files) {
            if (!files || files.length === 0) return;

            var file = files[0];
            whiteLabelService.uploadFavicon({file: file}, function(response) {
                if (response.status === 'OK') {
                    $scope.config.faviconUrl = response.data.url;
                }
            });
        };

        // Upload mobile app icon
        $scope.uploadAppIcon = function(files) {
            if (!files || files.length === 0) return;

            var file = files[0];
            whiteLabelService.uploadAppIcon({file: file}, function(response) {
                if (response.status === 'OK') {
                    $scope.config.mobileAppIcon = response.data.url;
                }
            });
        };

        // Upload splash screen
        $scope.uploadSplashScreen = function(files) {
            if (!files || files.length === 0) return;

            var file = files[0];
            whiteLabelService.uploadSplashScreen({file: file}, function(response) {
                if (response.status === 'OK') {
                    $scope.config.splashScreen = response.data.url;
                }
            });
        };

        // Reset to defaults
        $scope.resetToDefaults = function() {
            if (confirm(localization.localize('confirm.reset.whitelabel'))) {
                whiteLabelService.resetConfig({}, function(response) {
                    if (response.status === 'OK') {
                        $scope.loadConfig();
                        alertService.showSuccessMessage(
                            localization.localize('success.config.reset')
                        );
                    }
                });
            }
        };

        // Preview changes
        $scope.togglePreview = function() {
            $scope.previewMode = !$scope.previewMode;
        };

        // Generate mobile app
        $scope.generateMobileApp = function() {
            alertService.showInfoMessage(
                localization.localize('message.generating.app')
            );
            whiteLabelService.generateMobileApp({}, function(response) {
                if (response.status === 'OK') {
                    alertService.showSuccessMessage(
                        localization.localize('success.app.generated')
                    );
                    if (response.data.downloadUrl) {
                        window.open(response.data.downloadUrl, '_blank');
                    }
                }
            });
        };

        // Preview styles
        $scope.getPreviewStyles = function() {
            return {
                'background-color': $scope.config.backgroundColor,
                'color': $scope.config.textColor
            };
        };

        $scope.getPrimaryButtonStyles = function() {
            return {
                'background-color': $scope.config.primaryColor,
                'border-color': $scope.config.primaryColor
            };
        };

        $scope.getSecondaryButtonStyles = function() {
            return {
                'background-color': $scope.config.secondaryColor,
                'border-color': $scope.config.secondaryColor
            };
        };

        // Initialize
        $scope.loadConfig();
    });
