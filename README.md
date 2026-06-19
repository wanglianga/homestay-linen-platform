# 民宿布草洗涤周转与损耗平台

一套完整的民宿布草全生命周期管理系统，覆盖从房间退房、保洁装袋、司机取件称重、洗涤厂清洗质检打包返店、库管入库补货，到店长处理损耗客赔的全流程追踪。让一条布草从房间、洗涤厂、仓库再回到房间都有轨迹，旺季缺货、客赔和洗涤短少都能说清责任。

## 原始需求

> 请实现民宿布草洗涤周转与损耗平台，Vue3 工作台连接民宿前台、保洁、洗涤厂、库管和店长，Spring Boot 保存房间退房、布草袋码、污渍照片、称重、洗涤批次、配送签收、报损和成本分摊。保洁退房点数后按床单、被套、浴巾、浴袍和地巾装袋；司机取件并称重；洗涤厂清洗、质检、打包和返店；库管入库后给房间补货；店长处理损耗、客赔和安全库存。这个产品要让一条布草从房间、洗涤厂、仓库再回到房间都有轨迹，旺季缺货、客赔和洗涤短少都能说清责任。

## 技术栈

- **前端**: Vue 3 + Vite + Vue Router + Pinia + Element Plus + ECharts + Axios + Day.js
- **后端**: Spring Boot 3.4 + Spring Data JPA + H2 (开发) / MySQL 8 (生产) + Lombok + Validation
- **部署**: Docker + Docker Compose + Nginx

## 功能概览

### 角色工作台

| 角色 | 主要功能 |
|------|----------|
| 前台 (reception) | 房间状态看板、退房登记、客赔登记 |
| 保洁 (cleaner) | 待清洁任务、退房点数、布草装袋（床单/被套/浴巾/浴袍/地巾）、污渍标记 |
| 司机 (driver) | 待取件列表、取件称重、配送追踪 |
| 洗涤厂 (factory) | 布草袋验收、洗涤批次管理（创建/开始/完成）、质检打包返店 |
| 库管 (warehouse) | 回库验收、库存管理、房间补货、安全库存预警 |
| 店长 (manager) | 数据概览、报损审批、客赔管理、安全库存设置、成本分摊 |

### 业务流程

```
房间退房 → 保洁点数装袋(生成袋码+污渍记录)
    → 司机取件称重
    → 洗涤厂验收(可短少) → 洗涤批次 → 洗涤完成返店
    → 库管回库验收(可短少) → 仓库库存
    → 房间补货 → 回到房间
```

每条布草袋全程轨迹可查，每一步数量差异自动记录，责任清晰。

## 启动方式

### 前置要求

- Node.js >= 18
- JDK >= 17
- Maven >= 3.9
- Docker >= 20.10（使用 Docker 启动时需要）
- Docker Compose >= 2.0（使用 Docker 启动时需要）

### Docker 一键启动（推荐）

在项目根目录执行：

```bash
docker compose up --build
```

后台运行：

```bash
docker compose up --build -d
```

查看日志：

```bash
docker compose logs -f
```

停止并清理：

```bash
docker compose down
```

启动后访问地址：
- 前端: http://localhost:3000
- 后端 API: http://localhost:8080/api
- H2 控制台（仅开发配置）: http://localhost:8080/api/h2-console

Docker 启动会自动拉起 MySQL 8、后端 Spring Boot 服务和前端 Nginx 服务。

### 本地开发启动

#### 1. 安装前端依赖

```bash
cd frontend
npm install
```

#### 2. 启动前端开发服务

```bash
cd frontend
npm run dev
```

前端地址: http://localhost:3000

#### 3. 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

后端默认使用 H2 内存数据库（开发模式），地址: http://localhost:8080/api

如需使用 MySQL，修改 `backend/src/main/resources/application.yml` 中的 `spring.profiles.active` 为 `mysql`，并确保 MySQL 已启动且配置正确。

### 测试账号

所有测试账号密码均为 `123456`：

| 用户名 | 角色 | 说明 |
|--------|------|------|
| reception | 前台 | 房间管理、退房登记 |
| cleaner | 保洁 | 退房点数、布草装袋 |
| driver | 司机 | 取件称重、配送 |
| factory | 洗涤厂 | 洗涤批次、质检返店 |
| warehouse | 库管 | 入库验收、房间补货 |
| manager | 店长 | 报损审批、成本分摊、全权限 |

## 目录结构

```
.
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/homestay/linen/
│   │   ├── common/             # 通用响应类
│   │   ├── config/             # 数据初始化配置
│   │   ├── controller/         # REST API 控制器
│   │   ├── entity/             # JPA 实体
│   │   ├── repository/         # 数据访问层
│   │   └── service/            # 业务逻辑层
│   ├── src/main/resources/
│   │   └── application.yml     # 应用配置
│   ├── Dockerfile
│   ├── .dockerignore
│   └── pom.xml
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── api/                # API 接口封装
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── styles/             # 全局样式
│   │   └── views/              # 页面视图
│   │       ├── reception/      # 前台工作台
│   │       ├── cleaner/        # 保洁工作台
│   │       ├── driver/         # 司机工作台
│   │       ├── factory/        # 洗涤厂工作台
│   │       ├── warehouse/      # 库管工作台
│   │       └── manager/        # 店长工作台
│   ├── Dockerfile
│   ├── .dockerignore
│   ├── nginx.conf
│   └── package.json
├── Dockerfile                  # 根目录一键构建（前后端合并）
├── docker-compose.yml          # Docker Compose 编排
├── .dockerignore
└── README.md
```

## API 说明

后端统一响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

主要 API 模块：

- `/api/user/*` - 用户登录与查询
- `/api/room/*` - 房间管理
- `/api/linenType/*` - 布草类型管理
- `/api/checkoutRecord/*` - 退房记录
- `/api/linenBag/*` - 布草袋（核心周转单元）
- `/api/washBatch/*` - 洗涤批次
- `/api/deliveryReceipt/*` - 配送签收
- `/api/linenStock/*` - 库存管理
- `/api/damageReport/*` - 报损管理
- `/api/costAllocation/*` - 成本分摊
- `/api/roomRestock/*` - 房间补货记录
- `/api/linenTrack/*` - 布草轨迹追踪

## 注意事项

1. 开发环境默认使用 H2 内存数据库，重启后数据会丢失；生产环境请使用 MySQL profile
2. 布草袋状态流转：待取件 → 配送中 → 洗涤中 → 待回库 → 已回库，每一步强制校验
3. 每一步数量差异会自动记录到库存损耗数量，店长可通过报损单进一步明确责任
4. 布草轨迹支持按袋码、房号查询全流程追踪
