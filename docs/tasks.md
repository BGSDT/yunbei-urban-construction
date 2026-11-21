# 标牌大渲染重构 — 实施任务

> 前置：spec.md 已批准。全部改动不提交 git，除非用户明确要求。

## T1 生成器
- [x] 新建 `tools/sign_image/gen_sign_image.py`
- [x] 实现模型匹配（多重集 + 薄片判定 + 三目录一致性校验）
- [x] 输出 `SignImageDefinitions.java`（226 条）
- [x] 重写 226 个 blockstate（并还原未转换标志的 blockstate）
- [x] 改写 `SignBlocks.java` 中 226 行注册（含非正方形/纯色符号回退）
- [x] 提供 `--check` 模式
- 验收：脚本可重复执行，二次运行无 diff；`--check` 退出码 0

## T2 定义类
- [x] 新建 `SignImageDefinitions.java`（生成物）
- 验收：每个 `SignImageBlock` 的 `definition()` 非空；类型集合与 spec 第 2 节表格一致

## T3 方块类
- [x] 新建 `SignImageBlock`（继承 `CustomSignTypeBlock`）
- [x] 实现 `onPlace` / `updateShapeCompat` 触发 `ensure`
- [x] 不在 `newBlockEntityCompat` 写行
- [x] `ModBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY` 登记全部 `SignImageBlock`（1.20.5+ 构造校验，否则放置即崩溃）
- 验收：新放置普通/杆挂标志均有图片；类型不支持时不写行

## T4 平台方法
- [x] `BlockPlatform` 增加 `ensureBlockEntity(Level, BlockPos)`（共享默认实现）及 `setLevelBlockEntity` / `setProtoChunkBlockEntity` / `chunkMinY` / `isSectionEmpty`
- [x] 在 11 个版本的 `BlockPlatformImpl` 中实现（1.16.5/1.17.1 用 `isEmpty()` 与 minY=0；1.18~1.21 用 `getMinBuildHeight()`+`hasOnlyAir()`；26.x 用 `getMinY()`+`hasOnlyAir()`）
- [x] 验收：已编译版本（1.16.5 / 1.19.2 / 1.19.4 / 1.20.4 / 1.21.1 / 26.1 / 26.1.2 / 26.3）通过

## T5 默认行写入
- [x] 新建 `SignImageMigration.ensure(...)`
- [x] 迁移路径静默写入；放置路径 `notifyTextChanged()` 同步附近玩家
- [x] 幂等：已有文本行或 `hasCustomizedTextLines()` 时跳过；若已有行全部为 `builtin` 且与定义不一致（如旧版纯色填充行），则重建
- [x] 多行按顺序 zOffset 递增 0.01
- 验收：重复调用不叠加；编辑器删除后不再自动加回

## T6 旧存档迁移
- [x] 1.16.5~1.21.1：`ChunkSerializer.read` 的 `RETURN` 注入 → `SignImageMigration.migrate(ProtoChunk)`
- [x] 26.1 / 26.1.2 / 26.3：改为 `SerializableChunkData.read` 的 `RETURN` 注入（26.x 已无 `ChunkSerializer`）
- [x] section 级扫描（跳过空 section），只处理受支持类型
- [x] 注入全部 `require = 0`，签名不符时静默失效
- [ ] 验收：改动前的存档进入后符号自动出现且仅一次；重启不重复

## T7 资源一致性
- [x] 确认旧模型、物品模型、贴图均未删除/移动
- [x] 确认 31 个未转换标志的 blockstate 未变
- 验收：`git status` 只包含预期文件

## T8 编译验证
- [x] `versions/1.21.1`：`.\gradlew.bat :common:compileJava`
- [x] `1.16.5`、`1.19.2`、`1.19.4`、`1.20.4`、`26.1`、`26.1.2`、`26.3` 编译通过
- [ ] `1.17.1`、`1.18.2`、`1.20.1`：Gradle 配置阶段需要重新下载 intermediary，当前网络不可达（maven.fabricmc.net 连接超时），无法在本机完成；三者的 API 组合分别被 1.16.5（isEmpty/minY=0）、1.19.2/1.21.1（getMinBuildHeight/hasOnlyAir）覆盖
- [x] 记录各版本差异修正（`isClientSide()` 方法取代 26.x 的私有字段；26.x 改用 `SerializableChunkData`）
- 验收：可离线编译的版本全部通过

## T10 扩展：让行/检查、禁止驶入、车道行驶方向
- [x] 目标集扩到 254 个（新增 `sign_stop` / `sign_yield` / `sign_yield_to_oncoming_traffic` / `sign_check` / `sign_port_check`）
- [x] 5 个让行、检查标志复用现成底板（`stop_simple` / `yield_simple` / `no_simple_3`）
- [x] 禁止驶入派生 `sign_custom_no_entry_simple`
- [x] 37 个车道行驶方向派生 `sign_custom_lane_direction_simple_1..4`（车道分隔线烘进底板）
- [x] `imageable()` 判定（正方形 + 整张 UV + 非纯色填充）；13 个非正方形符号标志回退为 `SignSimpleBlock`
- [x] `block/color/*` 纯色填充不得成为图片行；行人/非机动车 1、2 的分隔条改用 `-rect 0.5 14` 文本行
- [x] 旧存档中残留的旧版内置行（全部 `builtin` 且与定义不符）在区块加载时重建
- [x] 生成器支持派生底板写盘与 `--check`（0 diff）
- [x] `versions/1.21.1` 编译通过
- [ ] 运行验证（并入 T9）

## T11 zones_board 渲染化
- [x] 24 个 zones_board 纳入转换（6 箭头 + 6 图标 + 12 文字）
- [x] 8 个派生底板（simple / simple_2 / ramp / fluorescence / fluorescence_2 / warning / truck_right / large_truck_right）
- [x] 13 个派生底板注册为方块 + 物品（SignBlocks / ModItems / ModBlockEntities / 12 变体 blockstate / 物品模型 / 中英文本）
- [x] 删除已转换标志的 `pole_l` / `pole_h` 原始模型 401 个（引用检查通过；派生底板代表模型与 `sign_no_overtake` 保留）
- [x] 生成器改为只匹配 `normal` 模型，放置类型优先取现有定义、回退到模型存在性；`normal` 不可图片化时不计入类型
- [x] 文字牌改用 abc 文本行，字体 `"a"`
- [x] 文字尺寸/位置按原贴图像素包围盒计算（字号 + `scaleX`）
- [x] 3 个镜像箭头生成 `*_mirror.png`
- [x] 可编辑牌（time_limit / over_weight / length / distance_length / suggested_speed）保持原样
- [x] `versions/1.21.1` 编译通过，`--check` 0 diff
- [ ] 运行验证（并入 T9）

## T9 运行验证（1.21.1）
- [ ] 禁令 / 指示 / 警告各 5 个样本：位置、大小、朝向、无 z-fighting
- [ ] 横向杆 / 纵向杆放置与拆杆后的补行
- [ ] 编辑器：图片行可见、可编辑、删除后不复活
- [ ] 旧存档迁移
- [ ] 物品栏图标
- [ ] 二次进入世界无重复
- 验收：spec 第 5 节全部通过
