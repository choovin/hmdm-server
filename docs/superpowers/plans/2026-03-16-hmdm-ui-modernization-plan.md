# HMDM UI 现代化改造实施计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 对 HMDM 管理后台进行现代化 UI/UX 改造，实现国际化中文默认显示，并按照现代互联网产品设计标准重新设计界面交互。

**Architecture:** 基于现有 AngularJS 1.x + Bootstrap 技术栈，通过 CSS 变量系统、组件化改造、布局重构实现现代化 UI。保持所有后端 API 不变，仅调整前端展示层。

**Tech Stack:** AngularJS 1.x, Bootstrap 3/4, Google Material Icons, CSS Variables, localStorage

---

## 文件结构总览

### 新增文件
```
server/src/main/webapp/
├── css/
│   ├── modern-theme.css          # 现代主题样式（浅色模式）
│   ├── dark-theme.css            # 深色模式样式
│   ├── sidebar.css               # 侧边栏组件样式
│   └── utilities.css             # 工具类样式
├── app/components/ui/
│   ├── sidebar/
│   │   ├── sidebar.html          # 侧边栏模板
│   │   └── sidebar.controller.js # 侧边栏控制器
│   ├── theme-switcher/
│   │   ├── theme-switcher.html
│   │   └── theme-switcher.controller.js
│   ├── view-toggle/
│   │   ├── view-toggle.html
│   │   └── view-toggle.controller.js
│   └── search-filter/
│       ├── search-filter.html
│       └── search-filter.controller.js
└── app/shared/service/
    └── theme.service.js          # 主题管理服务
```

### 修改文件
```
server/src/main/webapp/
├── index.html                    # 引入新样式和组件
├── app/
│   ├── app.module.js            # 注册新组件
│   ├── components/main/
│   │   ├── view/content.html    # 重构布局
│   │   ├── view/main.html       # 调整主容器
│   │   └── controller/main.controller.js
│   └── shared/
│       └── localization/
│           └── localization.js  # 修改默认语言逻辑
└── localization/
    └── zh_CN.js                 # 补充翻译
```

---

## Chunk 1: 基础设施和主题系统

### Task 1.1: 创建现代主题 CSS 变量系统

**Files:**
- Create: `server/src/main/webapp/css/modern-theme.css`
- Modify: `server/src/main/webapp/index.html`

**Description:**
建立 CSS 变量系统，定义颜色、间距、阴影等设计令牌，为后续组件提供一致的样式基础。

- [ ] **Step 1: 创建现代主题 CSS 文件**

Create: `server/src/main/webapp/css/modern-theme.css`

```css
/*
 * HMDM Modern Theme - CSS Variables
 * Primary: Technology Blue (#2563EB)
 */

:root {
  /* Primary Colors */
  --color-primary: #2563EB;
  --color-primary-dark: #1E40AF;
  --color-primary-light: #93C5FD;
  --color-primary-50: #EFF6FF;
  --color-primary-100: #DBEAFE;
  --color-primary-200: #BFDBFE;

  /* Neutral Colors */
  --color-bg: #F8FAFC;
  --color-surface: #FFFFFF;
  --color-surface-hover: #F1F5F9;
  --color-border: #E2E8F0;
  --color-border-light: #F1F5F9;

  /* Text Colors */
  --text-primary: #1E293B;
  --text-secondary: #64748B;
  --text-tertiary: #94A3B8;
  --text-inverse: #FFFFFF;

  /* Status Colors */
  --color-success: #10B981;
  --color-warning: #F59E0B;
  --color-error: #EF4444;
  --color-info: #3B82F6;

  /* Shadows */
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1);

  /* Spacing */
  --space-1: 0.25rem;  /* 4px */
  --space-2: 0.5rem;   /* 8px */
  --space-3: 0.75rem;  /* 12px */
  --space-4: 1rem;     /* 16px */
  --space-5: 1.25rem;  /* 20px */
  --space-6: 1.5rem;   /* 24px */
  --space-8: 2rem;     /* 32px */
  --space-10: 2.5rem;  /* 40px */

  /* Border Radius */
  --radius-sm: 4px;
  --radius: 6px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-xl: 16px;
  --radius-full: 9999px;

  /* Typography */
  --font-sans: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  --font-mono: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;

  /* Sidebar */
  --sidebar-width: 220px;
  --sidebar-width-collapsed: 64px;
  --sidebar-bg: #FFFFFF;
  --sidebar-border: #E2E8F0;

  /* Header */
  --header-height: 64px;
  --header-bg: #FFFFFF;
  --header-border: #E2E8F0;

  /* Transitions */
  --transition-fast: 150ms ease;
  --transition-base: 200ms ease;
  --transition-slow: 300ms ease;
}

/* Global Reset & Base */
.modern-ui {
  font-family: var(--font-sans);
  color: var(--text-primary);
  background-color: var(--color-bg);
}

.modern-ui * {
  box-sizing: border-box;
}

/* Material Icons Base */
.material-icons {
  font-family: 'Material Icons';
  font-weight: normal;
  font-style: normal;
  font-size: 24px;
  line-height: 1;
  letter-spacing: normal;
  text-transform: none;
  display: inline-block;
  white-space: nowrap;
  word-wrap: normal;
  direction: ltr;
  -webkit-font-feature-settings: 'liga';
  -webkit-font-smoothing: antialiased;
}
```

- [ ] **Step 2: 在 index.html 中引入新样式**

Modify: `server/src/main/webapp/index.html`

在 `<head>` 中添加（在现有 css 之后）：
```html
<!-- Modern Theme -->
<link rel="stylesheet" href="css/modern-theme.css">
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
```

- [ ] **Step 3: Commit**

```bash
git add server/src/main/webapp/css/modern-theme.css server/src/main/webapp/index.html
git commit -m "feat: add modern theme CSS variables system"
```

---

### Task 1.2: 创建深色模式样式

**Files:**
- Create: `server/src/main/webapp/css/dark-theme.css`

**Description:**
创建深色模式 CSS 变量覆盖，支持浅色/深色主题切换。

- [ ] **Step 1: 创建深色模式 CSS**

Create: `server/src/main/webapp/css/dark-theme.css`

```css
/*
 * HMDM Dark Theme
 * Automatically applied when [data-theme="dark"] is set on html element
 */

[data-theme="dark"] {
  /* Background Colors */
  --color-bg: #0F172A;
  --color-surface: #1E293B;
  --color-surface-hover: #334155;
  --color-border: #334155;
  --color-border-light: #1E293B;

  /* Text Colors */
  --text-primary: #F1F5F9;
  --text-secondary: #94A3B8;
  --text-tertiary: #64748B;
  --text-inverse: #0F172A;

  /* Sidebar */
  --sidebar-bg: #1E293B;
  --sidebar-border: #334155;

  /* Header */
  --header-bg: #1E293B;
  --header-border: #334155;

  /* Shadows (darker for dark mode) */
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.3);
  --shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.4), 0 1px 2px -1px rgba(0, 0, 0, 0.4);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.4), 0 2px 4px -2px rgba(0, 0, 0, 0.4);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.4), 0 4px 6px -4px rgba(0, 0, 0, 0.4);
}

/* Dark mode specific adjustments */
[data-theme="dark"] .modern-ui {
  background-color: var(--color-bg);
  color: var(--text-primary);
}

[data-theme="dark"] .card {
  background-color: var(--color-surface);
  border-color: var(--color-border);
}

[data-theme="dark"] input,
[data-theme="dark"] select,
[data-theme="dark"] textarea {
  background-color: var(--color-surface);
  border-color: var(--color-border);
  color: var(--text-primary);
}

[data-theme="dark"] input:focus,
[data-theme="dark"] select:focus,
[data-theme="dark"] textarea:focus {
  border-color: var(--color-primary);
}
```

- [ ] **Step 2: 在 index.html 中引入深色模式样式**

Modify: `server/src/main/webapp/index.html`

在 modern-theme.css 之后添加：
```html
<link rel="stylesheet" href="css/dark-theme.css">
```

- [ ] **Step 3: Commit**

```bash
git add server/src/main/webapp/css/dark-theme.css server/src/main/webapp/index.html
git commit -m "feat: add dark theme support"
```

---

### Task 1.3: 创建主题管理服务

**Files:**
- Create: `server/src/main/webapp/app/shared/service/theme.service.js`
- Modify: `server/src/main/webapp/app/app.module.js`

**Description:**
创建 AngularJS 服务管理主题切换，支持自动检测系统主题、手动切换、记忆用户偏好。

- [ ] **Step 1: 创建主题服务**

Create: `server/src/main/webapp/app/shared/service/theme.service.js`

```javascript
/**
 * Theme Service - Manage light/dark theme switching
 * Features:
 * - Auto-detect system preference
 * - Manual toggle
 * - Persist user preference to localStorage
 * - Apply theme by setting data-theme attribute
 */

angular.module('hmdm').service('themeService', ['$rootScope', function($rootScope) {

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
```

- [ ] **Step 2: Commit**

```bash
git add server/src/main/webapp/app/shared/service/theme.service.js
git commit -m "feat: add theme management service with auto-detect and persistence"
```

---

## Chunk 2: 默认语言改为中文

### Task 2.1: 修改国际化默认语言逻辑

**Files:**
- Modify: `server/src/main/webapp/app/shared/localization/localization.js`

**Description:**
修改语言检测优先级，将默认回退语言由英文改为中文，实现智能检测：用户设置 > 浏览器语言 > 中文。

- [ ] **Step 1: 读取当前 localization.js 文件**

```bash
cat server/src/main/webapp/app/shared/localization/localization.js
```

- [ ] **Step 2: 修改语言检测逻辑**

Find the language detection logic and modify it.

Typical pattern to look for:
```javascript
// Current logic might be:
// 1. Check user preference
// 2. Check browser language
// 3. Fallback to 'en_US' or 'en'

// Change fallback to 'zh_CN'
```

- [ ] **Step 3: 添加语言优先级配置**

Modify: `server/src/main/webapp/app/shared/localization/localization.js`

Add at the beginning of the service:
```javascript
// Language priority configuration
// Order: User preference > Browser language > Default (Chinese)
var LANGUAGE_PRIORITY = ['zh_CN', 'en', 'ru', 'pt', 'es', 'fr', 'de', 'pl', 'ar', 'ja', 'ko'];
var DEFAULT_LANGUAGE = 'zh_CN';  // Changed from 'en' or 'en_US'
```

Find the `getDefaultLanguage()` or similar function and modify:
```javascript
function getDefaultLanguage() {
    // Check if user has set a preference
    var userLang = localStorage.getItem('hmdm-language');
    if (userLang && isLanguageSupported(userLang)) {
        return userLang;
    }

    // Check browser language
    var browserLang = navigator.language || navigator.userLanguage;
    if (browserLang) {
        // Map browser lang to supported lang
        var mapped = mapBrowserLanguage(browserLang);
        if (mapped && isLanguageSupported(mapped)) {
            return mapped;
        }
    }

    // Default to Chinese instead of English
    return DEFAULT_LANGUAGE;
}
```

- [ ] **Step 4: Commit**

```bash
git add server/src/main/webapp/app/shared/localization/localization.js
git commit -m "feat: change default language to Chinese (zh_CN)"
```

---

### Task 2.2: 补充中文翻译

**Files:**
- Modify: `server/src/main/webapp/localization/zh_CN.js`

**Description:**
检查并补充 zh_CN.js 中的翻译，确保覆盖所有界面文本。

- [ ] **Step 1: 对比中英文翻译文件**

Compare `localization/en.js` or `localization/en_US.js` with `localization/zh_CN.js` to find missing keys.

- [ ] **Step 2: 补充缺失的中文翻译**

Add any missing translations to `zh_CN.js`. Example additions:
```javascript
// Add if missing
'table.view.card': '卡片视图',
'table.view.table': '表格视图',
'theme.light': '浅色模式',
'theme.dark': '深色模式',
'theme.auto': '跟随系统',
'sidebar.collapse': '收起菜单',
'tidebar.expand': '展开菜单',
'search.placeholder': '搜索...',
'filter.advanced': '高级筛选',
'filter.clear': '清除筛选',
```

- [ ] **Step 3: Commit**

```bash
git add server/src/main/webapp/localization/zh_CN.js
git commit -m "feat: complete Chinese translations"
```

---

## Chunk 3: 侧边栏组件

### Task 3.1: 创建侧边栏控制器

**Files:**
- Create: `server/src/main/webapp/app/components/ui/sidebar/sidebar.controller.js`

**Description:**
创建可折叠侧边栏控制器，支持展开/收起、菜单项管理、活动状态跟踪。

- [ ] **Step 1: 创建侧边栏控制器**

Create: `server/src/main/webapp/app/components/ui/sidebar/sidebar.controller.js`

```javascript
/**
 * Sidebar Controller
 * Features:
 * - Collapsible sidebar (expanded/collapsed)
 * - Menu item management
 * - Active state tracking
 * - Persist collapsed state to localStorage
 */

angular.module('hmdm').controller('SidebarController',
['$scope', '$location', '$rootScope',
function($scope, $location, $rootScope) {

    const STORAGE_KEY = 'hmdm-sidebar-collapsed';

    // Sidebar state
    $scope.isCollapsed = false;
    $scope.activeMenu = '';

    // Menu structure
    $scope.menuItems = [
        {
            id: 'devices',
            icon: 'smartphone',
            label: 'menu.devices',
            route: '/devices',
            permission: 'devices'
        },
        {
            id: 'applications',
            icon: 'apps',
            label: 'menu.applications',
            route: '/applications',
            permission: 'applications'
        },
        {
            id: 'configurations',
            icon: 'settings_applications',
            label: 'menu.configurations',
            route: '/configurations',
            permission: 'configurations'
        },
        {
            id: 'files',
            icon: 'folder',
            label: 'menu.files',
            route: '/files',
            permission: 'files'
        },
        {
            id: 'users',
            icon: 'people',
            label: 'menu.users',
            route: '/settings/users',
            permission: 'settings'
        },
        {
            id: 'groups',
            icon: 'account_tree',
            label: 'menu.groups',
            route: '/settings/groups',
            permission: null
        },
        {
            id: 'plugins',
            icon: 'extension',
            label: 'menu.plugins',
            route: '/plugins',
            permission: 'plugins_customer_access_management'
        }
    ];

    /**
     * Initialize sidebar
     */
    function init() {
        // Load collapsed state from storage
        var savedState = localStorage.getItem(STORAGE_KEY);
        if (savedState !== null) {
            $scope.isCollapsed = savedState === 'true';
        }

        // Set active menu based on current route
        updateActiveMenu();

        // Listen for route changes
        $rootScope.$on('$locationChangeSuccess', function() {
            updateActiveMenu();
        });
    }

    /**
     * Update active menu based on current route
     */
    function updateActiveMenu() {
        var path = $location.path();

        // Find matching menu item
        var activeItem = $scope.menuItems.find(function(item) {
            return path.indexOf(item.route) !== -1;
        });

        $scope.activeMenu = activeItem ? activeItem.id : '';
    }

    /**
     * Toggle sidebar collapsed state
     */
    $scope.toggleSidebar = function() {
        $scope.isCollapsed = !$scope.isCollapsed;
        localStorage.setItem(STORAGE_KEY, $scope.isCollapsed);

        // Broadcast event for other components
        $rootScope.$broadcast('sidebarToggle', $scope.isCollapsed);
    };

    /**
     * Navigate to menu item
     */
    $scope.navigate = function(item) {
        if (item.route) {
            $location.path(item.route);
        }
    };

    /**
     * Check if menu item is active
     */
    $scope.isActive = function(itemId) {
        return $scope.activeMenu === itemId;
    };

    /**
     * Check if user has permission for menu item
     */
    $scope.hasPermission = function(permission) {
        if (!permission) return true;
        // Delegate to existing permission check
        return $scope.$parent.hasPermission ? $scope.$parent.hasPermission(permission) : true;
    };

    // Initialize
    init();
}])
.directive('hmdmSidebar', function() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/ui/sidebar/sidebar.html',
        controller: 'SidebarController',
        controllerAs: 'sidebarCtrl',
        scope: {
            // Optional: pass in custom menu items
            customMenuItems: '=?'
        }
    };
});
```

- [ ] **Step 2: Commit**

```bash
git add server/src/main/webapp/app/components/ui/sidebar/sidebar.controller.js
git commit -m "feat: add sidebar controller with collapse/expand functionality"
```

---

### Task 3.2: 创建侧边栏模板

**Files:**
- Create: `server/src/main/webapp/app/components/ui/sidebar/sidebar.html`
- Create: `server/src/main/webapp/css/sidebar.css`

**Description:**
创建侧边栏 HTML 模板和样式。

- [ ] **Step 1: 创建侧边栏模板**

Create: `server/src/main/webapp/app/components/ui/sidebar/sidebar.html`

```html
<!-- Sidebar Component -->
<aside class="sidebar" ng-class="{'collapsed': isCollapsed}">
    <!-- Logo Area -->
    <div class="sidebar-header">
        <div class="sidebar-logo" ng-if="!isCollapsed">
            <span class="logo-icon material-icons">phonelink_setup</span>
            <span class="logo-text">HMDM</span>
        </div>
        <div class="sidebar-logo collapsed" ng-if="isCollapsed">
            <span class="logo-icon material-icons">phonelink_setup</span>
        </div>
        <button class="sidebar-toggle" ng-click="toggleSidebar()" title="{{isCollapsed ? '展开菜单' : '收起菜单'}}">
            <span class="material-icons">{{isCollapsed ? 'chevron_right' : 'chevron_left'}}</span>
        </button>
    </div>

    <!-- Menu Items -->
    <nav class="sidebar-nav">
        <ul class="sidebar-menu">
            <li ng-repeat="item in menuItems"
                ng-if="hasPermission(item.permission)"
                class="sidebar-menu-item"
                ng-class="{'active': isActive(item.id)}">
                <a ng-click="navigate(item)" class="sidebar-menu-link">
                    <span class="menu-icon material-icons">{{item.icon}}</span>
                    <span class="menu-text" ng-if="!isCollapsed">{{localization.localize(item.label)}}</span>
                </a>
                <!-- Tooltip for collapsed state -->
                <div class="sidebar-tooltip" ng-if="isCollapsed">
                    {{localization.localize(item.label)}}
                </div>
            </li>
        </ul>
    </nav>

    <!-- Bottom Section -->
    <div class="sidebar-footer" ng-if="!isCollapsed">
        <div class="sidebar-footer-content">
            <span class="version">v5.38.1</span>
        </div>
    </div>
</aside>
```

- [ ] **Step 2: 创建侧边栏样式**

Create: `server/src/main/webapp/css/sidebar.css`

```css
/*
 * Sidebar Component Styles
 */

.sidebar {
    position: fixed;
    left: 0;
    top: var(--header-height);
    bottom: 0;
    width: var(--sidebar-width);
    background-color: var(--sidebar-bg);
    border-right: 1px solid var(--sidebar-border);
    display: flex;
    flex-direction: column;
    transition: width var(--transition-base);
    z-index: 100;
    box-shadow: var(--shadow-sm);
}

.sidebar.collapsed {
    width: var(--sidebar-width-collapsed);
}

/* Header */
.sidebar-header {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 var(--space-4);
    border-bottom: 1px solid var(--sidebar-border);
    position: relative;
}

.sidebar-logo {
    display: flex;
    align-items: center;
    gap: var(--space-2);
    overflow: hidden;
}

.sidebar-logo .logo-icon {
    font-size: 28px;
    color: var(--color-primary);
}

.sidebar-logo .logo-text {
    font-size: 1.25rem;
    font-weight: 700;
    color: var(--text-primary);
    white-space: nowrap;
}

.sidebar-logo.collapsed .logo-icon {
    font-size: 32px;
}

.sidebar-toggle {
    position: absolute;
    right: -12px;
    top: 50%;
    transform: translateY(-50%);
    width: 24px;
    height: 24px;
    border-radius: var(--radius-full);
    background-color: var(--color-surface);
    border: 1px solid var(--color-border);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    opacity: 0;
    transition: opacity var(--transition-fast);
    z-index: 10;
}

.sidebar:hover .sidebar-toggle {
    opacity: 1;
}

.sidebar-toggle:hover {
    background-color: var(--color-primary);
    border-color: var(--color-primary);
    color: var(--text-inverse);
}

.sidebar-toggle .material-icons {
    font-size: 16px;
}

/* Navigation */
.sidebar-nav {
    flex: 1;
    overflow-y: auto;
    padding: var(--space-2) 0;
}

.sidebar-menu {
    list-style: none;
    margin: 0;
    padding: 0;
}

.sidebar-menu-item {
    position: relative;
    margin: var(--space-1) var(--space-2);
}

.sidebar-menu-link {
    display: flex;
    align-items: center;
    gap: var(--space-3);
    padding: var(--space-3) var(--space-4);
    border-radius: var(--radius-md);
    color: var(--text-secondary);
    text-decoration: none;
    cursor: pointer;
    transition: all var(--transition-fast);
}

.sidebar.collapsed .sidebar-menu-link {
    justify-content: center;
    padding: var(--space-3);
}

.sidebar-menu-link:hover {
    background-color: var(--color-surface-hover);
    color: var(--text-primary);
}

.sidebar-menu-item.active .sidebar-menu-link {
    background-color: var(--color-primary-50);
    color: var(--color-primary);
}

.sidebar-menu-link .menu-icon {
    font-size: 20px;
    flex-shrink: 0;
}

.sidebar-menu-link .menu-text {
    font-size: 0.875rem;
    font-weight: 500;
    white-space: nowrap;
}

/* Tooltip for collapsed state */
.sidebar-tooltip {
    position: absolute;
    left: calc(100% + var(--space-2));
    top: 50%;
    transform: translateY(-50%);
    background-color: var(--text-primary);
    color: var(--text-inverse);
    padding: var(--space-2) var(--space-3);
    border-radius: var(--radius);
    font-size: 0.875rem;
    white-space: nowrap;
    opacity: 0;
    visibility: hidden;
    transition: all var(--transition-fast);
    z-index: 1000;
}

.sidebar.collapsed .sidebar-menu-item:hover .sidebar-tooltip {
    opacity: 1;
    visibility: visible;
}

/* Footer */
.sidebar-footer {
    padding: var(--space-4);
    border-top: 1px solid var(--sidebar-border);
}

.sidebar-footer-content {
    text-align: center;
}

.sidebar-footer .version {
    font-size: 0.75rem;
    color: var(--text-tertiary);
}

/* Content adjustment */
.main-content {
    margin-left: var(--sidebar-width);
    transition: margin-left var(--transition-base);
}

.sidebar.collapsed ~ .main-content {
    margin-left: var(--sidebar-width-collapsed);
}
```

- [ ] **Step 3: 在 index.html 中引入侧边栏样式**

Modify: `server/src/main/webapp/index.html`

Add to `<head>`:
```html
<link rel="stylesheet" href="css/sidebar.css">
```

- [ ] **Step 4: Commit**

```bash
git add server/src/main/webapp/app/components/ui/sidebar/ server/src/main/webapp/css/sidebar.css server/src/main/webapp/index.html
git commit -m "feat: add sidebar component with modern styling"
```

---

## Chunk 4: 主题切换组件

### Task 4.1: 创建主题切换组件

**Files:**
- Create: `server/src/main/webapp/app/components/ui/theme-switcher/theme-switcher.controller.js`
- Create: `server/src/main/webapp/app/components/ui/theme-switcher/theme-switcher.html`

**Description:**
创建主题切换按钮组件，支持浅色/深色/自动模式切换。

- [ ] **Step 1: 创建主题切换控制器**

Create: `server/src/main/webapp/app/components/ui/theme-switcher/theme-switcher.controller.js`

```javascript
/**
 * Theme Switcher Component
 * Toggle between light/dark/auto themes
 */

angular.module('hmdm').controller('ThemeSwitcherController',
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
```

- [ ] **Step 2: 创建主题切换模板**

Create: `server/src/main/webapp/app/components/ui/theme-switcher/theme-switcher.html`

```html
<!-- Theme Switcher Component -->
<div class="theme-switcher">
    <!-- Compact button version (for header) -->
    <button class="theme-btn"
            ng-click="cycleTheme()"
            title="{{getThemeLabel()}}">
        <span class="material-icons">{{getThemeIcon()}}</span>
    </button>

    <!-- Dropdown version (for settings page) -->
    <div class="theme-dropdown" ng-if="showDropdown">
        <button class="theme-option"
                ng-class="{'active': currentTheme === THEMES.LIGHT}"
                ng-click="setTheme(THEMES.LIGHT)">
            <span class="material-icons">light_mode</span>
            <span>浅色模式</span>
        </button>
        <button class="theme-option"
                ng-class="{'active': currentTheme === THEMES.DARK}"
                ng-click="setTheme(THEMES.DARK)">
            <span class="material-icons">dark_mode</span>
            <span>深色模式</span>
        </button>
        <button class="theme-option"
                ng-class="{'active': currentTheme === THEMES.AUTO}"
                ng-click="setTheme(THEMES.AUTO)">
            <span class="material-icons">brightness_auto</span>
            <span>跟随系统</span>
        </button>
    </div>
</div>
```

- [ ] **Step 3: 添加样式到 modern-theme.css**

Add to `server/src/main/webapp/css/modern-theme.css`:

```css
/* Theme Switcher Styles */
.theme-switcher {
    display: flex;
    align-items: center;
}

.theme-btn {
    width: 36px;
    height: 36px;
    border-radius: var(--radius);
    border: 1px solid transparent;
    background: transparent;
    color: var(--text-secondary);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all var(--transition-fast);
}

.theme-btn:hover {
    background-color: var(--color-surface-hover);
    color: var(--text-primary);
}

.theme-btn .material-icons {
    font-size: 20px;
}

/* Theme Dropdown */
.theme-dropdown {
    display: flex;
    flex-direction: column;
    gap: var(--space-1);
    padding: var(--space-2);
    background-color: var(--color-surface);
    border: 1px solid var(--color-border);
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-md);
    min-width: 160px;
}

.theme-option {
    display: flex;
    align-items: center;
    gap: var(--space-3);
    padding: var(--space-2) var(--space-3);
    border-radius: var(--radius);
    border: none;
    background: transparent;
    color: var(--text-secondary);
    cursor: pointer;
    font-size: 0.875rem;
    text-align: left;
    transition: all var(--transition-fast);
}

.theme-option:hover {
    background-color: var(--color-surface-hover);
    color: var(--text-primary);
}

.theme-option.active {
    background-color: var(--color-primary-50);
    color: var(--color-primary);
}

.theme-option .material-icons {
    font-size: 18px;
}
```

- [ ] **Step 4: Commit**

```bash
git add server/src/main/webapp/app/components/ui/theme-switcher/
git commit -m "feat: add theme switcher component"
```

---

## Chunk 5: 主布局重构

### Task 5.1: 重构主布局框架

**Files:**
- Modify: `server/src/main/webapp/app/components/main/view/main.html`
- Modify: `server/src/main/webapp/app/components/main/view/content.html`

**Description:**
重构主布局，整合侧边栏、顶部导航、主题系统，形成新的现代布局。

- [ ] **Step 1: 重构 main.html**

Modify: `server/src/main/webapp/app/components/main/view/main.html`

```html
<!-- Modern Main Layout -->
<div class="modern-ui">
    <!-- Header -->
    <header class="modern-header">
        <div class="header-left">
            <div class="logo" ng-if="isSidebarCollapsed">
                <span class="material-icons">phonelink_setup</span>
            </div>
            <h1 class="page-title">{{pageTitle}}</h1>
        </div>

        <div class="header-right">
            <!-- Theme Switcher -->
            <theme-switcher></theme-switcher>

            <!-- Language Switcher (existing) -->
            <div class="language-switcher" ng-if="localization.getAvailableLanguages().length > 1">
                <select ng-model="localization.currentLanguage"
                        ng-change="localization.setLanguage(localization.currentLanguage)"
                        class="lang-select">
                    <option ng-repeat="lang in localization.getAvailableLanguages()"
                            value="{{lang.code}}">{{lang.name}}</option>
                </select>
            </div>

            <!-- User Menu -->
            <div class="user-menu dropdown" uib-dropdown>
                <button class="user-btn dropdown-toggle" uib-dropdown-toggle>
                    <span class="material-icons">account_circle</span>
                    <span class="user-name" ng-if="!isSidebarCollapsed">{{user.name || user.login}}</span>
                </button>
                <ul class="dropdown-menu dropdown-menu-right" uib-dropdown-menu>
                    <li><a ng-click="openProfile()">{{localization.localize('menu.profile')}}</a></li>
                    <li><a ng-click="logout()">{{localization.localize('menu.logout')}}</a></li>
                </ul>
            </div>
        </div>
    </header>

    <!-- Sidebar -->
    <hmdm-sidebar></hmdm-sidebar>

    <!-- Main Content Area -->
    <main class="main-content">
        <ng-include src="'app/components/main/view/content.html'"></ng-include>
    </main>
</div>
```

- [ ] **Step 2: 简化 content.html**

Modify: `server/src/main/webapp/app/components/main/view/content.html`

```html
<!-- Content Area - Modern Layout -->
<div class="content-area">
    <!-- View Toggle (Table/Card) -->
    <view-toggle ng-if="showViewToggle"></view-toggle>

    <!-- Search & Filter -->
    <search-filter ng-if="showSearchFilter"></search-filter>

    <!-- Main Content -->
    <div class="content-body">
        <!-- Tabset -->
        <tabset class="modern-tabset">
            <tab ng-repeat="tab in tabs"
                 active="tab.active"
                 ng-click="openTab(tab.id)"
                 ng-if="hasPermission(tab.permission)">
                <tab-heading>
                    <span class="material-icons" ng-if="tab.icon">{{tab.icon}}</span>
                    {{localization.localize(tab.labelKey)}}
                </tab-heading>
                <ng-include src="tab.template" ng-if="activeTab === tab.id"></ng-include>
            </tab>
        </tabset>
    </div>
</div>
```

- [ ] **Step 3: 添加布局样式到 modern-theme.css**

Add to `server/src/main/webapp/css/modern-theme.css`:

```css
/* Modern Layout Styles */

/* Header */
.modern-header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: var(--header-height);
    background-color: var(--header-bg);
    border-bottom: 1px solid var(--header-border);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 var(--space-6);
    z-index: 200;
    box-shadow: var(--shadow-sm);
}

.header-left {
    display: flex;
    align-items: center;
    gap: var(--space-4);
}

.header-left .logo {
    display: flex;
    align-items: center;
}

.header-left .logo .material-icons {
    font-size: 28px;
    color: var(--color-primary);
}

.page-title {
    font-size: 1.25rem;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0;
}

.header-right {
    display: flex;
    align-items: center;
    gap: var(--space-3);
}

/* Language Switcher */
.language-switcher .lang-select {
    padding: var(--space-2) var(--space-3);
    border: 1px solid var(--color-border);
    border-radius: var(--radius);
    background-color: var(--color-surface);
    color: var(--text-primary);
    font-size: 0.875rem;
    cursor: pointer;
}

/* User Menu */
.user-menu .user-btn {
    display: flex;
    align-items: center;
    gap: var(--space-2);
    padding: var(--space-2) var(--space-3);
    border: 1px solid transparent;
    border-radius: var(--radius);
    background: transparent;
    color: var(--text-secondary);
    cursor: pointer;
    transition: all var(--transition-fast);
}

.user-menu .user-btn:hover {
    background-color: var(--color-surface-hover);
    color: var(--text-primary);
}

.user-menu .user-btn .material-icons {
    font-size: 24px;
}

.user-menu .user-name {
    font-size: 0.875rem;
    font-weight: 500;
}

/* Main Content */
.main-content {
    margin-left: var(--sidebar-width);
    margin-top: var(--header-height);
    min-height: calc(100vh - var(--header-height));
    background-color: var(--color-bg);
    transition: margin-left var(--transition-base);
}

.sidebar.collapsed ~ .main-content {
    margin-left: var(--sidebar-width-collapsed);
}

/* Content Area */
.content-area {
    padding: var(--space-6);
}

.content-body {
    background-color: var(--color-surface);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow);
    padding: var(--space-6);
}

/* Modern Tabs */
.modern-tabset .nav-tabs {
    border-bottom: 1px solid var(--color-border);
    margin-bottom: var(--space-4);
}

.modern-tabset .nav-tabs > li > a {
    display: flex;
    align-items: center;
    gap: var(--space-2);
    padding: var(--space-3) var(--space-4);
    color: var(--text-secondary);
    border: none;
    border-bottom: 2px solid transparent;
    background: transparent;
    font-weight: 500;
    transition: all var(--transition-fast);
}

.modern-tabset .nav-tabs > li > a:hover {
    color: var(--text-primary);
    background-color: var(--color-surface-hover);
}

.modern-tabset .nav-tabs > li.active > a {
    color: var(--color-primary);
    border-bottom-color: var(--color-primary);
    background-color: var(--color-primary-50);
}

.modern-tabset .nav-tabs > li > a .material-icons {
    font-size: 18px;
}
```

- [ ] **Step 4: Commit**

```bash
git add server/src/main/webapp/app/components/main/view/main.html server/src/main/webapp/app/components/main/view/content.html server/src/main/webapp/css/modern-theme.css
git commit -m "feat: refactor main layout with sidebar and modern header"
```

---

## Chunk 6: 视图切换组件

### Task 6.1: 创建表格/卡片视图切换组件

**Files:**
- Create: `server/src/main/webapp/app/components/ui/view-toggle/view-toggle.controller.js`
- Create: `server/src/main/webapp/app/components/ui/view-toggle/view-toggle.html`

**Description:**
创建视图切换组件，支持表格视图和卡片视图切换，记忆用户偏好。

- [ ] **Step 1: 创建视图切换控制器**

Create: `server/src/main/webapp/app/components/ui/view-toggle/view-toggle.controller.js`

```javascript
/**
 * View Toggle Component
 * Toggle between table and card views
 */

angular.module('hmdm').controller('ViewToggleController',
['$scope', '$rootScope', function($scope, $rootScope) {

    const STORAGE_KEY = 'hmdm-view-preference';

    // View types
    $scope.VIEWS = {
        TABLE: 'table',
        CARD: 'card'
    };

    // Current view
    $scope.currentView = $scope.VIEWS.TABLE;

    /**
     * Initialize
     */
    function init() {
        var savedView = localStorage.getItem(STORAGE_KEY);
        if (savedView && (savedView === $scope.VIEWS.TABLE || savedView === $scope.VIEWS.CARD)) {
            $scope.currentView = savedView;
        }
    }

    /**
     * Set view type
     */
    $scope.setView = function(view) {
        if (view === $scope.currentView) return;

        $scope.currentView = view;
        localStorage.setItem(STORAGE_KEY, view);

        // Broadcast event for parent controllers
        $rootScope.$broadcast('viewChanged', view);
    };

    /**
     * Check if view is active
     */
    $scope.isActive = function(view) {
        return $scope.currentView === view;
    };

    init();
}])
.directive('viewToggle', function() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/ui/view-toggle/view-toggle.html',
        controller: 'ViewToggleController',
        controllerAs: 'viewCtrl',
        scope: {
            // Optional: callback when view changes
            onViewChange: '&?'
        }
    };
});
```

- [ ] **Step 2: 创建视图切换模板**

Create: `server/src/main/webapp/app/components/ui/view-toggle/view-toggle.html`

```html
<!-- View Toggle Component -->
<div class="view-toggle">
    <button class="view-btn"
            ng-class="{'active': isActive(VIEWS.TABLE)}"
            ng-click="setView(VIEWS.TABLE)"
            title="表格视图">
        <span class="material-icons">table_rows</span>
    </button>
    <button class="view-btn"
            ng-class="{'active': isActive(VIEWS.CARD)}"
            ng-click="setView(VIEWS.CARD)"
            title="卡片视图">
        <span class="material-icons">grid_view</span>
    </button>
</div>
```

- [ ] **Step 3: 添加样式**

Add to `server/src/main/webapp/css/modern-theme.css`:

```css
/* View Toggle Styles */
.view-toggle {
    display: flex;
    align-items: center;
    gap: var(--space-1);
    padding: var(--space-1);
    background-color: var(--color-surface);
    border: 1px solid var(--color-border);
    border-radius: var(--radius);
}

.view-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border: none;
    border-radius: var(--radius-sm);
    background: transparent;
    color: var(--text-tertiary);
    cursor: pointer;
    transition: all var(--transition-fast);
}

.view-btn:hover {
    background-color: var(--color-surface-hover);
    color: var(--text-secondary);
}

.view-btn.active {
    background-color: var(--color-primary);
    color: var(--text-inverse);
}

.view-btn .material-icons {
    font-size: 18px;
}
```

- [ ] **Step 4: Commit**

```bash
git add server/src/main/webapp/app/components/ui/view-toggle/
git commit -m "feat: add table/card view toggle component"
```

---

## Chunk 7: 搜索筛选组件

### Task 7.1: 创建搜索筛选组件

**Files:**
- Create: `server/src/main/webapp/app/components/ui/search-filter/search-filter.controller.js`
- Create: `server/src/main/webapp/app/components/ui/search-filter/search-filter.html`

**Description:**
创建搜索和筛选组件，支持快速搜索、快捷筛选标签、高级筛选抽屉。

- [ ] **Step 1: 创建搜索筛选控制器**

Create: `server/src/main/webapp/app/components/ui/search-filter/search-filter.controller.js`

```javascript
/**
 * Search Filter Component
 * Search, quick filters, and advanced filters
 */

angular.module('hmdm').controller('SearchFilterController',
['$scope', '$timeout', function($scope, $timeout) {

    // Search state
    $scope.searchQuery = '';
    $scope.searchTimeout = null;

    // Filters
    $scope.activeFilters = {};
    $scope.showAdvancedFilter = false;

    // Quick filter tags
    $scope.quickFilters = $scope.quickFilters || [];

    /**
     * Initialize
     */
    function init() {
        // Load saved filters if any
        var savedFilters = localStorage.getItem('hmdm-filters-' + $scope.filterKey);
        if (savedFilters) {
            try {
                $scope.activeFilters = JSON.parse(savedFilters);
            } catch(e) {
                console.warn('Failed to load saved filters');
            }
        }
    }

    /**
     * Handle search input with debounce
     */
    $scope.onSearchChange = function() {
        if ($scope.searchTimeout) {
            $timeout.cancel($scope.searchTimeout);
        }

        $scope.searchTimeout = $timeout(function() {
            $scope.applyFilters();
        }, 300);
    };

    /**
     * Apply search and filters
     */
    $scope.applyFilters = function() {
        var filterData = {
            search: $scope.searchQuery,
            filters: $scope.activeFilters
        };

        // Call parent handler
        if ($scope.onFilterChange) {
            $scope.onFilterChange({filters: filterData});
        }

        // Save filters
        if ($scope.filterKey) {
            localStorage.setItem('hmdm-filters-' + $scope.filterKey, JSON.stringify($scope.activeFilters));
        }
    };

    /**
     * Toggle quick filter
     */
    $scope.toggleQuickFilter = function(filter) {
        filter.active = !filter.active;

        if (filter.active) {
            $scope.activeFilters[filter.key] = filter.value;
        } else {
            delete $scope.activeFilters[filter.key];
        }

        $scope.applyFilters();
    };

    /**
     * Toggle advanced filter panel
     */
    $scope.toggleAdvancedFilter = function() {
        $scope.showAdvancedFilter = !$scope.showAdvancedFilter;
    };

    /**
     * Clear all filters
     */
    $scope.clearFilters = function() {
        $scope.searchQuery = '';
        $scope.activeFilters = {};

        // Reset quick filters
        $scope.quickFilters.forEach(function(f) {
            f.active = false;
        });

        $scope.applyFilters();
    };

    /**
     * Check if any filter is active
     */
    $scope.hasActiveFilters = function() {
        return $scope.searchQuery || Object.keys($scope.activeFilters).length > 0;
    };

    /**
     * Get active filter count
     */
    $scope.getActiveFilterCount = function() {
        return Object.keys($scope.activeFilters).length;
    };

    init();
}])
.directive('searchFilter', function() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/ui/search-filter/search-filter.html',
        controller: 'SearchFilterController',
        controllerAs: 'filterCtrl',
        scope: {
            filterKey: '@?',           // Unique key for saving filters
            quickFilters: '=?',        // Array of quick filter objects
            onFilterChange: '&?',      // Callback when filters change
            placeholder: '@?'          // Search placeholder text
        }
    };
});
```

- [ ] **Step 2: 创建搜索筛选模板**

Create: `server/src/main/webapp/app/components/ui/search-filter/search-filter.html`

```html
<!-- Search Filter Component -->
<div class="search-filter">
    <!-- Search Bar -->
    <div class="search-bar">
        <span class="search-icon material-icons">search</span>
        <input type="text"
               class="search-input"
               ng-model="searchQuery"
               ng-change="onSearchChange()"
               placeholder="{{placeholder || '搜索...'}}">
        <button class="search-clear"
                ng-if="searchQuery"
                ng-click="searchQuery = ''; onSearchChange()">
            <span class="material-icons">close</span>
        </button>
    </div>

    <!-- Quick Filter Tags -->
    <div class="quick-filters" ng-if="quickFilters.length > 0">
        <button class="filter-tag"
                ng-repeat="filter in quickFilters"
                ng-class="{'active': filter.active}"
                ng-click="toggleQuickFilter(filter)">
            {{filter.label}}
        </button>
    </div>

    <!-- Advanced Filter Toggle -->
    <button class="filter-btn" ng-click="toggleAdvancedFilter()">
        <span class="material-icons">filter_list</span>
        <span>筛选</span>
        <span class="filter-count" ng-if="getActiveFilterCount() > 0">{{getActiveFilterCount()}}</span>
    </button>

    <!-- Clear Filters -->
    <button class="clear-btn" ng-if="hasActiveFilters()" ng-click="clearFilters()">
        <span class="material-icons">clear_all</span>
        <span>清除</span>
    </button>
</div>

<!-- Advanced Filter Panel (Drawer) -->
<div class="advanced-filter-drawer" ng-class="{'open': showAdvancedFilter}">
    <div class="drawer-header">
        <h3>高级筛选</h3>
        <button class="close-btn" ng-click="toggleAdvancedFilter()">
            <span class="material-icons">close</span>
        </button>
    </div>
    <div class="drawer-body">
        <!-- Transclude advanced filter content from parent -->
        <ng-transclude></ng-transclude>
    </div>
    <div class="drawer-footer">
        <button class="btn btn-secondary" ng-click="clearFilters()">重置</button>
        <button class="btn btn-primary" ng-click="applyFilters(); toggleAdvancedFilter()">应用</button>
    </div>
</div>
```

- [ ] **Step 3: 添加样式**

Add to `server/src/main/webapp/css/modern-theme.css`:

```css
/* Search Filter Styles */
.search-filter {
    display: flex;
    align-items: center;
    gap: var(--space-3);
    margin-bottom: var(--space-4);
    flex-wrap: wrap;
}

/* Search Bar */
.search-bar {
    position: relative;
    flex: 1;
    min-width: 240px;
    max-width: 400px;
}

.search-bar .search-icon {
    position: absolute;
    left: var(--space-3);
    top: 50%;
    transform: translateY(-50%);
    color: var(--text-tertiary);
    font-size: 20px;
}

.search-input {
    width: 100%;
    height: 40px;
    padding: 0 var(--space-10) 0 var(--space-10);
    border: 1px solid var(--color-border);
    border-radius: var(--radius);
    background-color: var(--color-surface);
    color: var(--text-primary);
    font-size: 0.875rem;
    transition: all var(--transition-fast);
}

.search-input:focus {
    outline: none;
    border-color: var(--color-primary);
    box-shadow: 0 0 0 3px var(--color-primary-100);
}

.search-clear {
    position: absolute;
    right: var(--space-2);
    top: 50%;
    transform: translateY(-50%);
    width: 24px;
    height: 24px;
    border: none;
    border-radius: var(--radius-full);
    background: var(--color-surface-hover);
    color: var(--text-secondary);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
}

.search-clear:hover {
    background: var(--color-border);
}

.search-clear .material-icons {
    font-size: 16px;
}

/* Quick Filters */
.quick-filters {
    display: flex;
    align-items: center;
    gap: var(--space-2);
    flex-wrap: wrap;
}

.filter-tag {
    padding: var(--space-1) var(--space-3);
    border: 1px solid var(--color-border);
    border-radius: var(--radius-full);
    background-color: var(--color-surface);
    color: var(--text-secondary);
    font-size: 0.875rem;
    cursor: pointer;
    transition: all var(--transition-fast);
}

.filter-tag:hover {
    border-color: var(--color-primary);
    color: var(--color-primary);
}

.filter-tag.active {
    background-color: var(--color-primary);
    border-color: var(--color-primary);
    color: var(--text-inverse);
}

/* Filter Button */
.filter-btn,
.clear-btn {
    display: flex;
    align-items: center;
    gap: var(--space-2);
    height: 40px;
    padding: 0 var(--space-4);
    border: 1px solid var(--color-border);
    border-radius: var(--radius);
    background-color: var(--color-surface);
    color: var(--text-secondary);
    font-size: 0.875rem;
    cursor: pointer;
    transition: all var(--transition-fast);
}

.filter-btn:hover,
.clear-btn:hover {
    border-color: var(--color-primary);
    color: var(--color-primary);
}

.filter-btn .material-icons,
.clear-btn .material-icons {
    font-size: 18px;
}

.filter-count {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 18px;
    height: 18px;
    padding: 0 5px;
    background-color: var(--color-primary);
    color: var(--text-inverse);
    border-radius: var(--radius-full);
    font-size: 0.75rem;
    font-weight: 600;
}

/* Advanced Filter Drawer */
.advanced-filter-drawer {
    position: fixed;
    right: 0;
    top: var(--header-height);
    bottom: 0;
    width: 320px;
    background-color: var(--color-surface);
    border-left: 1px solid var(--color-border);
    box-shadow: var(--shadow-lg);
    transform: translateX(100%);
    transition: transform var(--transition-base);
    z-index: 150;
    display: flex;
    flex-direction: column;
}

.advanced-filter-drawer.open {
    transform: translateX(0);
}

.drawer-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: var(--space-4) var(--space-6);
    border-bottom: 1px solid var(--color-border);
}

.drawer-header h3 {
    margin: 0;
    font-size: 1rem;
    font-weight: 600;
    color: var(--text-primary);
}

.drawer-header .close-btn {
    width: 32px;
    height: 32px;
    border: none;
    border-radius: var(--radius);
    background: transparent;
    color: var(--text-secondary);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
}

.drawer-header .close-btn:hover {
    background-color: var(--color-surface-hover);
    color: var(--text-primary);
}

.drawer-body {
    flex: 1;
    overflow-y: auto;
    padding: var(--space-6);
}

.drawer-footer {
    display: flex;
    gap: var(--space-3);
    padding: var(--space-4) var(--space-6);
    border-top: 1px solid var(--color-border);
}

.drawer-footer .btn {
    flex: 1;
    height: 40px;
    border-radius: var(--radius);
    font-size: 0.875rem;
    font-weight: 500;
    cursor: pointer;
    transition: all var(--transition-fast);
}

.drawer-footer .btn-secondary {
    border: 1px solid var(--color-border);
    background-color: var(--color-surface);
    color: var(--text-secondary);
}

.drawer-footer .btn-secondary:hover {
    border-color: var(--text-secondary);
    color: var(--text-primary);
}

.drawer-footer .btn-primary {
    border: none;
    background-color: var(--color-primary);
    color: var(--text-inverse);
}

.drawer-footer .btn-primary:hover {
    background-color: var(--color-primary-dark);
}
```

- [ ] **Step 4: Commit**

```bash
git add server/src/main/webapp/app/components/ui/search-filter/
git commit -m "feat: add search and filter component"
```

---

## Chunk 8: 注册新组件

### Task 8.1: 在 Angular 模块中注册新组件

**Files:**
- Modify: `server/src/main/webapp/app/app.module.js`

**Description:**
确保所有新组件都在 Angular 模块中正确注册。

- [ ] **Step 1: 读取当前 app.module.js**

```bash
cat server/src/main/webapp/app/app.module.js
```

- [ ] **Step 2: 添加新组件依赖**

Modify: `server/src/main/webapp/app/app.module.js`

Ensure all new controllers and directives are loaded. The files should be automatically picked up if they use `angular.module('hmdm')`, but verify the module structure.

Add script includes to `index.html` after other app scripts:

Modify: `server/src/main/webapp/index.html`

Add before closing `</body>`:
```html
<!-- Modern UI Components -->
<script src="app/shared/service/theme.service.js"></script>
<script src="app/components/ui/sidebar/sidebar.controller.js"></script>
<script src="app/components/ui/theme-switcher/theme-switcher.controller.js"></script>
<script src="app/components/ui/view-toggle/view-toggle.controller.js"></script>
<script src="app/components/ui/search-filter/search-filter.controller.js"></script>
```

- [ ] **Step 3: Commit**

```bash
git add server/src/main/webapp/index.html
git commit -m "chore: register new UI components in app module"
```

---

## Chunk 9: 表格和卡片样式

### Task 9.1: 创建现代表格样式

**Files:**
- Modify: `server/src/main/webapp/css/modern-theme.css`

**Description:**
添加现代表格和卡片视图样式。

- [ ] **Step 1: 添加表格样式**

Add to `server/src/main/webapp/css/modern-theme.css`:

```css
/* Modern Table Styles */
.modern-table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 0;
    font-size: 0.875rem;
}

.modern-table th,
.modern-table td {
    padding: var(--space-3) var(--space-4);
    text-align: left;
    border-bottom: 1px solid var(--color-border);
}

.modern-table th {
    background-color: var(--color-surface);
    font-weight: 600;
    color: var(--text-primary);
    text-transform: uppercase;
    font-size: 0.75rem;
    letter-spacing: 0.05em;
    position: sticky;
    top: 0;
    z-index: 10;
}

.modern-table tbody tr {
    transition: background-color var(--transition-fast);
}

.modern-table tbody tr:hover {
    background-color: var(--color-surface-hover);
}

.modern-table tbody tr.selected {
    background-color: var(--color-primary-50);
}

.modern-table tbody tr.selected td {
    border-left: 3px solid var(--color-primary);
}

.modern-table tbody tr.selected:hover {
    background-color: var(--color-primary-100);
}

/* Sortable headers */
.modern-table th.sortable {
    cursor: pointer;
    user-select: none;
}

.modern-table th.sortable:hover {
    background-color: var(--color-surface-hover);
}

.modern-table th .sort-icon {
    margin-left: var(--space-1);
    color: var(--text-tertiary);
    font-size: 16px;
}

.modern-table th.sort-asc .sort-icon,
.modern-table th.sort-desc .sort-icon {
    color: var(--color-primary);
}

/* Table actions */
.table-actions {
    display: flex;
    gap: var(--space-1);
}

.table-actions .action-btn {
    width: 28px;
    height: 28px;
    border: none;
    border-radius: var(--radius);
    background: transparent;
    color: var(--text-secondary);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all var(--transition-fast);
}

.table-actions .action-btn:hover {
    background-color: var(--color-surface-hover);
    color: var(--color-primary);
}

.table-actions .action-btn.delete:hover {
    color: var(--color-error);
}

.table-actions .action-btn .material-icons {
    font-size: 18px;
}

/* Empty state */
.table-empty {
    padding: var(--space-10);
    text-align: center;
    color: var(--text-secondary);
}

.table-empty .empty-icon {
    font-size: 48px;
    color: var(--text-tertiary);
    margin-bottom: var(--space-4);
}

.table-empty .empty-title {
    font-size: 1rem;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: var(--space-2);
}

.table-empty .empty-action {
    margin-top: var(--space-4);
}
```

- [ ] **Step 2: 添加卡片样式**

Add to `server/src/main/webapp/css/modern-theme.css`:

```css
/* Card View Styles */
.card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: var(--space-4);
}

.modern-card {
    background-color: var(--color-surface);
    border: 1px solid var(--color-border);
    border-radius: var(--radius-md);
    padding: var(--space-4);
    transition: all var(--transition-fast);
    cursor: pointer;
}

.modern-card:hover {
    box-shadow: var(--shadow-md);
    border-color: var(--color-primary-200);
}

.modern-card.selected {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 3px var(--color-primary-100);
}

.modern-card .card-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    margin-bottom: var(--space-3);
}

.modern-card .card-icon {
    width: 40px;
    height: 40px;
    border-radius: var(--radius);
    background-color: var(--color-primary-50);
    color: var(--color-primary);
    display: flex;
    align-items: center;
    justify-content: center;
}

.modern-card .card-icon .material-icons {
    font-size: 20px;
}

.modern-card .card-status {
    display: flex;
    align-items: center;
    gap: var(--space-1);
    padding: var(--space-1) var(--space-2);
    border-radius: var(--radius-full);
    font-size: 0.75rem;
    font-weight: 500;
}

.modern-card .card-status.online {
    background-color: rgba(16, 185, 129, 0.1);
    color: var(--color-success);
}

.modern-card .card-status.offline {
    background-color: rgba(239, 68, 68, 0.1);
    color: var(--color-error);
}

.modern-card .card-title {
    font-size: 1rem;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 var(--space-1) 0;
}

.modern-card .card-subtitle {
    font-size: 0.875rem;
    color: var(--text-secondary);
    margin: 0;
}

.modern-card .card-meta {
    display: flex;
    gap: var(--space-4);
    margin-top: var(--space-4);
    padding-top: var(--space-3);
    border-top: 1px solid var(--color-border-light);
}

.modern-card .meta-item {
    display: flex;
    flex-direction: column;
}

.modern-card .meta-label {
    font-size: 0.75rem;
    color: var(--text-tertiary);
    text-transform: uppercase;
}

.modern-card .meta-value {
    font-size: 0.875rem;
    color: var(--text-primary);
    font-weight: 500;
}

.modern-card .card-actions {
    display: flex;
    gap: var(--space-2);
    margin-top: var(--space-4);
}
```

- [ ] **Step 3: 添加分页样式**

Add to `server/src/main/webapp/css/modern-theme.css`:

```css
/* Pagination Styles */
.modern-pagination {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: var(--space-4) 0;
    margin-top: var(--space-4);
    border-top: 1px solid var(--color-border);
}

.pagination-info {
    font-size: 0.875rem;
    color: var(--text-secondary);
}

.pagination-controls {
    display: flex;
    align-items: center;
    gap: var(--space-2);
}

.pagination-btn {
    min-width: 36px;
    height: 36px;
    padding: 0 var(--space-2);
    border: 1px solid var(--color-border);
    border-radius: var(--radius);
    background-color: var(--color-surface);
    color: var(--text-secondary);
    font-size: 0.875rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all var(--transition-fast);
}

.pagination-btn:hover:not(:disabled) {
    border-color: var(--color-primary);
    color: var(--color-primary);
}

.pagination-btn.active {
    background-color: var(--color-primary);
    border-color: var(--color-primary);
    color: var(--text-inverse);
}

.pagination-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.pagination-btn .material-icons {
    font-size: 18px;
}

.pagination-ellipsis {
    color: var(--text-tertiary);
    padding: 0 var(--space-2);
}
```

- [ ] **Step 4: Commit**

```bash
git add server/src/main/webapp/css/modern-theme.css
git commit -m "feat: add modern table and card view styles"
```

---

## Chunk 10: 响应式适配

### Task 10.1: 添加响应式断点样式

**Files:**
- Modify: `server/src/main/webapp/css/modern-theme.css`

**Description:**
添加响应式断点，确保在不同屏幕尺寸下正常显示。

- [ ] **Step 1: 添加响应式样式**

Add to end of `server/src/main/webapp/css/modern-theme.css`:

```css
/* ================================
   Responsive Design
   ================================ */

/* Tablet (< 1200px) */
@media (max-width: 1200px) {
    :root {
        --sidebar-width: var(--sidebar-width-collapsed);
    }

    .sidebar {
        width: var(--sidebar-width-collapsed);
    }

    .sidebar .sidebar-logo .logo-text,
    .sidebar .menu-text,
    .sidebar .sidebar-footer {
        display: none;
    }

    .sidebar-toggle {
        opacity: 1;
    }

    .main-content {
        margin-left: var(--sidebar-width-collapsed);
    }
}

/* Mobile (< 768px) */
@media (max-width: 768px) {
    .modern-header {
        padding: 0 var(--space-4);
    }

    .page-title {
        font-size: 1rem;
    }

    .user-menu .user-name {
        display: none;
    }

    .sidebar {
        transform: translateX(-100%);
        width: var(--sidebar-width);
    }

    .sidebar.open {
        transform: translateX(0);
    }

    .sidebar .sidebar-logo .logo-text,
    .sidebar .menu-text {
        display: block;
    }

    .main-content {
        margin-left: 0;
    }

    .content-area {
        padding: var(--space-4);
    }

    .content-body {
        padding: var(--space-4);
    }

    .card-grid {
        grid-template-columns: 1fr;
    }

    .modern-table {
        display: block;
        overflow-x: auto;
        white-space: nowrap;
    }

    .search-filter {
        flex-direction: column;
        align-items: stretch;
    }

    .search-bar {
        max-width: none;
    }

    .advanced-filter-drawer {
        width: 100%;
        top: 0;
    }
}

/* Small Mobile (< 480px) */
@media (max-width: 480px) {
    .modern-header {
        height: 56px;
    }

    .main-content {
        margin-top: 56px;
    }

    .content-area {
        padding: var(--space-3);
    }

    .modern-tabset .nav-tabs > li > a {
        padding: var(--space-2) var(--space-3);
        font-size: 0.875rem;
    }

    .modern-tabset .nav-tabs > li > a .material-icons {
        display: none;
    }
}

/* Mobile menu overlay */
.sidebar-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 99;
    opacity: 0;
    visibility: hidden;
    transition: all var(--transition-base);
}

.sidebar-overlay.open {
    opacity: 1;
    visibility: visible;
}
```

- [ ] **Step 2: Commit**

```bash
git add server/src/main/webapp/css/modern-theme.css
git commit -m "feat: add responsive design breakpoints"
```

---

## Chunk 11: 页面改造

### Task 11.1: 改造设备列表页面

**Files:**
- Modify: `server/src/main/webapp/app/components/main/view/devices.html`
- Modify: `server/src/main/webapp/app/components/main/controller/devices.controller.js`

**Description:**
应用现代 UI 到设备列表页面，添加视图切换、搜索筛选功能。

- [ ] **Step 1: 读取现有设备页面**

```bash
cat server/src/main/webapp/app/components/main/view/devices.html
```

- [ ] **Step 2: 重构设备页面**

Wrap existing content with modern classes and add view toggle/search filter components.

Modify: `server/src/main/webapp/app/components/main/view/devices.html`

```html
<!-- Devices Page - Modern UI -->
<div class="page-devices">
    <!-- Page Header -->
    <div class="page-header">
        <h2>{{localization.localize('devices.title')}}</h2>
        <button class="btn btn-primary" ng-click="addDevice()">
            <span class="material-icons">add</span>
            {{localization.localize('devices.add')}}
        </button>
    </div>

    <!-- Search and Filter -->
    <search-filter
        filter-key="devices"
        placeholder="搜索设备..."
        quick-filters="quickFilters"
        on-filter-change="onFilterChange(filters)">
        <!-- Advanced Filter Content -->
        <div class="filter-section">
            <label>状态</label>
            <select ng-model="activeFilters.status" ng-change="applyFilters()">
                <option value="">全部</option>
                <option value="online">在线</option>
                <option value="offline">离线</option>
                <option value="pending">待处理</option>
            </select>
        </div>
        <div class="filter-section">
            <label>分组</label>
            <select ng-model="activeFilters.group" ng-change="applyFilters()">
                <option value="">全部</option>
                <option ng-repeat="group in groups" value="{{group.id}}">{{group.name}}</option>
            </select>
        </div>
    </search-filter>

    <!-- View Toggle -->
    <view-toggle on-view-change="onViewChange(view)"></view-toggle>

    <!-- Table View -->
    <div class="view-table" ng-if="currentView === 'table'">
        <table class="modern-table">
            <thead>
                <tr>
                    <th class="sortable" ng-click="sortBy('number')">
                        编号
                        <span class="sort-icon material-icons">
                            {{sortField === 'number' ? (sortAsc ? 'arrow_upward' : 'arrow_downward') : 'unfold_more'}}
                        </span>
                    </th>
                    <th class="sortable" ng-click="sortBy('imei')">
                        IMEI
                        <span class="sort-icon material-icons">
                            {{sortField === 'imei' ? (sortAsc ? 'arrow_upward' : 'arrow_downward') : 'unfold_more'}}
                        </span>
                    </th>
                    <th>状态</th>
                    <th class="sortable" ng-click="sortBy('lastUpdate')">
                        最后在线
                        <span class="sort-icon material-icons">
                            {{sortField === 'lastUpdate' ? (sortAsc ? 'arrow_upward' : 'arrow_downward') : 'unfold_more'}}
                        </span>
                    </th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr ng-repeat="device in filteredDevices" ng-class="{'selected': device.selected}">
                    <td>{{device.number}}</td>
                    <td>{{device.imei}}</td>
                    <td>
                        <span class="badge" ng-class="device.status">
                            {{localization.localize('device.status.' + device.status)}}
                        </span>
                    </td>
                    <td>{{device.lastUpdate | date:'yyyy-MM-dd HH:mm'}}</td>
                    <td>
                        <div class="table-actions">
                            <button class="action-btn" ng-click="viewDevice(device)" title="查看">
                                <span class="material-icons">visibility</span>
                            </button>
                            <button class="action-btn" ng-click="editDevice(device)" title="编辑">
                                <span class="material-icons">edit</span>
                            </button>
                            <button class="action-btn delete" ng-click="deleteDevice(device)" title="删除">
                                <span class="material-icons">delete</span>
                            </button>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <!-- Card View -->
    <div class="view-card" ng-if="currentView === 'card'">
        <div class="card-grid">
            <div class="modern-card"
                 ng-repeat="device in filteredDevices"
                 ng-class="{'selected': device.selected}"
                 ng-click="selectDevice(device)">
                <div class="card-header">
                    <div class="card-icon">
                        <span class="material-icons">smartphone</span>
                    </div>
                    <div class="card-status" ng-class="device.status">
                        <span class="material-icons">
                            {{device.status === 'online' ? 'check_circle' : 'error'}}
                        </span>
                        {{localization.localize('device.status.' + device.status)}}
                    </div>
                </div>
                <h3 class="card-title">{{device.number || device.imei}}</h3>
                <p class="card-subtitle">IMEI: {{device.imei}}</p>
                <div class="card-meta">
                    <div class="meta-item">
                        <span class="meta-label">最后在线</span>
                        <span class="meta-value">{{device.lastUpdate | date:'MM-dd HH:mm'}}</span>
                    </div>
                    <div class="meta-item">
                        <span class="meta-label">分组</span>
                        <span class="meta-value">{{device.groupName || '未分组'}}</span>
                    </div>
                </div>
                <div class="card-actions">
                    <button class="btn btn-sm btn-secondary" ng-click="viewDevice(device)">查看</button>
                    <button class="btn btn-sm btn-primary" ng-click="editDevice(device)">编辑</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Empty State -->
    <div class="table-empty" ng-if="filteredDevices.length === 0">
        <span class="material-icons empty-icon">devices</span>
        <h3 class="empty-title">暂无设备</h3>
        <p>您还没有添加任何设备</p>
        <button class="btn btn-primary empty-action" ng-click="addDevice()">添加设备</button>
    </div>

    <!-- Pagination -->
    <div class="modern-pagination" ng-if="totalPages > 1">
        <div class="pagination-info">
            显示 {{(currentPage - 1) * pageSize + 1}} - {{Math.min(currentPage * pageSize, totalItems)}} 共 {{totalItems}} 条
        </div>
        <div class="pagination-controls">
            <button class="pagination-btn" ng-click="prevPage()" ng-disabled="currentPage === 1">
                <span class="material-icons">chevron_left</span>
            </button>
            <button class="pagination-btn"
                    ng-repeat="page in getPages()"
                    ng-class="{'active': page === currentPage}"
                    ng-click="goToPage(page)">
                {{page}}
            </button>
            <button class="pagination-btn" ng-click="nextPage()" ng-disabled="currentPage === totalPages">
                <span class="material-icons">chevron_right</span>
            </button>
        </div>
    </div>
</div>
```

- [ ] **Step 3: Commit**

```bash
git add server/src/main/webapp/app/components/main/view/devices.html
git commit -m "feat: modernize devices list page with table/card views"
```

---

## Chunk 12: 集成测试和优化

### Task 12.1: 运行构建并修复问题

**Files:**
- All modified files

**Description:**
运行 Maven 构建，测试新 UI 功能，修复发现的问题。

- [ ] **Step 1: 构建前端资源**

```bash
cd /home/sailfish/jubensha-hmdm-ws/hmdm-server/server
cd webtarget && npm install && grunt resolve
cd .. && mvn clean install -DskipTests
```

- [ ] **Step 2: 检查控制台错误**

Open browser console and check for:
- Missing file errors
- JavaScript syntax errors
- AngularJS module errors
- CSS loading issues

- [ ] **Step 3: 修复问题**

Fix any issues found during testing.

- [ ] **Step 4: Commit fixes**

```bash
git add -A
git commit -m "fix: address UI integration issues"
```

---

## Chunk 13: 文档更新

### Task 13.1: 更新 README

**Files:**
- Modify: `README.md` or `server/README.md`

**Description:**
更新项目文档，记录新的 UI 特性和主题切换功能。

- [ ] **Step 1: 添加 UI 特性说明**

Add to README:
```markdown
## UI Features

### Theme Support
- Light/Dark theme with auto-detection
- Manual theme toggle in header
- Theme preference persisted to localStorage

### Internationalization
- Default language: Chinese (zh_CN)
- Auto-detects browser language
- Manual language switcher

### Layout
- Collapsible sidebar navigation
- Responsive design (desktop/tablet/mobile)
- Table/Card view toggle for lists

### Navigation
- Top header with quick actions
- Collapsible sidebar with tooltip support
- Breadcrumb navigation
```

- [ ] **Step 2: Commit**

```bash
git add README.md
git commit -m "docs: update README with new UI features"
```

---

# Implementation Complete

The implementation plan is complete and ready for execution. Summary:

## New Components Created
1. Theme Service - Dark/light theme management
2. Sidebar Component - Collapsible navigation
3. Theme Switcher - Theme toggle UI
4. View Toggle - Table/card view switcher
5. Search Filter - Search and filter component

## Modified Components
1. Localization - Changed default to Chinese
2. Main Layout - New header + sidebar structure
3. Content Area - Updated tab structure
4. Devices Page - Example modernized list page

## Files Created
- `css/modern-theme.css` - Main theme styles
- `css/dark-theme.css` - Dark mode overrides
- `css/sidebar.css` - Sidebar component styles
- `app/components/ui/*` - New UI components
- `app/shared/service/theme.service.js` - Theme management

## Next Steps
1. Execute plan using subagent-driven-development or executing-plans skill
2. Test on multiple browsers
3. Gather user feedback
4. Iterate on remaining pages

---

Plan complete and saved to `docs/superpowers/plans/2026-03-16-hmdm-ui-modernization-plan.md`. Ready to execute?
