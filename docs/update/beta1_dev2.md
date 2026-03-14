# 云北城建 25w42a-branch

[![Downloads](https://img.shields.io/badge/downloads-云北城建-bright?style=flat)](https://modrinth.com/mod/yunbeiuc)
[![QQ群](https://img.shields.io/badge/QQ-云北城建-bright?label=&logo=qq&logoColor=ffffff&color=1EBAFC&labelColor=1DB0EF&logoSize=auto)](https://qm.qq.com/q/uDgtwOJ2Ks)

> [!NOTE]
>
> - 此为分支版本，不公开发布，请勿装载于地图中，切勿与非分支版本一起使用，否则会出现很严重问题，仅供尝鲜！
> - 路牌系统：新增多类型路牌（限速、限重、限宽、限高、禁止类等），支持客户端 GUI 编辑并通过网络包同步到服务端。
> - 道旗（Flag）：新增可交互道旗方块，支持样式选择与状态保存。
> - Geckolib 静态方块：对少数复杂模型（如混凝土护栏）采用 Geckolib 的静态模型和渲染器，便于模型维护与统一渲染逻辑。
> - 同步机制：所有可编辑方块的状态通过 `network` 包下的消息类型同步，确保服务端与客户端一致性。
> - 保存数据：方块实体（BlockEntity）`update` 方法中处理数据保存与加载，确保方块状态在重启后依然保留。

## 简介

本次版本基于 `25w39a`，以完善路牌体系、道旗及部分使用 Geckolib 的静态方块渲染为主，同时补全了方块/物品列表与开发者说明。项目采用 Fabric 官方模板驱动生成并使用 Yarn 映射。

## 创造模式选项卡

模组为单独的分支版本，延续一个创造模式选项卡：
- 云北城建 | 道路方块

该选项卡包含所有道路相关方块，便于玩家查找与使用。
## 本次更新亮点

- 路牌系统：新增多类型路牌（限速、限重、限宽、限高、禁止类等），支持客户端 GUI 编辑并通过网络包同步到服务端。
- 道旗（Flag）：新增可交互道旗方块，支持样式选择与状态保存。
- Geckolib 静态方块：对少数复杂模型（如混凝土护栏）采用 Geckolib 的静态模型和渲染器，便于模型维护与统一渲染逻辑。
- 同步机制：所有可编辑方块的状态通过 `network` 包下的消息类型同步，确保服务端与客户端一致性。
- 保存数据：方块实体（BlockEntity）`update` 方法中处理数据保存与加载，确保方块状态在重启后依然保留。

## 重要文件（摘录）

下面列出本项目中与本次更新直接相关的关键源码文件，便于开发者定位实现：

- 主入口
  - `src/main/java/com/beigu/yunbeiuc/YunbeiUrbanConstruction.java`
  - `src/main/java/com/beigu/yunbeiuc/YunbeiUrbanConstructionClient.java`
- 数据生成
  - `src/main/java/com/beigu/yunbeiuc/YunbeiUrbanConstructionDataGenerator.java`
- 方块注册与声明
  - `src/main/java/com/beigu/yunbeiuc/block/ModBlocks.java`
- 方块（自定义）
  - `src/main/java/com/beigu/yunbeiuc/block/custom/AntiGlareNetPoleBlock.java`
  - `src/main/java/com/beigu/yunbeiuc/block/custom/CrashBarrierConcrete.java`
  - `src/main/java/com/beigu/yunbeiuc/block/custom/DirectionBlock.java`
  - `src/main/java/com/beigu/yunbeiuc/block/custom/FlagBlock.java`
  - `src/main/java/com/beigu/yunbeiuc/block/custom/GantryFrameMainBlock.java`
  - `src/main/java/com/beigu/yunbeiuc/block/custom/GantryFrameSideBlock.java`
  - `src/main/java/com/beigu/yunbeiuc/block/custom/GantryFrameConnectionBlock.java`
  - `src/main/java/com/beigu/yunbeiuc/block/custom/RoadMedianBarrierBlock.java`
  - `src/main/java/com/beigu/yunbeiuc/block/custom/RoadPolesTextDisplay.java`
  - `src/main/java/com/beigu/yunbeiuc/block/custom/SimpleSignBlock.java`
- 方块枚举数据类型（data-driven）
  - `src/main/java/com/beigu/yunbeiuc/block/custom/data/*`（多种 Type 类，例如 `CrashBarrierConcreteType`, `GantryFrameType`, `Sign*` 等）
- 方块实体
  - `src/main/java/com/beigu/yunbeiuc/entity/*.java`（包含 `FlagBlockEntity`, `CrashBarrierConcreteEntity`, 各类 `Sign*BlockEntity`, `SimpleSignEntity` 等）
- 物品与分组
  - `src/main/java/com/beigu/yunbeiuc/item/ModItems.java`
  - `src/main/java/com/beigu/yunbeiuc/item/ModItemGroups.java`
  - `src/main/java/com/beigu/yunbeiuc/item/custom/CrashBarrierConcreteItem.java`
- 网络同步
  - `src/main/java/com/beigu/yunbeiuc/network/ModMessages.java`
  - 多个 Packet 类，例如 `UpdateSpeedLimitPacket.java`, `UpdateFlagPacket.java` 等
- 渲染与模型
  - `src/main/java/com/beigu/yunbeiuc/render/*`（`FlagBlockEntityRenderer`, `CrashBarrierConcreteRenderer` 等）
- 屏幕（GUI）
  - `src/main/java/com/beigu/yunbeiuc/screen/*`（`Sign*Screen`, `FlagSelectionScreen`, `RoadPolesTextDisplayScreen` 等）

## 方块与物品清单（按源码映射）

下列为项目中主要方块/物品的概览以及它们在源码中的实现位置，便于玩家和开发者快速检索：

- 路牌系列（可编辑）
  - 限速、限重、限宽、限高、取消类与禁止类路牌：
    - 实现：`src/main/java/com/beigu/yunbeiuc/entity/Sign*BlockEntity.java`
    - 屏幕：`src/main/java/com/beigu/yunbeiuc/screen/Sign*Screen.java`
    - 同步包：`src/main/java/com/beigu/yunbeiuc/network/Update*Packet.java`
- 简易路牌
  - `SimpleSignBlock.java` / `SimpleSignEntity.java` / `SimpleSignScreen.java` / 渲染器 `SimpleSignBlockEntityRenderer.java`
- 道旗（Flag）
  - `FlagBlock.java` / `FlagBlockEntity.java` / `FlagSelectionScreen.java` / `UpdateFlagPacket.java` / `FlagBlockEntityRenderer.java`
- 道路文字显示器
  - `RoadPolesTextDisplay.java` / `RoadPolesTextDisplayEntity.java` / `RoadPolesTextDisplayScreen.java` / `RoadPolesTextDisplayUpdatePacket.java` / 渲染器
- 混凝土护栏（Geckolib 静态模型）
  - `CrashBarrierConcrete.java` / `CrashBarrierConcreteEntity.java` / `CrashBarrierConcreteModel.java` / `CrashBarrierConcreteItemModel.java` / 渲染器
- 框架与连接件（Gantry Frame）
  - `GantryFrameMainBlock.java`, `GantryFrameSideBlock.java`, `GantryFrameConnectionBlock.java` 与对应 `*Type` 数据类
- 其它基础道路与城建方块
  - 包含：`AntiGlareNetPoleBlock.java`, `RoadMedianBarrierBlock.java` 等

> 说明：上面列出的文件路径均可在 `src/main/java/com/beigu/yunbeiuc` 目录下找到，方便二次开发与定位。

## 使用与交互说明

- 编辑类方块（路牌/道旗/文本显示）
  - 在游戏中右键对应方块可打开编辑界面（Screen）。
  - 编辑完成后，界面会通过对应的 `Update*Packet` 发送到服务端，由服务端验证并广播到其它客户端。
- 道旗选择
  - 使用 `FlagSelectionScreen` 选择样式/朝向，提交后状态会保存在方块实体并同步。
- 文本显示类
  - `RoadPolesTextDisplay` 支持多行文本与位置排版，文本会保存在方块实体并通过网络同步给客户端进行渲染。
- 调试工具
  - 目前分支版本均采用木棍为工具，用于快速切换方块状态或生成测试内容（例如水源、树木）。

## 开发者说明（实现细节与注意点）

- 网络一致性：所有客户端可编辑的方块类型都通过 `ModMessages` 注册的包在客户端和服务端进行双向同步，确保多人联机时状态一致。
- 渲染注意事项：部分复杂方块使用 Geckolib 静态模型（位于 `entity/custom` 和 `render` 中的模型/渲染器），确保资源文件（geo/json、材质）与注册代码匹配。
- 本地化与资源：模型/材质以及 GUI 文本请在资源文件夹中提供对应的语言与材质，否则在某些语言环境下可能显示占位符。
- 性能：连接型方块实现了面消隐（当方块与同类连接时取消内表面渲染）以提升性能；复杂渲染的方块应避免在大量实例同时存在时触发昂贵的渲染路径。

## 变更要点摘要

- 在 `25w39a` 的基础上并重构路牌编辑界面、网络同步及道旗交互逻辑。
- 引入 Geckolib 静态模型用于混凝土护栏和复杂物品的统一渲染。

---

感谢测试与反馈，欢迎在模组的反馈渠道（Modrinth、QQ群等）提交 Bug 与建议，便于我们在后续 dev3 与 beta 发布中持续改进。