#!/bin/bash
# MySQL 初始化脚本
# 按指定顺序执行 database 目录下的所有 SQL 文件
#
# 目录结构:
#   /docker-init-sql/base/       - 基础表结构 (按顺序: quartz → ruoyi-vue-pro → 其余按字母)
#   /docker-init-sql/new/        - 增量更新脚本 (按 PRIORITY_FILES 指定顺序)
#   /docker-init-sql/replace-en/ - 英文替换脚本 (最后执行)

# 注意: 不使用 set -e，因为部分 SQL 文件的 ALTER TABLE ADD COLUMN
# 可能在重复执行时报错（列已存在），这类错误是可预期的、不应中断初始化

echo "=========================================="
echo "  MySQL Initialization Starting..."
echo "=========================================="

# 等待 MySQL 完全启动
until mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" -e "SELECT 1" > /dev/null 2>&1; do
    echo "[$(date '+%H:%M:%S')] Waiting for MySQL to be ready..."
    sleep 2
done

echo "[$(date '+%H:%M:%S')] MySQL is ready, executing SQL files..."

# 封装: 执行SQL文件，错误时只警告不中断
run_sql() {
    local filepath="$1"
    local filename="$2"
    echo "[$(date '+%H:%M:%S')] Executing: ${filename}"
    if mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "${filepath}"; then
        return 0
    else
        echo "[$(date '+%H:%M:%S')] WARNING: Errors in ${filename} (may be benign: duplicate column, duplicate key, etc.)"
        return 0  # 不中断脚本
    fi
}

# SQL 文件目录
SQL_BASE_DIR="/docker-init-sql/base"
SQL_NEW_DIR="/docker-init-sql/new"
SQL_REPLACE_EN_DIR="/docker-init-sql/replace-en"

# ============================================
# Phase 1: base 目录 - 基础表结构
# ============================================
echo "--- Phase 1: Base schema ---"

# 1.1 quartz.sql (Quartz 调度器表) — 必须成功
echo "[$(date '+%H:%M:%S')] Executing: quartz.sql"
if ! mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "${SQL_BASE_DIR}/quartz.sql"; then
    echo "FATAL: quartz.sql failed!"; exit 1
fi

# 1.2 ruoyi-vue-pro.sql (核心系统表) — 必须成功
echo "[$(date '+%H:%M:%S')] Executing: ruoyi-vue-pro.sql"
if ! mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "${SQL_BASE_DIR}/ruoyi-vue-pro.sql"; then
    echo "FATAL: ruoyi-vue-pro.sql failed!"; exit 1
fi

# 1.3 其余 base 文件 (按字母顺序，排除已执行的)
for sql_file in "${SQL_BASE_DIR}"/*.sql; do
    filename=$(basename "$sql_file")
    if [[ "$filename" != "quartz.sql" && "$filename" != "ruoyi-vue-pro.sql" ]]; then
        run_sql "$sql_file" "$filename"
    fi
done

# ============================================
# Phase 2: new 目录 - 增量更新 (按依赖顺序)
# ============================================
echo "--- Phase 2: Incremental updates ---"

if [ -d "$SQL_NEW_DIR" ]; then
    # PRIORITY_FILES: 按依赖关系排列的完整文件列表
    # 排序依据: i18n基础表 → CRM表结构 → CRM主菜单 → 增量菜单 → 修复补丁 → 权限 → 测试数据 → 列修复
    PRIORITY_FILES=(
        # ---- 2.1 i18n 基础表 (必须最先执行) ----
        "new-i18n.sql"                    # 1. system_menu_i18n 表结构 + 基础数据
        "new-mall-i18n.sql"               # 2. promotion_diy_menu_i18n 表
        "new-i18n-ar.sql"                 # 3. 阿拉伯语翻译 (依赖 system_menu_i18n)
        "new-product-category-i18n.sql"   # 4. 产品分类国际化
        "new-large-file-upload.sql"       # 5. 大文件上传支持

        # ---- 2.2 CRM 表结构 (DROP+CREATE 或 CREATE IF NOT EXISTS) ----
        "crm-order-2025-07-11.sql"        # 6. crm_order + crm_order_product 表
        "crm-p1-competitor-2025-07-13.sql" # 7. crm_competitor + lose_competitor_id 列
        "crm-p1-visit-2025-07-13.sql"     # 8. crm_visit_plan + crm_visit_record
        "crm-p1-target-2025-07-13.sql"    # 9. crm_sales_target
        "crm-p1-quotation-2025-07-13.sql" # 10. crm_quotation + crm_quotation_product

        # ---- 2.3 CRM 主合并文件 (INSERT IGNORE, 含完整菜单/i18n/权限) ----
        "crm-master-2025-07-13.sql"       # 11. 所有CRM表(IF NOT EXISTS) + 菜单 + i18n + 角色授权

        # ---- 2.4 增量菜单 (依赖 crm-master 的菜单结构) ----
        "crm-order-menu-2025-07-11.sql"   # 12. 订单管理菜单 (6004-6009)
        "crm-cluepool-task-2025-07-12.sql" # 13. 公共线索池 + 跟进任务菜单

        # ---- 2.5 修复与补丁 ----
        "crm-p0-menu-i18n-2025-07-13.sql" # 14. P0菜单i18n补充 (INSERT IGNORE)
        "crm-menu-fix-all-2025-07-13.sql" # 15. 修复菜单名称乱码 + i18n补充

        # ---- 2.6 最新增量 ----
        "crm-quotation-permission-2025-07-16.sql" # 16. 报价单按钮权限 (6051-6055)

        # ---- 2.7 数据修复与测试数据 ----
        "fix-plan-perm.sql"               # 17. 计划权限修复
        "test-crm-backlog-data.sql"        # 18. CRM待办测试数据

        # ---- 2.8 最终列修复 (最后执行，确保所有缺失列补齐) ----
        "fix-missing-columns.sql"          # 19. 直接ALTER TABLE补齐所有缺失列
    )

    # 按依赖顺序执行 PRIORITY_FILES 中的文件
    for filename in "${PRIORITY_FILES[@]}"; do
        if [ -f "${SQL_NEW_DIR}/${filename}" ]; then
            run_sql "${SQL_NEW_DIR}/${filename}" "$filename"
        else
            echo "[$(date '+%H:%M:%S')] SKIP (not found): ${filename}"
        fi
    done

    # 兜底: 执行 new 目录下未被 PRIORITY_FILES 覆盖的 SQL 文件 (按文件名排序)
    # 用于自动发现未来新增的文件
    for sql_file in "${SQL_NEW_DIR}"/*.sql; do
        if [ -f "$sql_file" ]; then
            filename=$(basename "$sql_file")
            skip=0
            for pf in "${PRIORITY_FILES[@]}"; do
                if [[ "$filename" == "$pf" ]]; then
                    skip=1
                    break
                fi
            done
            if [ $skip -eq 0 ]; then
                run_sql "$sql_file" "$filename"
            fi
        fi
    done
else
    echo "WARNING: SQL_NEW_DIR (${SQL_NEW_DIR}) not found, skipping incremental updates"
fi

# ============================================
# Phase 3: replace-en 目录 - 英文翻译替换
# ============================================
echo "--- Phase 3: English translation replacements ---"

if [ -d "$SQL_REPLACE_EN_DIR" ]; then
    for sql_file in "${SQL_REPLACE_EN_DIR}"/*.sql; do
        if [ -f "$sql_file" ]; then
            filename=$(basename "$sql_file")
            run_sql "$sql_file" "$filename"
        fi
    done
else
    echo "INFO: SQL_REPLACE_EN_DIR (${SQL_REPLACE_EN_DIR}) not found, skipping"
fi

echo "=========================================="
echo "  MySQL Initialization Completed!"
echo "=========================================="
