# 标牌大渲染重构 — 规格说明

## 1. 目标

把「禁令 / 指示 / 警告」三大类中，当前每个标志一套独立 3D 模型（`SignSimpleBlock`）的实现，改为：

1. 复用已有的 `sign_custom_*` 底板模型（空心牌面）；
2. 标志符号改为世界内渲染的透明 PNG（`CustomSignBlockEntity` 的 `-texture` 文本行）；
3. 放置时自动写入图片行；
4. 旧存档方块自动补建方块实体并写入图片行；
5. 物品栏图标保持与现在一致。

不改变任何方块注册 ID，因此不影响已放置方块的存档兼容性。

## 2. 现状（已核实）

- 共享资源目录：`versions/common/src/main/resources/assets/yunbeiuc/`（所有 MC 版本共用，版本目录下无 resources 覆盖）。
- `SignBlocks` 共 502 个注册，其中 305 个 `new SignSimpleBlock(...)`。
- 目标模型 254 个（`sign_no_*` / `sign_cancel_*` / `sign_speed_limit_*` / `sign_min_speed_limit_*` / `sign_height_limit_*` / `sign_width_limit_*` / `sign_weight_limit_*` / `sign_alex_weight_limit_*` / `sign_indication_*` / `sign_warning_*`，外加 `sign_stop` / `sign_yield` / `sign_yield_to_oncoming_traffic` / `sign_check` / `sign_port_check` 5 个让行、检查标志）。
- 可复用底板 19 个（`normal` / `pole_l` / `pole_h` 三个目录都有，共 118 个自定义模型文件）：
  `sign_custom_no_simple_1/2/3`、`cancel_simple`、`width_limit_simple`、`height_limit_simple`、`alex_weight_limit_simple`、`indication_simple_1/2/3`、`indication_simple_fluorescence`、`warning_simple_1..4`、`warning_simple_fluorescence`、`stop_simple`、`yield_simple`、`min_speed_limit_simple`。
- 旧模型 = 底板元素 + 1~3 个「单面、薄片」符号元素；符号元素贴图为透明底 PNG。
- 文本行 `-texture` 的贴图路径格式必须是 `yunbeiuc:textures/block/sign/xxx.png`（渲染器直接作为 `RenderType.entityCutout` 贴图使用，已由 `SignGuideIntersectionAdvanceWarning7Entity` 等现有代码验证）。
- 渲染参数：`SignTextLinesHelper.logo(path, andX, andY, size, rotZ)`
  - `andX/andY` 单位为 1/16 格，原点为牌面中心 → `模型坐标 - 8`；
  - `size` 为世界内边长（格）→ `元素宽/高 / 16`；
  - 行默认 `builtin=true`，不显示在编辑器普通行中，但可被编辑器保存/删除。
- 模型匹配结论（脚本核对，0 异常、0 缺贴图）：

| 类型支持 | 数量 |
| --- | --- |
| `normal` + `pole_l` + `pole_h` 全支持 | 147 |
| `normal` + `pole_h` | 61 |
| `normal` + `pole_l` | 16 |
| `pole_l` + `pole_h`（`sign_no_overtake`） | 1 |
| 仅 `normal`（`sign_yield`） | 1 |
| **合计可转换** | **226** |

- 同一标志在三个目录下匹配到的底板与图片行参数完全一致（已校验 0 差异），因此定义只需存一份。
- 未匹配 28 个（保持现状，继续使用 `SignSimpleBlock` + 旧 3D 模型）：
  - `sign_indication_one_way_street_*`（3）
  - `sign_indication_parking_4`、`sign_indication_school_bus_wuxi`
  - `sign_no_motor_vehicles`、`sign_no_parking`、`sign_no_parking_long_time`
  - `sign_warning_camel_back_bridge`、`sign_warning_distance_between_vehicles`、`sign_warning_emergency_lane_1/2`、`sign_warning_less_3_to_2`/`less_4_to_3`、`sign_warning_linear_guidance_marker_1..9`、`sign_warning_road_uneven`、`sign_warning_road_wet`、`sign_warning_speed_bump`、`sign_warning_steep_slope_down/up`
  - `sign_speed_limit_*`（13，无对应底板）、`sign_weight_limit_*`（4，无对应底板）
- 其中 `sign_warning_camel_back_bridge`、`road_uneven`、`road_wet`、`speed_bump`、`steep_slope_*`、`reverse_detour_1` 的符号是**非正方形或局部 UV**，无法用现有 `logo()` 的等比正方形渲染，已从转换集合中排除（详见 3.9）。
- `sign_indication_pedestrian_non_motor_vehicles_1/2` 中间的 `ffffff` 分隔条是纯色填充（UV 仅 `[0.25,0.25,0.75,0.75]`），不能作为图片行；改为 `-rect 0.5 14`（白色）文本行，符号仍是两张图片行。

## 3. 方案

### 3.1 新增 `SignImageBlock`

文件：`versions/common/src/main/java/com/beigu/yunbeiuc/block/custom/sign/SignImageBlock.java`

```java
public class SignImageBlock extends CustomSignTypeBlock {
    private final String imageId;                     // 例如 "sign_height_limit_20"
    public SignImageBlock(Properties props, String imageId) { ... }
    public SignImageDefinitions.Def definition() { return SignImageDefinitions.get(imageId); }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (!level.isClientSide()) SignImageMigration.ensure(level, pos);
    }

    @Override
    protected BlockState updateShapeCompat(...) {
        BlockState next = super.updateShapeCompat(...);
        // 杆被拆掉后 TYPE 变为 NORMAL，需要补行（仅服务端）；
        // 此刻世界里还是旧状态，必须把 next 传进去判定新类型
        if (next != state && !world.isClientSide() && world instanceof Level level) {
            SignImageMigration.ensure(level, pos, next);
        }
        return next;
    }
}
```

要点：

- 不在 `newBlockEntityCompat` 里写入文本行。1.16.5 的 `EntityBlockMapper.newBlockEntity(BlockGetter)` 会用 `BlockPos.ZERO` + 空气创建，写行会丢失/错位；统一放到 `onPlace` / 迁移中处理。
- 不覆盖 `getStateForPlacement`、`useCompat`、形状、编辑器逻辑，全部继承 `CustomSignTypeBlock`。
- 类型不支持（`definition().types()` 不含 `state.getValue(TYPE)`）时不写行，继续显示旧 3D 模型。

### 3.2 定义表 `SignImageDefinitions`

文件：`versions/common/src/main/java/com/beigu/yunbeiuc/block/custom/sign/SignImageDefinitions.java`（脚本生成，勿手改）

```java
public final class SignImageDefinitions {
    public record Line(String texture, float x, float y, float size, float rotZ) {}
    public record Def(String base, Set<CustomSignTypeBlock.Type> types, List<Line> lines) {}
    public static Def get(String blockId) { ... }   // 不存在返回 null
}
```

- `base` 为完整底板模型名（如 `sign_custom_no_simple_1`）。
- `types` 为该标志支持的放置类型。
- `lines` 顺序即绘制顺序；部分标志多行（最多 3 行）。
- 生成 226 条，按 ID 排序。示例：

```java
d("sign_height_limit_20", "sign_custom_height_limit_simple", "nh",
  l("yunbeiuc:textures/block/sign/sign_height_limit_20.png", 0f, 0f, 0.625f, 0f));
```

（`"nh"` 是 `normal`/`pole_h` 简写；生成器内部展开为枚举集合。）

### 3.3 注册改写

文件：`versions/common/src/main/java/com/beigu/yunbeiuc/block/SignBlocks.java`

脚本把 226 行：

```java
register("sign_xxx", () -> new SignSimpleBlock(VersionServices.blocks().copyProperties(VanillaBlocks.CYAN_TERRACOTTA).noOcclusion().requiresCorrectToolForDrops()));
```

改写为：

```java
register("sign_xxx", () -> new SignImageBlock(VersionServices.blocks().copyProperties(VanillaBlocks.CYAN_TERRACOTTA).noOcclusion().requiresCorrectToolForDrops(), "sign_xxx"));
```

其余 `SignSimpleBlock` 保持不变。物品注册（`ModItems`）与创造模式标签不需要改动（Block ID 未变）。

> 补充（必需）：`CUSTOM_SIGN_BLOCK_ENTITY` 的合法方块列表必须包含这 226 个新方块。
> 1.20.5+ 的 `BlockEntity` 构造函数会调用 `BlockEntityType.isValid(state)` 校验，
> 未登记时放置/读档直接抛 `IllegalStateException: Invalid block entity ... state`（客户端预测放置即崩溃）。
> 实现见 `ModBlockEntities.withImageSigns(...)`：在原显式列表后追加所有 `instanceof SignImageBlock` 的注册项。

### 3.4 方块状态生成

对 226 个 `assets/yunbeiuc/blockstates/sign_xxx.json` 重新生成 12 个 variant：

- `type=normal` → `yunbeiuc:block/sign/normal/<base>`
- `type=pole_l` → `yunbeiuc:block/sign/pole_l/<base>`（仅当 `types` 含 `POLE_L`；否则保留 `.../pole_l/sign_xxx`）
- `type=pole_h` → `yunbeiuc:block/sign/pole_h/<base>`（同上）
- 四个朝向 `y: 0/90/180/270`，与底板 blockstate 完全一致。

旧模型 JSON 文件全部保留：
- 作为物品图标 `models/item/sign_xxx.json` 的 parent；
- 作为不支持类型的方块模型；
- 作为一键回退/对照。

### 3.5 默认图片行写入

文件：`versions/common/src/main/java/com/beigu/yunbeiuc/block/custom/sign/SignImageMigration.java`

```java
public static void ensure(Level level, BlockPos pos) {
    if (level.isClientSide()) return;
    BlockState state = level.getBlockState(pos);
    if (!(state.getBlock() instanceof SignImageBlock block)) return;
    SignImageDefinitions.Def def = block.definition();
    if (def == null) return;
    CustomSignTypeBlock.Type type = state.getValue(CustomSignTypeBlock.TYPE);
    if (!def.types().contains(type)) return;

    BlockEntity raw = VersionServices.blocks().ensureBlockEntity(level, pos);
    if (!(raw instanceof CustomSignBlockEntity be)) return;
    if (be.hasCustomizedTextLines() || !be.getTextLines().isEmpty()) return;

    List<TextLineData> lines = new ArrayList<>();
    float z = 0f;
    for (SignImageDefinitions.Line line : def.lines()) {
        TextLineData data = SignTextLinesHelper.logo(line.texture(), line.x(), line.y(), line.size(), line.rotZ());
        data.setZOffset(z);   // 每行 0.01（1/16 格），避免多图层 z-fighting
        z += 0.01f;
        lines.add(data);
    }
    be.getTextLines().addAll(lines);
    be.setChanged();
    be.notifyTextChanged();   // 放置路径需要同步给附近玩家；区块迁移路径不经过 ensure，保持静默
}
```

幂等性：
- 已有文本行或用户自定义过 → 不写；
- 玩家在编辑器删空后 `textLinesCustomized=true` → 不再自动加回；
- 重复调用安全。

### 3.6 旧存档迁移

复用每版本已有的 `LegacyRoadStateMixin`（`yunbeiuc.mixins.json` 已注册），在区块反序列化返回 `ProtoChunk` 后扫描并补建方块实体：

1. 1.16.5 ~ 1.21.1：`ChunkSerializer.read(...)` 的 `RETURN` 注入；
   26.1 / 26.1.2 / 26.3：`SerializableChunkData.read(ServerLevel, PoiManager, RegionStorageInfo, ChunkPos)` 的 `RETURN` 注入
   （26.x 已删除 `ChunkSerializer`，该类由 `SerializableChunkData` 取代；注入处理器为实例方法）。
2. 注入统一调用共享的 `SignImageMigration.migrate(ProtoChunk)`：
   - 遍历 `chunk.getSections()`，跳过空 section（1.16.5/1.17.1 用 `isEmpty()`，1.18+ 用 `hasOnlyAir()`）；
   - `pos = new BlockPos(chunkX*16 + lx, chunkMinY + i*16 + ly, chunkZ*16 + lz)`；
   - 方块为 `SignImageBlock` 且当前 `TYPE` 受支持、该位置尚无方块实体时：
     `createBlockEntityCompat(pos, state)` → 写入图片行 → `setProtoChunkBlockEntity(chunk, be)`。
3. 所有注入都带 `require = 0`，签名不符时静默失效，不影响启动。

扫描成本：每个非空 section 4096 次状态查询，只在区块从磁盘反序列化时执行一次；随后 `ensure()` 幂等，不会重复叠加。

### 3.7 跨版本平台补充

`versions/common/.../api/block/BlockPlatform.java` 增加：

```java
@Nullable default BlockEntity ensureBlockEntity(Level level, BlockPos pos); // 不存在则创建并注册（共享默认实现）
void setLevelBlockEntity(Level level, BlockPos pos, BlockEntity be);         // 1.16.5 两参，其余一参
void setProtoChunkBlockEntity(ProtoChunk chunk, BlockEntity be);             // 1.16.5 两参，其余一参
int chunkMinY(ChunkAccess chunk);                                            // 1.16.5/1.17.1 返回 0；1.18~1.21 getMinBuildHeight()；26.x getMinY()
boolean isSectionEmpty(LevelChunkSection section);                           // 1.16.5/1.17.1 isEmpty()；1.18+ hasOnlyAir()
```

`ensureBlockEntity` 在接口中给出共享默认实现：
`level.getBlockEntity(pos)` 非空直接返回；否则取 `state.getBlock() instanceof EntityBlockCompat`，调用
`EntityBlockCompat.createBlockEntityCompat(pos, state)` 创建并通过 `setLevelBlockEntity` 注册。

其余 4 个方法在 11 个 `versions/<ver>/common/.../api/block/BlockPlatformImpl.java` 中按上述版本差异实现。

### 3.8 生成脚本

文件：`tools/sign_image/gen_sign_image.py`（开发期工具，不打包）

职责：
1. 解析 254 个旧模型与 19 个现成底板模型；
2. 按「from/to（2 位小数）+ 解析后的贴图 ID」多重集匹配，底板必须为旧模型的子集；多出的元素必须是单面、厚度 ≤ 0.16 的薄片；
3. 三个目录分别匹配，取并集作为 `types`，要求三目录的底板名与行参数一致（不一致则报错中止）；
4. 生成 `SignImageDefinitions.java`；
5. 重写 226 个 blockstate，并把未转换但 blockstate 已被改成 custom 底板的标志还原；
6. 改写 `SignBlocks.java` 中对应注册行；
7. `--check` 模式只校验、不写盘（CI/验证用）。

### 3.9 派生底板与「可图片化」判定

有些标志没有现成底板（禁止驶入、车道行驶方向），脚本直接从原模型派生底板：保留**多面元素**，以及**非正方形 / 局部 UV 的单面元素**（如车道分隔线，属于牌面固定装饰），只把正方形、整张贴图的单面元素留作图片行。

派生底板（`models/block/sign/<dir>/`，三目录各一份）：

| 底板 | 代表模型 | 覆盖 |
| --- | --- | --- |
| `sign_custom_no_entry_simple` | `sign_no_entry` | 禁止驶入 |
| `sign_custom_lane_direction_simple_1` | `sign_indication_lane_direction_motor_vehicles` | 车型车道（20 宽） |
| `sign_custom_lane_direction_simple_2` | `sign_indication_lane_direction_motor_vehicles_straight` | 车型+方向车道（20 宽） |
| `sign_custom_lane_direction_simple_3` | `sign_indication_lane_direction_straight` | 方向车道（16 宽） |
| `sign_custom_lane_direction_simple_4` | `sign_indication_lane_direction_small_passenger_car_straight` | 小型车/BRT/有轨电车（16 宽） |

`imageable(elem)` 判定（生成器）：单面、厚度 ≤ 0.16、无 z 轴以外旋转、UV 恰为 `[0,0,16,16]`、宽高差 ≤ 0.02，并且贴图**不是纯色填充**（`yunbeiuc:block/color/*`）。不满足则该元素不能作为图片行：
- 纯色填充（`yunbeiuc:block/color/*`）改为 `-rect <宽> <高>` 文本行（`rectable` 判定：单面、薄片、仅 z 轴旋转），颜色取自贴图名，尺寸单位 1/16 格；
- 其余不满足的元素（车道分隔线等非正方形 / 局部 UV 图案）：派生底板时并入底板；
- 使用现有底板时整个标志不转换，`SignBlocks` 中回退为 `SignSimpleBlock`，blockstate 同时还原为原模型（脚本会把多余的 `new SignImageBlock(...)` 还原，并把已改成 `sign_custom_*` 的 blockstate 写回 `<dir>/<name>`）。

原因：`SignTextLinesHelper.logo()` 只有单一 `size`，渲染器按正方形绘制整张贴图，非正方形或局部 UV 的符号会被拉伸/错位。纯色填充（如 `sign_indication_pedestrian_non_motor_vehicles_1/2` 之间的 `ffffff` 分隔条，UV 仅 `[0.25,0.25,0.75,0.75]`）若被当作图片行，会按正方形铺满整个牌面变成一大块纯白；改用 `-rect 0.5 14` 后可保持原始长宽比。

## 3.10 zones_board

24 个 zones_board 纳入转换：6 个箭头、`bus` / `school_bus_1` / `taxi` / `non_motor_vehicles` / `charging` / `disabled` 用图片行；12 个纯文字牌改用 abc 文本行（字体固定 `"a"`，与其它实体一致）。

- 文本内容：学校、口岸、单位专属、匝道、货车道路、货车靠右行驶、校车专用、半挂车、中置轴挂车、中型客车、旅游大客车；`大型货车 / 靠右行驶` 拆为两行。
- 文本行尺寸与位置由原贴图的像素包围盒测得（非白、非透明像素），字号按包围盒高度换算，`scaleX` 按包围盒宽度压缩，避免窄牌文字溢出。
- 派生底板：`sign_custom_zones_board_simple`、`_simple_2`、`_ramp`、`_fluorescence`、`_fluorescence_2`、`_warning`、`_truck_right`、`_large_truck_right`，由对应的无符号整牌模型生成，三目录各一份。
- 全部 13 个派生底板（含 `sign_custom_no_entry_simple`、`sign_custom_lane_direction_simple_1..4`）均注册为独立方块与物品，含 12 变体 blockstate、物品模型和中英文名称。
- 图标/符号改为文本行渲染后，238 个已转换标志在 `pole_l` / `pole_h` 目录下的原始整牌模型已删除（401 个文件，保留派生底板代表模型与 `sign_no_overtake`）；生成器相应改为只读取 `normal` 模型，放置类型取已有定义。
- `zones_board_arrow_right` / `_arrow_left_right` / `_arrow_right_turn` 原模型靠反向 UV 镜像同一张贴图，转换后使用新增的 `*_mirror.png`，避免左右反向。
- 可编辑牌 `time_limit`、`over_weight`、`length`、`distance_length(_left/_right)`、`suggested_speed` 保持原方块类与实体，不转换。
- `red` / `yellow` / `white` / `simple_white_1/2` 为纯底板，无符号，不转换。

## 4. 不做的事

- 不删除任何旧模型 / 贴图 / 物品模型。
- 不改方块 ID、物品 ID、创造模式分组、语言文件。
- 不转换 28 个无底板或符号非正方形的标志（继续 3D）。
- 不改渲染器、编辑器 UI、文本行格式。
- 不调整 `width_limit` 复用 `height_limit` 底板等视觉上的等价替换（已由匹配规则自动选定，视觉一致）。

## 5. 验证

1. 生成器 `--check` 无差异。
2. `versions/1.21.1`：`.\gradlew.bat :common:compileJava`（必要时 `--offline`）通过。
3. 依次验证 `1.20.1`、`1.16.5`、`26.3` 编译（旧版本重点确认 `BlockPlatformImpl` 两处新方法）。
4. 运行验证（1.21.1）：
   - 分别放置禁令 / 指示 / 警告各 5 个样本，正面图位置、大小、朝向正确，无 z-fighting；
   - 放置到纵向杆 / 横向杆上，类型切换后图片仍正确；
   - 拆掉杆子，`pole_l → normal` 自动补行；
   - 用魔杖打开编辑器：图片行可见、可移动/缩放；删除后重载不再自动加回；
   - 旧存档（改动前保存）进入后，旧标志自动出现符号且只出现一次；
   - 物品栏图标与改动前一致；
   - 重新进入世界不重复叠加图片行。

## 6. 风险与回退

- 迁移扫描导致进服卡顿 → 降低每 tick 区块数或仅在区块首次加载时扫描（已限速）。
- 某版本 `ensureBlockEntity` API 差异 → 该版本单独编译修正；资源不改，回退只需把 `SignBlocks` 注册与 blockstate 还原。
- 多图层 z-fighting → 行 zOffset 递增 0.01 已规避（仅 4 个多行标志）。
