# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Headwind MDM** 是一个开源的 Android 移动设备管理平台，用于企业管理 Android 设备。支持设备注册、应用分发、配置管理、Kiosk 模式等功能。

- **技术栈**: Java 8, Jersey (REST), MyBatis, Guice, PostgreSQL, Liquibase
- **前端**: Vanilla JS (组件化结构), Bower, Grunt
- **构建工具**: Maven

## Build Commands

```bash
# 构建项目
mvn install

# 运行单个模块
mvn install -pl <module-name> -am

# 清理构建
mvn clean
```

## Project Structure

```
hmdm-server/
├── common/              # 核心共享模块：DAO、Domain、REST filters、事件系统
├── jwt/                 # JWT 认证模块
├── notification/        # 通知服务
├── server/              # 主服务器模块
│   ├── src/main/java/com/hmdm/
│   │   ├── guice/      # Guice 依赖注入配置
│   │   ├── rest/       # REST 资源 (resource/)
│   │   │   ├── resource/  # REST API 端点
│   │   │   └── json/      # 请求/响应 DTO
│   │   └── task/       # 后台定时任务
│   └── src/main/webapp/  # 前端资源 (Vanilla JS)
├── plugins/             # 插件系统
│   ├── audit/          # 审计日志插件
│   ├── deviceinfo/     # 设备信息插件
│   ├── messaging/      # 消息推送插件
│   ├── platform/       # 平台插件管理
│   ├── push/           # 推送插件
│   ├── xtra/           # 企业版插件
│   └── devicelog/      # 设备日志插件
└── install/sql/        # 数据库初始化和迁移
    └── migrations/     # Liquibase 迁移脚本
```

## Key Architecture Patterns

### REST API Architecture
- **资源层**: `server/src/main/java/com/hmdm/rest/resource/` 下每个文件对应一个 REST 端点
- **JSON DTO**: `rest/json/` 目录包含请求/响应对象
- **认证**: 通过 `common/src/main/java/com/hmdm/rest/filter/AuthFilter.java` 进行

### 数据库访问
- 使用 **MyBatis** 进行 ORM 映射
- Mapper 接口在 `persistence/mapper/` 目录
- 数据库迁移使用 **Liquibase**，通过 Guice 模块在应用启动时自动执行
  - 主 changelog: `server/src/main/resources/liquibase/db.changelog.xml`
  - 插件 changelog: 各插件 `src/main/resources/liquibase/` 目录
  - Liquibase 通过 `DATABASECHANGELOG` 表跟踪已执行的迁移

### 依赖注入
- 使用 **Guice** 进行依赖注入
- 各模块有独立的 Guice Module 配置（如 `*LiquibaseModule.java`, `*RestModule.java`, `*PersistenceModule.java`）

### 插件系统
- 插件位于 `plugins/` 目录，每个插件有独立的模块结构
- 插件通过 Guice 模块集成，提供 REST 端点、数据库表、后台任务

## Development Notes

- 配置文件模板: `server/build.properties.example` → 复制为 `server/build.properties`
- 数据库初始化: `install/sql/hmdm_init.en.sql` (英文) 或 `hmdm_init.ru.sql` (俄文)
- 迁移脚本: `install/sql/migrations/` 目录下按序号执行
- 前端资源: `server/src/main/webapp/app/` 包含 JS 组件

## Important Files

- 入口类: `server/src/main/java/com/hmdm/HMDMApplication.java`
- 数据库配置: `server/src/main/resources/db.properties` (需从模板创建)
- 前端入口: `server/src/main/webapp/index.html`