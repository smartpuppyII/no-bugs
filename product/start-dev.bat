@echo off
chcp 65001 >nul
title 项目开发环境启动

echo ============================================
echo   密讯商城 - 启动开发环境
echo ============================================
echo.

REM 1. 启动 Docker 基础服务
echo [1/3] 启动 Docker 基础服务 (Redis,RabbitMQ,TDengine)...
cd /d "%~dp0dev"
docker compose up -d redis rabbitmq tdengine
if %errorlevel% neq 0 (
    echo [错误] Docker 启动失败，请确认 Docker Desktop 已运行
    pause
    exit /b 1
)
echo       基础服务启动成功!

REM 等待 Redis 就绪
echo       等待 Redis 就绪...
:wait_redis
docker exec mitedtsm-dev-redis redis-cli ping >nul 2>&1
if %errorlevel% neq 0 (
    timeout /t 2 /nobreak >nul
    goto wait_redis
)
echo       Redis 已就绪!
echo.

REM 2. 启动后端 Server (新窗口)
echo [2/3] 启动后端 Server (端口 8080)...
cd /d "%~dp0Server"
start "后端 Server" cmd /k "title 后端 Server - 端口8080 && mvn spring-boot:run -pl mitedtsm-server -Dspring-boot.run.profiles=local"
echo       后端 Server 已在新窗口启动

REM 等待后端启动
echo       等待后端启动 (约 60 秒)...
timeout /t 60 /nobreak >nul
echo.

REM 3. 启动前端 Web (新窗口)
echo [3/3] 启动前端 Web (端口 80)...
cd /d "%~dp0Web"
start "前端 Web" cmd /k "title 前端 Web - 端口80 && npm run dev"
echo       前端 Web 已在新窗口启动
echo.

echo ============================================
echo   启动完成!
echo.
echo   后端 API : http://localhost:8080/
echo   管理后台 : http://localhost:80/
echo   登录账号 : admin / admin123
echo ============================================
echo.
echo   关闭时请分别关闭三个窗口
pause
