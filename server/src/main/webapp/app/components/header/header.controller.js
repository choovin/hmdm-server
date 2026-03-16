// Localization completed
angular.module( 'headwind-kiosk' )
.controller( 'HeaderController', function( $scope, $rootScope, $state, $modal, $timeout, $interval, $filter, $window,
                                           authService, localization, hintService, rebranding, alertService, Idle ) {
    $scope.isControlPanel = false;
    $scope.authService = authService;
    $scope.showExitReportMode = false;

    // Store event listener references for cleanup
    var listeners = [];

    // Page titles mapping for modern layout - using localized strings
    var pageTitleKeys = {
        'summary': 'tab.summary.title',
        'main': 'tab.devices.title',
        'applications': 'tab.applications.title',
        'configurations': 'tab.configurations.title',
        'files': 'tab.files.title',
        'designSettings': 'tab.design.title',
        'commonSettings': 'tab.common.title',
        'users': 'tab.users.title',
        'roles': 'tab.roles.title',
        'groups': 'tab.groups.title',
        'icons': 'tab.icons.title',
        'langSettings': 'tab.language.title',
        'pluginSettings': 'tab.plugins.title',
        'profile': 'profile.title',
        'updates': 'updates.title',
        'control-panel': 'control.panel.title'
    };

    $scope.getPageTitle = function() {
        var key = pageTitleKeys[$state.current.name];
        if (key) {
            var localized = localization.localize(key);
            if (localized !== key) {
                return localized;
            }
        }

        // Check for plugin states
        if ($state.current.name && $state.current.name.indexOf('plugin-') === 0) {
            return localization.localize('plugin.functions') || '插件功能';
        }

        return localization.localize('app.name') || 'MDM Server';
    };

    $scope.toggleSidebar = function() {
        $rootScope.$broadcast('sidebarToggle');
    };

    listeners.push($scope.$on( 'START_REPORT_MODE', function() {
        $scope.showExitReportMode = true;
    } ));

    listeners.push($scope.$on( 'HIDE_REPORT_MODE', function() {
        $scope.showExitReportMode = false;
    } ));

    listeners.push($scope.$on( 'HIDE_ADDRESS', function() {
        $scope.mapToolsConfig.showDeviceAddress = false;
    } ));

    listeners.push($scope.$on( 'SHOW_CHECKLIST_INFO', function( event, checklistId ) {
        showWorkResultsContent( checklistId );
    } ));

    listeners.push($scope.$on( 'SHOW_DATA_LOADING_MODAL', function() {
        $scope.dataLoadingWait = true;
    } ));

    listeners.push($scope.$on( 'HIDE_DATA_LOADING_MODAL', function() {
        $scope.dataLoadingWait = false;
    } ));

    listeners.push($scope.$on('IdleWarn', function(e, countdown) {
        if (!$scope.logoutAlert) {
            $scope.logoutAlert = alertService.showAlertMessage(
                localization.localize('idle.logout.message').replace('${sec}', countdown),
                function() {
                    $scope.logoutAlert = null;
                },
                localization.localize('idle.logout.resume'));
        } else {
            // How to update the logoutAlert contents??
        }
    }));

    listeners.push($scope.$on('IdleTimeout', function() {
        $scope.logoutAlert.close();
        $scope.logoutAlert = null;
        $scope.logout();
    }));

    listeners.push($rootScope.$on( 'SHOW_EXPIRY_WARNING', function() {
        $scope.expiryWarning = true;
    } ));

    rebranding.query(function(value) {
        $scope.appName = value.appName;
    }, function() {
        // Error handling
        $scope.appName = 'MDM';
    });

    updateDateTime = function() {
        $scope.dateTime = $filter( 'date' )( new Date(), localization.localize('format.date.header') );
    };
    updateDateTime();

    var interval = $interval( updateDateTime, 10000 );

    // Pause interval when tab is hidden to save resources
    var visibilityHandler = function() {
        if (document.hidden) {
            $interval.cancel(interval);
        } else {
            updateDateTime();
            interval = $interval(updateDateTime, 10000);
        }
    };
    document.addEventListener('visibilitychange', visibilityHandler);

    // Cleanup on destroy
    $scope.$on('$destroy', function() {
        // Cancel interval
        $interval.cancel(interval);
        // Remove visibility listener
        document.removeEventListener('visibilitychange', visibilityHandler);
        // Unregister all scope listeners
        listeners.forEach(function(listener) { listener(); });
    });

    // Cache URL check result - document.URL rarely changes
    var urlHasInvoice = document.URL.indexOf('invoice') !== -1;

    $scope.getUserName = function() { return authService.getUserName(); };
    $scope.isAuth = function() { return authService.isLoggedIn() && !urlHasInvoice; };
    $scope.isHidden = function() {
        return $state.current.name === 'qr' || $state.current.name === 'passwordReset';
    };

    $scope.isSuperAdmin = function() {
        return authService.isSuperAdmin();
    };
    $scope.updatesAllowed = function() {
        return (authService.isSingleCustomer() || authService.isSuperAdmin()) && authService.hasPermission('get_updates');
    };

    $scope.logout = function() {
        authService.logout();
        hintService.onLogout();
        $state.transitionTo( 'login' );
        $rootScope.$emit('aero_USER_LOGOUT');
    };

    $scope.isActive = function( state ) {
        return $state.$current.self.name === state;
    };

    $scope.controlPanel = function() {
        $state.transitionTo( 'control-panel' );
        $scope.isControlPanel = true;
    };

    $scope.mainPanel = function() {
        $state.transitionTo( 'main' );
        $scope.isControlPanel = false;
    };

    $scope.about = function () {
        $modal.open({
            templateUrl: 'app/components/about/about.html',
            controller: 'AboutController'
        });
    };
} );
