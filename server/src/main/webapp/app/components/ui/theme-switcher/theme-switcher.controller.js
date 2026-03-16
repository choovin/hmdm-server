/**
 * Theme Switcher Component
 * Toggle between light/dark/auto themes
 */

angular.module('headwind-kiosk').controller('ThemeSwitcherController',
['$scope', 'themeService', function($scope, themeService) {

    $scope.currentTheme = themeService.getCurrentTheme();
    $scope.THEMES = themeService.THEMES;

    /**
     * Get icon for current theme
     */
    $scope.getThemeIcon = function() {
        switch($scope.currentTheme) {
            case themeService.THEMES.DARK:
                return 'dark_mode';
            case themeService.THEMES.LIGHT:
                return 'light_mode';
            default:
                return 'brightness_auto';
        }
    };

    /**
     * Get label for current theme
     */
    $scope.getThemeLabel = function() {
        switch($scope.currentTheme) {
            case themeService.THEMES.DARK:
                return '深色模式';
            case themeService.THEMES.LIGHT:
                return '浅色模式';
            default:
                return '跟随系统';
        }
    };

    /**
     * Toggle to next theme (light -> dark -> auto -> light)
     */
    $scope.cycleTheme = function() {
        var themes = [themeService.THEMES.LIGHT, themeService.THEMES.DARK, themeService.THEMES.AUTO];
        var currentIndex = themes.indexOf($scope.currentTheme);
        var nextIndex = (currentIndex + 1) % themes.length;
        var nextTheme = themes[nextIndex];

        themeService.setTheme(nextTheme);
        $scope.currentTheme = nextTheme;
    };

    /**
     * Set specific theme
     */
    $scope.setTheme = function(theme) {
        themeService.setTheme(theme);
        $scope.currentTheme = theme;
    };

    // Listen for theme changes from other components
    $scope.$on('themeChanged', function(event, theme) {
        $scope.currentTheme = theme;
    });
}])
.directive('themeSwitcher', function() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/ui/theme-switcher/theme-switcher.html',
        controller: 'ThemeSwitcherController',
        controllerAs: 'themeCtrl'
    };
});
