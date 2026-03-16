/**
 * Theme Service - Manage light/dark theme switching
 * Features:
 * - Auto-detect system preference
 * - Manual toggle
 * - Persist user preference to localStorage
 * - Apply theme by setting data-theme attribute
 */

angular.module('headwind-kiosk').service('themeService', ['$rootScope', function($rootScope) {

    const STORAGE_KEY = 'hmdm-theme-preference';

    // Theme constants
    const THEMES = {
        LIGHT: 'light',
        DARK: 'dark',
        AUTO: 'auto'
    };

    /**
     * Initialize theme on app startup
     */
    function init() {
        const savedTheme = localStorage.getItem(STORAGE_KEY);

        if (savedTheme && savedTheme !== THEMES.AUTO) {
            applyTheme(savedTheme);
        } else {
            // Auto-detect system preference
            const systemPrefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
            applyTheme(systemPrefersDark ? THEMES.DARK : THEMES.LIGHT);
        }

        // Listen for system theme changes
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', function(e) {
            const currentPref = localStorage.getItem(STORAGE_KEY);
            if (!currentPref || currentPref === THEMES.AUTO) {
                applyTheme(e.matches ? THEMES.DARK : THEMES.LIGHT);
            }
        });
    }

    /**
     * Apply theme to document
     */
    function applyTheme(theme) {
        const html = document.documentElement;

        if (theme === THEMES.DARK) {
            html.setAttribute('data-theme', 'dark');
        } else {
            html.removeAttribute('data-theme');
        }

        // Update body class for scoped styles
        const body = document.body;
        if (theme === THEMES.DARK) {
            body.classList.add('dark-theme');
            body.classList.remove('light-theme');
        } else {
            body.classList.add('light-theme');
            body.classList.remove('dark-theme');
        }

        // Broadcast theme change
        $rootScope.$broadcast('themeChanged', theme);
    }

    /**
     * Set theme and save preference
     */
    function setTheme(theme) {
        if (theme === THEMES.AUTO) {
            localStorage.removeItem(STORAGE_KEY);
            const systemPrefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
            applyTheme(systemPrefersDark ? THEMES.DARK : THEMES.LIGHT);
        } else {
            localStorage.setItem(STORAGE_KEY, theme);
            applyTheme(theme);
        }
    }

    /**
     * Get current theme
     */
    function getCurrentTheme() {
        const saved = localStorage.getItem(STORAGE_KEY);
        if (saved) return saved;

        const hasDarkAttr = document.documentElement.hasAttribute('data-theme');
        return hasDarkAttr ? THEMES.DARK : THEMES.LIGHT;
    }

    /**
     * Toggle between light and dark
     */
    function toggle() {
        const current = getCurrentTheme();
        const next = current === THEMES.DARK ? THEMES.LIGHT : THEMES.DARK;
        setTheme(next);
        return next;
    }

    // Public API
    return {
        THEMES: THEMES,
        init: init,
        setTheme: setTheme,
        getCurrentTheme: getCurrentTheme,
        toggle: toggle
    };
}])
.run(['themeService', function(themeService) {
    // Initialize theme on app startup
    themeService.init();
}]);
