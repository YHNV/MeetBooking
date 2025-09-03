# MeetBooking - 会议室预订系统

## 🌟 快速开始

### 项目环境

- **后端**:
  - JDK 17+
  - Maven 3.6+
- **前端**:
  - Node.js 18+/20+
  - pnpm 18+
- **服务器**:
  - Nginx 1.18+ (可选)

### 1. 克隆项目

```bash
git clone https://github.com/YHNV/MeetBooking.git
cd MeetBooking
```

## 🚀 启动

### 项目配置

1. 修改后端文件上传路径：

   编辑 `/MeetBooking/backend/src/main/java/com/zb/backend/util/FileUploadUtil.java`，将 `UPLOAD_DIR` 的值改为自己的资源目录

2. 修改前端 API 配置:

   编辑 `/MeetBooking/frontend/src/composables/useApi.js`，将 `baseURL` 的值改为 `/api`

### Nginx 配置

1. 下载并安装 Nginx: [官方下载页面](https://nginx.org/en/download.html)
   或者使用对应的*包管理器*

2. 修改资源路径

   编辑 `/MeetBooking/nginx.conf`，将 `# 映射到你的本地图片文件夹` 下的路径，改为自己的资源目录

3. 启动 Nginx:

   **Linux/macOS:**

   ```bash
   nginx -c "$(pwd)/nginx.conf"
   ```

4. 访问应用: http://localhost:3000

### H2 配置（可选）

H2 相关配置在 `/MeetBooking/backend/src/main/resources/application.yml` 开头

**默认是内存模式**，如果需要改为持久化，请参照下面的示例，改为对应的本地路径

## 📁 项目结构

```
MeetBooking/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 前端
└── nginx.conf        # Nginx 配置文件
```
