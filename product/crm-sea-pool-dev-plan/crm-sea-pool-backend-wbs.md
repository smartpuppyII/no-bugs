# CRM 公海回收机制 — 后端开发 WBS 任务分解

> 版本: v1.0 | 日期: 2026-07-18 | 开发经理: AI Dev Manager

## 任务总览

| 编号 | 任务名称 | 类型 | 预估工时 | 依赖 | 优先级 |
|------|----------|------|----------|------|--------|
| T01 | 新建 DO 实体类（5个） | 新建 | 2h | - | P0 |
| T02 | 新建 Mapper 接口（5个） | 新建 | 1.5h | T01 | P0 |
| T03 | 扩展 CrmCustomerDO / CrmClueDO 字段 | 修改 | 0.5h | - | P0 |
| T04 | 扩展 CrmCustomerMapper / CrmClueMapper 查询方法 | 修改 | 1.5h | T03 | P0 |
| T05 | 新建 CrmCluePoolConfigService + Impl | 新建 | 1h | T02 | P0 |
| T06 | 新建 CrmSeaPoolLimitConfigService + Impl | 新建 | 1h | T02 | P0 |
| T07 | 重构 CrmCustomerAutoPutPoolJob | 修改 | 3h | T04,T05,T06 | P0 |
| T08 | 新建 CrmClueAutoPutPoolJob | 新建 | 2h | T04,T05 | P0 |
| T09 | 新建 CrmSeaPoolTodoRemindJob | 新建 | 2h | T04,T05 | P0 |
| T10 | 扩展 CrmCustomerService 接口 | 修改 | 0.5h | - | P0 |
| T11 | 扩展 CrmCustomerServiceImpl（putCustomerPool/batchForceReclaim/receiveCustomer增强） | 修改 | 3h | T06,T10 | P0 |
| T12 | 扩展 CrmClueService 接口 | 修改 | 0.5h | - | P0 |
| T13 | 扩展 CrmClueServiceImpl（putCluePool冷却/receiveClue增强） | 修改 | 2.5h | T06,T12 | P0 |
| T14 | 扩展 ErrorCodeConstants 错误码 | 修改 | 0.5h | - | P1 |

**总工时**: 21.5h ≈ 3个工作日（含15%缓冲）

---

## 详细任务分解

### T01: 新建 DO 实体类

| 子任务 | 文件 | 说明 |
|--------|------|------|
| T01.1 | `CrmCluePoolConfigDO.java` | 线索公海配置 DO，映射 `crm_clue_pool_config` |
| T01.2 | `CrmSeaPoolLimitConfigDO.java` | 公海领取限制配置 DO，映射 `crm_sea_pool_limit_config` |
| T01.3 | `CrmSeaPoolDailyLimitDO.java` | 公海每日领取限量 DO，映射 `crm_sea_pool_daily_limit` |
| T01.4 | `CrmCluePoolRecordDO.java` | 线索公海流转记录 DO，映射 `crm_clue_pool_record` |
| T01.5 | `CrmCustomerPoolRecordDO.java` | 客户公海流转记录 DO，映射 `crm_customer_pool_record` |

### T02: 新建 Mapper 接口

| 子任务 | 文件 | 关键方法 |
|--------|------|----------|
| T02.1 | `CrmCluePoolConfigMapper.java` | selectOne() |
| T02.2 | `CrmSeaPoolLimitConfigMapper.java` | selectOne() |
| T02.3 | `CrmSeaPoolDailyLimitMapper.java` | selectByUserAndDate(), incrementCount() |
| T02.4 | `CrmCluePoolRecordMapper.java` | selectByClueIdAndUserId() |
| T02.5 | `CrmCustomerPoolRecordMapper.java` | selectByCustomerIdAndUserId() |

### T03: 扩展 DO 字段

- `CrmCustomerDO`: 新增 poolStatus, poolEnterTime, poolReason, extendCount, countdownFreeze, freezeReason
- `CrmClueDO`: 新增 poolStatus, poolEnterTime, poolReason, extendCount, protectDeadline, protectUserId

### T04: 扩展 Mapper 查询方法

- `CrmCustomerMapper`: selectListByAutoPoolV2（分级回收+豁免+暂停）, updateOwnerUserIdByIdWithLock（乐观锁）
- `CrmClueMapper`: selectListByAutoPool（自动回收查询）, updateOwnerUserIdByIdWithLock（乐观锁）

### T05-T06: 配置服务层

- `CrmCluePoolConfigService` + Impl: 线索公海规则配置的读取/保存
- `CrmSeaPoolLimitConfigService` + Impl: 领取限制配置的读取/保存

### T07: 重构 CrmCustomerAutoPutPoolJob

核心逻辑变更：
1. 从 CrmCustomerPoolConfigDO 读取 levelExpireDays JSON，按客户等级分级计算回收时效
2. A类（level对应的A类字典值）客户豁免，不回收但生成预警
3. 检查合同状态：pauseContractEnabled 开启时，有未到期合同暂停计时
4. 检查回款状态：pauseReceivableEnabled 开启时，有待回款暂停计时
5. 跟进记录重置倒计时：每次跟进后 contactLastTime 更新，重置倒计时
6. 倒计时冻结标记：countdownFreeze 为 true 时跳过

### T08: 新建 CrmClueAutoPutPoolJob

1. 从 CrmCluePoolConfigDO 读取配置
2. 扫描超时未跟进线索，A类豁免
3. 跟进记录重置倒计时支持
4. 自动流入公海并记录流转日志

### T09: 新建 CrmSeaPoolTodoRemindJob

1. 到期前N天生成待办预警条目
2. 客户和线索分别处理
3. 发送站内信通知

### T10-T11: 扩展 CrmCustomerServiceImpl

- putCustomerPool(): 主动归还公海，增加冷却记录
- batchForceReclaim(): 主管批量强制回收
- receiveCustomer(): 增强每日上限校验、保护期锁定、乐观锁并发控制

### T12-T13: 扩展 CrmClueServiceImpl

- putCluePool(): 增加冷却记录
- receiveClue(): 增强每日上限、冷却时间、保护期锁定

### T14: 扩展 ErrorCodeConstants

新增错误码：公海领取上限、冷却期、保护期、乐观锁冲突等