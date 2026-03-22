// Localization completed
angular.module( 'headwind-kiosk' )
.controller( 'HeaderController', function( $scope, $rootScope, $state, $uibModal, $timeout, $interval, $filter, $window,
                                           authService, localization, hintService, rebranding, alertService, Idle,
                                           pluginService, userService ) {
    $scope.isControlPanel = false;
    $scope.authService = authService;
    $scope.showExitReportMode = false;

    // Tab navigation properties
    $scope.activeTab = 'DEVICES';
    $scope.functionsPlugins = [];
    $scope.settingsPlugins = [];

    // Route mappings for tab navigation
    var tabRoutes = {
        'SUMMARY': 'summary',
        'DEVICES': 'main',
        'APPS': 'applications',
        'CONFS': 'configurations',
        'FILES': 'files',
        'LOCATIONS': 'locations',
        'CONTACTS': 'contacts',
        'LDAP': 'ldap',
        'REMOTECONTROL': 'remotecontrol',
        'COMMANDS': 'commands',
        'NETWORKFILTER': 'networkfilter',
        'EXPORTIMPORT': 'exportimport',
        'WHITELABEL': 'whitelabel',
        'DESIGN': 'designSettings',
        'COMMON': 'commonSettings',
        'USERS': 'users',
        'ROLES': 'roles',
        'GROUPS': 'groups',
        'ICONS': 'icons',
        'LANG': 'langSettings',
        'PLUGINS': 'pluginSettings'
    };

    // Settings tab names for active state check
    var settingsTabs = ['DESIGN', 'COMMON', 'USERS', 'ROLES', 'GROUPS', 'ICONS', 'LANG', 'PLUGINS'];

    // Permission check function
    $scope.hasPermission = function(permission) {
        return authService.hasPermission(permission);
    };

    // Open tab function
    $scope.openTab = function(tabName) {
        if (tabName === $scope.activeTab) {
            return;
        }

        // Update active tab
        $scope.activeTab = tabName;

        // Update settings dropdown active state
        $scope.settingsTabActive = settingsTabs.indexOf(tabName) !== -1;

        // Update plugins dropdown active state
        $scope.pluginsTabActive = tabName.indexOf('plugin-') === 0;

        // Navigate to route if exists
        if (tabRoutes[tabName]) {
            $state.transitionTo(tabRoutes[tabName]);
        } else if (tabName.indexOf('plugin-') === 0) {
            $state.transitionTo(tabName);
        }
    };

    // Load plugins for dropdown menus
    var loadPlugins = function() {
        pluginService.getAvailablePlugins(function(response) {
            if (response.status === 'OK' && response.data) {
                // Filter function plugins
                $scope.functionsPlugins = response.data.filter(function(plugin) {
                    return !!plugin.functionsViewTemplate;
                });

                // Filter settings plugins
                $scope.settingsPlugins = response.data.filter(function(plugin) {
                    return !!plugin.settingsViewTemplate;
                });

                // Register plugin routes
                $scope.functionsPlugins.forEach(function(plugin) {
                    var id = 'plugin-' + plugin.identifier;
                    tabRoutes[id] = id;
                });

                $scope.settingsPlugins.forEach(function(plugin) {
                    var id = 'plugin-settings-' + plugin.identifier;
                    tabRoutes[id] = id;
                });
            }
        });
    };

    // Sync active tab with current state
    var syncActiveTabWithState = function() {
        var currentState = $state.current.name;

        // Find tab by route
        for (var tab in tabRoutes) {
            if (tabRoutes[tab] === currentState) {
                $scope.activeTab = tab;
                $scope.settingsTabActive = settingsTabs.indexOf(tab) !== -1;
                $scope.pluginsTabActive = tab.indexOf('plugin-') === 0;
                return;
            }
        }

        // Handle plugin states
        if (currentState && currentState.indexOf('plugin-') === 0) {
            $scope.activeTab = currentState;
            $scope.pluginsTabActive = true;
        }
    };

    // Initialize
    loadPlugins();
    syncActiveTabWithState();

    // Load current user
    $scope.currentUser = {};
    userService.getCurrent(function(response) {
        if (response.data) {
            $scope.currentUser = response.data;
        }
    });

    // Store event listener references for cleanup
    var listeners = [];

    // Listen for state changes to sync active tab
    listeners.push($scope.$on('$stateChangeSuccess', syncActiveTabWithState));
    listeners.push($scope.$on('aero_PLUGINS_UPDATED', loadPlugins));

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
        $uibModal.open({
            templateUrl: 'app/components/about/about.html',
            controller: 'AboutController'
        });
    };
} );
