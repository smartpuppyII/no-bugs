# Mitedtsm — 密讯企业级管理平台

## 项目介绍

`Mitedtsm`（密讯）是一套基于 **Spring Boot 3.5** 和 **Vue 3** 的全栈企业级管理系统，底层基于 RuoYi-Vue-Pro 脚手架二次开发，集成 **CRM（客户关系管理）**、**Mall（电商商城）**、**ERP（企业资源）**、**BPM（工作流）**、**IoT（物联网）**、**AI（大模型集成）** 等多业务模块，提供开箱即用的企业数字化解决方案。

系统采用前后端分离架构，支持 **Docker Compose 一键部署**，同时提供 uni-app 跨端商城前端，一套代码可发布到 iOS、Android、H5、微信小程序等多个平台。项目内置国际化支持（中文 / 英文 / 阿拉伯语），适用于全球化业务场景。

---

## 组织结构

```
product
├── Server/                         # 后端 Spring Boot 服务
│   ├── mitedtsm-dependencies/      # 全局依赖版本管理（BOM）
│   ├── mitedtsm-framework/         # 框架层（通用组件 & Starter）
│   │   ├── mitedtsm-common/                          # 通用工具类、枚举、异常
│   │   ├── mitedtsm-spring-boot-starter-web/          # Web 自动配置
│   │   ├── mitedtsm-spring-boot-starter-security/     # 安全认证自动配置
│   │   ├── mitedtsm-spring-boot-starter-mybatis/      # MyBatis 自动配置
│   │   ├── mitedtsm-spring-boot-starter-redis/        # Redis 缓存自动配置
│   │   ├── mitedtsm-spring-boot-starter-mq/           # 消息队列自动配置
│   │   ├── mitedtsm-spring-boot-starter-job/          # 定时任务自动配置
│   │   ├── mitedtsm-spring-boot-starter-excel/        # Excel 导入导出
│   │   ├── mitedtsm-spring-boot-starter-monitor/      # 监控自动配置
│   │   ├── mitedtsm-spring-boot-starter-protection/   # 服务保障（限流/熔断）
│   │   ├── mitedtsm-spring-boot-starter-biz-tenant/   # 多租户
│   │   ├── mitedtsm-spring-boot-starter-biz-data-permission/ # 数据权限
│   │   ├── mitedtsm-spring-boot-starter-biz-ip/       # IP 地址解析
│   │   ├── mitedtsm-spring-boot-starter-websocket/    # WebSocket 支持
│   │   └── mitedtsm-spring-boot-starter-test/         # 单元测试支持
│   ├── mitedtsm-module-system/     # 系统管理（用户 / 角色 / 权限 / 租户）
│   ├── mitedtsm-module-infra/      # 基础设施（文件 / 代码生成 / 定时任务）
│   ├── mitedtsm-module-member/     # 会员管理
│   ├── mitedtsm-module-mall/       # 商城管理（商品 / 交易 / 营销 / 统计）
│   ├── mitedtsm-module-pay/        # 支付管理
│   ├── mitedtsm-module-bpm/        # 工作流管理（Flowable）
│   ├── mitedtsm-module-crm/        # 客户关系管理（线索 / 客户 / 商机 / 合同）
│   ├── mitedtsm-module-erp/        # 企业资源管理
│   ├── mitedtsm-module-ai/         # AI 大模型管理（多模型集成）
│   ├── mitedtsm-module-iot/        # 物联网管理（设备接入 / 数据采集）
│   ├── mitedtsm-module-mp/         # 微信公众号管理
│   ├── mitedtsm-module-report/     # 报表管理（积木报表 / 大屏设计器）
│   └── mitedtsm-server/            # 主启动模块 & 全局配置
├── Web/                            # 管理后台前端（Vue 3 + Element Plus）
│   └── src/
│       ├── views/                  # 页面视图（按模块分层）
│       ├── api/                    # 接口请求
│       ├── components/             # 公共组件
│       ├── router/                 # 路由配置
│       ├── store/                  # 状态管理（Pinia）
│       ├── locales/                # 国际化语言包
│       └── utils/                  # 工具函数
├── MallFrontend/                   # 商城移动端前端（uni-app + Vue 3）
├── InitService/                    # 初始化服务（TDengine 初始建库）
├── database/                       # 数据库脚本
│   ├── base/                       # 基础数据（各模块初始 SQL）
│   ├── new/                        # 增量数据 & 国际化
│   └── replace-en/                 # 字典数据英文替换
├── docker-compose/                 # Docker Compose 一键部署配置
│   ├── docker-compose.yml          # 主编排文件
│   ├── nginx/                      # Nginx 反向代理配置
│   └── init/                       # 容器初始化脚本
├── docker-images/                  # Docker 离线镜像包
├── dev/                            # 本地开发环境（仅基础中间件）
├── docs/                           # 项目文档
└── uploads/                        # 上传文件存储
```

---

## 技术选型

### 后端技术

| 技术                | 说明                     | 官网                                           |
| ------------------- | ------------------------ | ---------------------------------------------- |
| Spring Boot 3.5.9   | 容器 + MVC 框架          | https://spring.io/projects/spring-boot         |
| Spring Security     | 安全认证与授权框架       | https://spring.io/projects/spring-security     |
| MyBatis             | ORM 框架                 | http://www.mybatis.org/mybatis-3/zh/index.html |
| MyBatis-Plus 3.5    | ORM 增强框架             | https://baomidou.com/                          |
| MyBatis-Plus-Join   | 多表联查插件             | https://mybatisplusjoin.com/                   |
| Flowable 7.2        | 工作流引擎               | https://www.flowable.com/                      |
| Druid               | 数据库连接池             | https://github.com/alibaba/druid               |
| MySQL 8.0           | 关系型数据库             | https://www.mysql.com/                         |
| Redis 6             | 分布式缓存               | https://redis.io/                              |
| RabbitMQ            | 消息队列                 | https://www.rabbitmq.com/                      |
| TDengine 3.3        | 时序数据库（IoT 专用）   | https://docs.tdengine.com/                     |
| SpringDoc / Knife4j | API 接口文档             | https://doc.xiaominfo.com/                     |
| Redisson            | 分布式锁 & Redis 客户端  | https://github.com/redisson/redisson           |
| Easy-Trans          | VO 数据翻译（字典/关联） | https://gitee.com/dromara/easy_trans           |
| Lombok              | 简化对象封装工具         | https://github.com/rzwitserloot/lombok         |
| MapStruct           | 对象映射工具             | https://mapstruct.org/                         |
| Hutool              | Java 工具类库            | https://hutool.cn/                             |
| MQTT                | IoT 消息协议             | https://mqtt.org/                              |
| Netty               | 高性能网络框架           | https://netty.io/                              |

### 前端技术（管理后台）

| 技术           | 说明            | 官网                            |
| -------------- | --------------- | ------------------------------- |
| Vue 3.5        | 前端框架        | https://vuejs.org/              |
| Vite 5         | 构建工具        | https://vitejs.dev/             |
| TypeScript     | 类型安全        | https://www.typescriptlang.org/ |
| Element Plus 2 | UI 组件库       | https://element-plus.org/       |
| Pinia          | 状态管理        | https://pinia.vuejs.org/        |
| Vue Router 4   | 路由管理        | https://router.vuejs.org/       |
| UnoCSS         | 原子化 CSS 引擎 | https://unocss.dev/             |
| ECharts 5      | 图表可视化      | https://echarts.apache.org/     |
| vue-i18n       | 国际化          | https://vue-i18n.intlify.dev/   |
| Axios          | HTTP 请求库     | https://axios-http.com/         |
| bpmn-js        | BPMN 流程设计器 | https://bpmn.io/                |
| wangeditor     | 富文本编辑器    | https://www.wangeditor.com/     |

### 前端技术（商城移动端）

| 技术     | 说明              | 官网                          |
| -------- | ----------------- | ----------------------------- |
| uni-app  | 跨端开发框架      | https://uniapp.dcloud.net.cn/ |
| Vue 3    | 前端框架          | https://vuejs.org/            |
| Pinia    | 状态管理          | https://pinia.vuejs.org/      |
| vue-i18n | 国际化            | https://vue-i18n.intlify.dev/ |
| uView    | uni-app UI 组件库 | https://www.uviewui.com/      |

---

## 环境搭建

### 开发环境要求

| 工具      | 版本号 | 下载                                                         |
| --------- | ------ | ------------------------------------------------------------ |
| JDK       | 17     | https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html |
| Maven     | 3.8+   | https://maven.apache.org/download.cgi                        |
| Node.js   | 16+    | https://nodejs.org/                                          |
| pnpm      | 8.6+   | https://pnpm.io/installation                                 |
| MySQL     | 8.0    | https://www.mysql.com/                                       |
| Redis     | 6+     | https://redis.io/                                            |
| RabbitMQ  | 3.x    | https://www.rabbitmq.com/                                    |
| Docker    | 20.10+ | https://www.docker.com/products/docker-desktop               |
| HBuilderX | 最新版 | https://www.dcloud.io/hbuilderx.html（仅 MallFrontend 打包发布需要） |
| IDEA      | 2024+  | https://www.jetbrains.com/idea/（推荐 IDE）                  |

### 方式一：Docker Compose 一键部署（推荐生产使用）

#### 1. 打包后端 Server

```bash
cd Server
mvn install
mvn clean package -DskipTests
```

#### 2. 打包管理后台 Web

```bash
cd Web
npm install -g pnpm
pnpm install
npm run build:prod
```

#### 3. 打包商城前端 MallFrontend

```bash
cd MallFrontend
pnpm install
# 打开 HBuilderX →【发行】→【网站-PC Web或手机H5】
```

#### 4. 打包 InitService

```bash
cd InitService
mvn clean package
```

#### 5. 导入 Docker 镜像

```bash
cd docker-images
docker load -i eclipse-temurin-17-jdk.tar
docker load -i mysql-8.0.tar
docker load -i redis-6-alpine.tar
docker load -i rabbitmq-3-management-alpine.tar
docker load -i tdengine-3.3.6.0.tar
docker load -i nginx-stable-alpine.tar
docker load -i mitedtsm-init-service-latest.tar
docker load -i mitedtsm-server-latest.tar
```

#### 6. 启动所有服务

```bash
cd docker-compose
docker-compose up -d --build
```

启动后将包含以下服务：

| 服务          | 端口       | 说明                |
| ------------- | ---------- | ------------------- |
| MySQL 8.0     | 3306       | 关系型数据库        |
| Redis 6       | 6379       | 缓存服务            |
| RabbitMQ      | 5672/15672 | 消息队列 / 管理界面 |
| TDengine      | 6030/6041  | 时序数据库          |
| Server 后端   | 8080       | Spring Boot 后端    |
| Web 管理后台  | 80         | Nginx 托管前端      |
| Mall 商城前端 | 81         | uni-app H5          |
| InitService   | —          | 初始化后自动退出    |

### 方式二：本地开发环境（推荐日常开发调试使用）

如果本地已安装部分中间件，可按需启动开发环境：

```bash
cd dev
docker-compose up -d                        # 启动全部基础环境
# 或按需组合：
docker-compose up -d redis rabbitmq tdengine init-service     # 跳过 MySQL
docker-compose up -d rabbitmq tdengine init-service           # 跳过 MySQL 和 Redis
docker-compose up -d tdengine init-service                    # 只启动 TDengine + 初始化
```

> ⚠ **注意**：只要启动了 TDengine，就必须带上 `init-service`。init-service 负责在 TDengine 中创建初始数据库，执行完会自动退出。

#### 本地运行后端

在 IDEA 中打开 `Server/mitedtsm-server/src/main/java/com/meession/etm/MitedtsmServerApplication.java`，点击运行按钮。

#### 本地运行管理后台

```bash
cd Web
npm run dev
```

#### 本地运行商城前端

在 HBuilderX 中打开 `MallFrontend`，点击【运行】→【运行到浏览器】。

---

### 默认环境配置（本地开发）

| 服务     | 地址           | 用户名 | 密码   |
| -------- | -------------- | ------ | ------ |
| MySQL    | localhost:3306 | root   | 1234   |
| Redis    | localhost:6379 | —      | 无密码 |
| TDengine | localhost:6041 | —      | —      |
| RabbitMQ | localhost:5672 | rabbit | rabbit |

以上为开发环境的默认配置，可在 `Server/mitedtsm-server/src/main/resources/application-local.yaml` 中修改。

---

## 版本更新说明

如需更新到新版本并重新部署：

```bash
cd docker-compose
docker-compose down -v          # 删除所有容器和数据卷
# 然后回到方式一或方式二的部署流程
```

---

## 国际化说明

系统支持中文（zh-CN）、英文（en）和阿拉伯语（ar）三种语言：

- **菜单 & UI**：系统菜单和界面标签已国际化，语言包位于 `Web/src/locales/`
- **字典数据**：业务字典数据默认存储为中文，Docker Compose 部署后部分内容仍显示中文属正常现象。如需将字典数据替换为英文，请手动在数据库中执行 `database/replace-en/` 下的 SQL 脚本
- **新增功能时**：需同时在 `system_menu_i18n` 表中插入 `zh-CN` 和 `en` 的翻译数据

---

## 项目文档

详细文档位于 `docs/` 目录下：

- `EDIT_GUIDE_BY_3031.md` — 项目修改指引（如何新增功能、修改前后端代码）
- `CRM_REQUIREMENT_ANALYSIS.md` — CRM 模块需求分析与功能规划
- `docs/develop/` — 开发相关文档
- `docs/language/` — 国际化相关文档

---

## 模块功能一览

| 模块   | 功能描述                                                     |
| ------ | ------------------------------------------------------------ |
| System | 用户管理、角色管理、菜单权限、部门管理、租户管理、OAuth2     |
| Infra  | 文件管理、代码生成器、定时任务、接口文档、WebSocket          |
| CRM    | 线索管理、客户管理、商机管理、合同管理、回款管理、公海池、拜访管理、目标管理、报价管理、竞品管理 |
| Mall   | 商品管理、订单管理、营销活动（拼团/秒杀/砍价）、优惠券、分销、商城统计 |
| Member | 会员信息管理、会员等级、积分体系                             |
| Pay    | 支付订单管理、退款管理、支付宝/微信支付接入                  |
| BPM    | 流程设计器、流程定义、流程实例、任务审批（Flowable）         |
| ERP    | 企业资源管理                                                 |
| AI     | 多模型对话、知识库 RAG、AI 绘图（Midjourney）、AI 音乐（Suno）、WebSearch |
| IoT    | 设备接入（MQTT/CoAP/Modbus）、物模型管理、设备数据采集（TDengine） |
| MP     | 微信公众号管理、菜单配置、消息自动回复                       |
| Report | 积木报表、大屏数据可视化设计器                               |

---

## License

本项目基于 MIT 协议开源。
