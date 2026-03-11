# GitHub 开源项目索引爬虫集群

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-blue" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.1-brightgreen" alt="Spring Boot 3.1">
  <img src="https://img.shields.io/badge/Vue-3.3-4fc08d" alt="Vue 3">
  <img src="https://img.shields.io/badge/PostgreSQL-15-336791" alt="PostgreSQL 15">
  <img src="https://img.shields.io/badge/Redis-7.0-red" alt="Redis 7.0">
  <img src="https://img.shields.io/badge/Docker-✓-2496ed" alt="Docker">
  <img src="https://img.shields.io/badge/Element%20Plus-✓-409EFF" alt="Element Plus">
</p>

## 📖 项目简介

本项目是一个分布式 GitHub 仓库元数据爬虫集群，旨在收集数百万个公开仓库的信息（名称、描述、星数、语言等），并提供可搜索的前端界面与可视化图表。核心亮点：

- **多实例爬虫**：通过 Docker Compose 启动多个爬虫实例，水平扩展抓取速度。
- **Redis 分布式锁**：确保同一仓库不会被多个实例重复抓取，避免 API 配额浪费。
- **令牌桶限流**：精确控制全局 GitHub API 请求速率，绕过每小时 5000 次的限制。
- **前后端分离**：Vue 3 前端 + Spring Boot REST API，部署灵活。

爬取的数据可用于开源项目趋势分析、语言热度统计、个性化推荐等场景。

## 🧰 技术栈

| 组件 | 技术选型 |
|------|----------|
| 后端 | Java 17 + Spring Boot 3.1 + MyBatis + Project Lombok + Validation |
| 前端 | Vue 3 + Vite + Vue Router + Pinia + Element Plus + Axios + ECharts |
| 数据库 | PostgreSQL 15（JSONB 存储原始数据） |
| 缓存/协调 | Redis 7（分布式锁、令牌桶、任务队列） |
| 容器化 | Docker + Docker Compose |
| API 客户端 | Spring WebClient（非阻塞调用 GitHub API） |
| 监控 | Spring Boot Actuator + 可集成 Prometheus/Grafana |

## 🏗️ 系统架构

> 实际图片可后续补充，文字说明如下

- **前端**：用户通过浏览器访问 Vue 应用，查询仓库数据。
- **后端 API**：Spring Boot 提供 REST 接口，供前端调用。
- **爬虫集群**：多个 Spring Boot 爬虫实例从 Redis 队列拉取任务，竞争分布式锁，从 GitHub API 抓取数据。
- **PostgreSQL**：存储仓库元数据，其中 `raw_data` 字段以 JSONB 格式保存 GitHub 原始响应。
- **Redis**：存放待抓取仓库队列 (`queue:repos`)、分布式锁 (`lock:repo:{full_name}`)、令牌桶 (`rate:token_bucket`)。
- **Docker Compose**：一键启动所有服务，并支持 scale 爬虫实例。

## ✨ 功能特性

- 多实例分布式爬虫（支持水平扩展）
- Redis 分布式锁（防止重复抓取）
- 令牌桶限流（精确控制 API 请求速率）
- 仓库数据持久化（PostgreSQL）
- 前端仓库列表展示（支持分页、筛选、排序，使用 Element Plus 表格）
- 数据可视化（语言分布饼图、星数直方图，使用 ECharts）
- 增量更新（基于 GH Archive 事件流）
- 全文搜索（PostgreSQL 全文检索或 Elasticsearch）
- Grafana 监控仪表板

## 🚀 快速开始

### 前置要求

- JDK 17+
- Node.js 18+ & npm/pnpm
- Docker & Docker Compose
- Git

### 1. 克隆仓库

```bash
git clone https://github.com/yourname/github-crawler.git
cd github-crawler
```

### 2. 配置 GitHub Token

创建 `tokens.txt` 文件，每行一个 Personal Access Token（可多个）：

```text
ghp_xxxxxxxxxxxxxxxxxxxx
ghp_yyyyyyyyyyyyyyyyyyyy
```

这些 token 将被爬虫实例轮换使用，提升总配额。

### 3. 本地开发环境运行

**启动依赖服务（PostgreSQL + Redis）**

```bash
docker-compose up -d postgres redis
```

**后端（Spring Boot）**

```bash
cd backend
./mvnw spring-boot:run
```

默认 API 地址：http://localhost:8080

**前端（Vue）**

```bash
cd frontend
npm install
npm run dev
```

默认前端地址：http://localhost:5173（Vite 默认端口）

### 4. 使用 Docker Compose 完整部署（模拟生产）

```bash
docker-compose up -d --build
```

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:3000（Nginx 容器） |
| 后端 API | http://localhost:8080 |
| PostgreSQL | localhost:5432 |
| Redis | localhost:6379 |

爬虫实例默认启动 3 个副本，可通过 `--scale crawler=5` 调整数量。

## ⚙️ 配置说明

### 后端配置 (`backend/src/main/resources/application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/github_crawler
    username: crawler
    password: secret
  redis:
    host: localhost
    port: 6379
  data:
    redis:
      repositories:
        enabled: false

github:
  tokens: file:./tokens.txt   # token 文件路径
  api:
    base-url: https://api.github.com
    per-page: 100

crawler:
  queue: "queue:repos"
  lock-prefix: "lock:repo:"
  lock-expire-ms: 10000
  rate-limit:
    max-requests-per-hour: 5000
    bucket-key: "rate:token_bucket"
```

### 前端配置 (`frontend/.env`)

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## 📦 项目结构

```text
github-crawler/
├── backend/                 # Spring Boot 后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/githubcrawler/
│   │   │   │   ├── config/        # Redis、MyBatis 配置
│   │   │   │   ├── controller/    # REST API
│   │   │   │   ├── model/         # 实体类
│   │   │   │   ├── repository/    # MyBatis Mapper
│   │   │   │   ├── service/       # 爬虫、锁、令牌服务
│   │   │   │   └── util/          # 工具类
│   │   │   └── resources/
│   │   │       ├── mapper/        # MyBatis XML
│   │   │       ├── application.yml
│   │   │       └── tokens.txt     # GitHub Token（不提交）
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                # Vue 前端
│   ├── public/
│   ├── src/
│   │   ├── components/    # 列表、图表组件
│   │   ├── views/         # 页面（Home、Stats）
│   │   ├── router/        # Vue Router 配置
│   │   ├── stores/        # Pinia 状态管理
│   │   ├── services/      # API 调用（Axios）
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── vite.config.js
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml       # 编排所有服务
├── .gitignore
└── README.md
```

## 📡 API 文档

### 获取仓库列表

```
GET /api/repos
```

**参数：**

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `page` | 页码 | 1 |
| `size` | 每页数量 | 20 |
| `language` | 按语言过滤（如 `Java`） | - |
| `minStars` | 最小星数 | - |
| `sort` | 排序字段（`stars`、`updated_at`） | `stars` |

**响应：**

```json
{
  "content": [ ... ],
  "totalPages": 100,
  "totalElements": 2000
}
```
