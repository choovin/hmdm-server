// Localization completed
/**
 * 标签页控制器
 * 负责管理主界面的标签页导航，包括原生的10个Enterprise功能
 */
angular.module('headwind-kiosk')
    .controller('TabController', function ($scope, $rootScope, $timeout, userService, authService, openTab, $state,
                                           pluginService, localization, hintService) {

        $scope.localization = localization;

        // 路由映射：将标签名称映射到AngularJS状态
        var routes = {
            SUMMARY: 'summary',
            DEVICES: 'main',
            APPS: 'applications',
            CONFS: 'configurations',
            FILES: 'files',
            LOCATIONS: 'locations',
            CONTACTS: 'contacts',
            LDAP: 'ldap',
            REMOTECONTROL: 'remotecontrol',
            COMMANDS: 'commands',
            NETWORKFILTER: 'networkfilter',
            EXPORTIMPORT: 'exportimport',
            WHITELABEL: 'whitelabel',
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

        // 加载可用插件列表
        var loadData = function () {
            pluginService.getAvailablePlugins(function (response) {
                if (response.status === 'OK') {
                    if (response.data) {
                        // 辅助函数：根据模板属性过滤插件
                        function filterPlugins(plugins, templateProperty) {
                            return plugins.filter(function(plugin) {
                                return !!plugin[templateProperty];
                            });
                        }

                        // 功能插件（Functions标签页）
                        $scope.functionsPlugins = filterPlugins(response.data, 'functionsViewTemplate');
                        $scope.functionsPlugins.forEach(function (plugin) {
                            let ID = 'plugin-' + plugin.identifier;
                            routes[ID] = ID;
                        });

                        // 设置插件（Settings标签页）
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

        // 权限检查
        $scope.hasPermission = authService.hasPermission;

        // 检查是否可以管理角色
        $scope.canManageRoles = function() {
            return authService.isSingleCustomer() || authService.isSuperAdmin();
        };

        // 当前激活的标签
        $scope.activeTab = openTab;

        // 标签激活状态映射
        $scope.act = {};
        $scope.act[openTab] = true;

        // 初始化插件数组
        $scope.functionsPlugins = [];
        $scope.settingsPlugins = [];

        // 打开标签页
        $scope.openTab = function (tabName) {
            if (tabName === $scope.activeTab) {
                return;
            }
            if (routes[tabName]) {
                $state.transitionTo(routes[tabName]);
            }
        };

        // 监听插件更新事件
        var listener = $scope.$on('aero_PLUGINS_UPDATED', loadData);
        $scope.$on('$destroy', listener);

        // 获取当前用户信息
        userService.getCurrent(function (response) {
            if (response.data) {
                $scope.currentUser = response.data;
            }
        });

        // 初始化加载数据
        loadData();
    });