// Localization completed
angular.module('headwind-kiosk')
    .controller('TabController', function ($scope, $rootScope, $timeout, userService, authService, openTab, $state,
                                           pluginService, localization, hintService) {

        $scope.localization = localization;

        // Page titles for modern layout
        var pageTitles = {
            'SUMMARY': '概览',
            'DEVICES': '设备管理',
            'APPS': '应用管理',
            'CONFS': '配置管理',
            'FILES': '文件管理',
            'DESIGN': '默认设计',
            'COMMON': '通用设置',
            'USERS': '用户管理',
            'ROLES': '角色管理',
            'GROUPS': '分组管理',
            'ICONS': '图标管理',
            'LANG': '语言设置',
            'PLUGINS': '插件管理'
        };

        // View mode (table/card) - persist to localStorage
        const VIEW_MODE_KEY = 'hmdm-view-mode';
        $scope.viewMode = localStorage.getItem(VIEW_MODE_KEY) || 'table';

        var routes = {
            SUMMARY: 'summary',
            DEVICES: 'main',
            APPS: 'applications',
            CONFS: 'configurations',
            FILES: 'files',
            DESIGN: 'designSettings',
            COMMON: 'commonSettings',
            USERS: 'users',
            ROLES: 'roles',
            GROUPS: 'groups',
            ICONS: 'icons',
            LANG: 'langSettings',
            HINTS: 'hints',
            PLUGINS: 'pluginSettings'
        };

        var loadData = function () {
            pluginService.getAvailablePlugins(function (response) {
                if (response.status === 'OK') {
                    if (response.data) {
                        // Helper function to filter plugins by template property
                        function filterPlugins(plugins, templateProperty) {
                            return plugins.filter(function(plugin) {
                                return !!plugin[templateProperty];
                            });
                        }

                        // Plugins available for Functions tab
                        $scope.functionsPlugins = filterPlugins(response.data, 'functionsViewTemplate');
                        $scope.functionsPlugins.forEach(function (plugin) {
                            let ID = 'plugin-' + plugin.identifier;
                            routes[ID] = ID;
                        });

                        // Plugins available for Settings tab
                        $scope.settingsPlugins = filterPlugins(response.data, 'settingsViewTemplate');
                        $scope.settingsPlugins.forEach(function (plugin) {
                            let ID = 'plugin-settings-' + plugin.identifier;
                            routes[ID] = ID;
                        });
                    }
                } else {
                    $scope.functionsPlugins = [];
                    $scope.settingsPlugins = [];
                }
            });
        };

        $scope.currentUser = {};

        $scope.hasPermission = authService.hasPermission;
        $scope.canManageRoles = function() {
            return authService.isSingleCustomer() || authService.isSuperAdmin();
        };

        // Get page title for modern layout
        $scope.getPageTitle = function() {
            return pageTitles[openTab] || 'MDM Server';
        };

        // Check if current page should show view toggle
        $scope.showViewToggle = function() {
            return openTab === 'DEVICES' || openTab === 'APPS' || openTab === 'CONFS' ||
                   openTab === 'FILES' || openTab === 'USERS' || openTab === 'GROUPS';
        };

        // Handle view mode change
        $scope.onViewModeChange = function(mode) {
            $scope.viewMode = mode;
            localStorage.setItem(VIEW_MODE_KEY, mode);
            $rootScope.$broadcast('viewModeChanged', mode);
        };

        $scope.activeTab = openTab;

        $scope.act = {};
        $scope.act[openTab] = true;

        $scope.functionsPlugins = [];
        $scope.settingsPlugins = [];

        $scope.openTab = function (tabName) {
            if (tabName === $scope.activeTab) {
                return;
            }
            if (routes[tabName]) {
                $state.transitionTo(routes[tabName]);
            }
        };

        var listener = $scope.$on('aero_PLUGINS_UPDATED', loadData);
        $scope.$on('$destroy', listener);

        userService.getCurrent(function (response) {
            if (response.data) {
                $scope.currentUser = response.data;
            }
        });

        // hintService start is fired by the controllers themselves after they are loaded all required content
//        $timeout(function () {
//            hintService.onStateChangeSuccess();
//        }, 100);

        loadData();
    });
