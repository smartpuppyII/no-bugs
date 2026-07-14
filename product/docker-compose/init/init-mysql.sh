#!/bin/bash
# MySQL 初始化脚本
# 按指定顺序执行 database 目录下的所有 SQL 文件

set -e

echo "Starting MySQL initialization..."

# 等待 MySQL 完全启动
until mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" -e "SELECT 1"; do
    echo "Waiting for MySQL to be ready..."
    sleep 2
done

echo "MySQL is ready, executing SQL files..."

# SQL 文件目录
SQL_BASE_DIR="/docker-init-sql/base"
SQL_NEW_DIR="/docker-init-sql/new"

# 1. 先执行 quartz.sql
echo "Executing: quartz.sql"
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "${SQL_BASE_DIR}/quartz.sql"

# 2. 再执行 ruoyi-vue-pro.sql
echo "Executing: ruoyi-vue-pro.sql"
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "${SQL_BASE_DIR}/ruoyi-vue-pro.sql"

# 3. 执行 base 目录下其余 SQL 文件（按字母顺序，排除已执行的）
for sql_file in "${SQL_BASE_DIR}"/*.sql; do
    filename=$(basename "$sql_file")
    if [[ "$filename" != "quartz.sql" && "$filename" != "ruoyi-vue-pro.sql" ]]; then
        echo "Executing: ${filename}"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "$sql_file"
    fi
done

# 4. 执行 new 目录下的 SQL 文件（先按依赖顺序执行核心文件，再遍历其余文件）
if [ -d "$SQL_NEW_DIR" ]; then
    # 已手动指定顺序的文件列表（按依赖关系排列）
    PRIORITY_FILES=(
        "new-i18n.sql"                    # 1. 基础表 system_menu_i18n
        "new-mall-i18n.sql"               # 2. promotion_diy_menu_i18n 表
        "new-i18n-ar.sql"                 # 3. 多语言数据（依赖 system_menu_i18n）
        "new-product-category-i18n.sql"   # 4. 产品分类国际化
        "new-large-file-upload.sql"       # 5. 大文件上传
    )

    # 先按依赖顺序执行核心文件
    for filename in "${PRIORITY_FILES[@]}"; do
        if [ -f "${SQL_NEW_DIR}/${filename}" ]; then
            echo "Executing: ${filename}"
            mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "${SQL_NEW_DIR}/${filename}"
        fi
    done

    # 再遍历执行其余 SQL 文件（按文件名排序，排除已执行的）
    for sql_file in "${SQL_NEW_DIR}"/*.sql; do
        if [ -f "$sql_file" ]; then
            filename=$(basename "$sql_file")
            # 跳过已在 PRIORITY_FILES 中执行过的文件
            skip=0
            for pf in "${PRIORITY_FILES[@]}"; do
                if [[ "$filename" == "$pf" ]]; then
                    skip=1
                    break
                fi
            done
            if [ $skip -eq 0 ]; then
                echo "Executing: ${filename}"
                mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "$sql_file"
            fi
        fi
    done
fi

echo "MySQL initialization completed successfully!"