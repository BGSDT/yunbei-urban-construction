# 云北城建1.0.0-beta1-dev3-分支版本

[![Downloads](https://img.shields.io/badge/downloads-云北城建-bright?style=flat)](https://modrinth.com/mod/yunbeiuc)
[![QQ群](https://img.shields.io/badge/QQ-云北城建-bright?label=&logo=qq&logoColor=ffffff&color=1EBAFC&labelColor=1DB0EF&logoSize=auto)](https://modrinth.com/mod/yunbeiuc)

> [!NOTE]
>
> - 本次为 `beta1_dev2` 的延续性更新，重点修复已知问题、优化网络同步与渲染性能，并补充若干可配置项与本地化文本。
> - 新增：路牌模板导入/导出（JSON），创建模式下的快速放置工具，方块粒子与点击音效微调。
> - 优化：网络消息体精简、客户端渲染分支内置缓存、Geckolib 模型文件小幅重构以减少内存占用。
> - 修复：若干导致方块状态不同步和界面崩溃的 Race 条件，以及部分资源丢失导致的加载警告。

## 简介

`beta1_dev3` 在 `beta1_dev2` 的功能基础上做了体验与稳定性优先的改进。
本次发布适合希望在多人服务器中稳定使用路牌/文本显示功能的测试者，同时为后续 `beta` 正式发布准备配置与本地化基础。

## 创造模式选项卡

保持不变：
- 云北城建 | 道路方块

该选项卡继续包含所有道路相关方块，新增若干快捷工具与样例模板，便于在创造模式下快速批量放置与测试。

## 本次更新亮点

- 网络同步优化：将多类 `Update*Packet` 的字段进行压缩与合并，降低了在多人场景下的带宽与处理压力。
- 客户端缓存：为常用的方块渲染数据添加缓存层，减少重复计算（特别是大量同类型路牌或护栏同时存在时）。
- 快速放置工具：添加创造模式下的“批量复制/粘贴”工具（使用木棍为默认触发物），支持沿轴复制方块与保留朝向/样式。
- Geckolib 调整：将混凝土护栏模型重命名并拆分为更小的子网格，减少单次渲染顶点量，兼容老设备的渲染路径。
- 本地化补全：补充了若干中文与英文翻译条目，修正显示占位符问题。
- 修复与稳定性：修复了导致服务端广播重复的同步 bug、修正部分方块在重载资源包时丢失自定义纹理的问题。

## 重要文件（摘录）

为便于开发者快速定位变更点，列出本次更新直接相关或新增/修改的文件：

- 主入口
  - `src/main/java/com/beigu/yunbeiuc/YunbeiUrbanConstruction.java`（小幅调整：注册阶段延迟加载部分客户端缓存）
  - `src/main/java/com/beigu/yunbeiuc/YunbeiUrbanConstructionClient.java`（新增渲染缓存初始化）
- 配置与数据
  - `src/main/resources/config/yunbeiuc.properties`（新增若干可配置项：`enableTemplateImport`, `renderCacheSize`）
  - `src/main/java/com/beigu/yunbeiuc/config/ModConfig.java`（新增配置读取/热重载逻辑）
- 路牌模板工具
  - `src/main/java/com/beigu/yunbeiuc/util/SignTemplateManager.java`（导入/导出与验证逻辑）
- 网络同步
  - `src/main/java/com/beigu/yunbeiuc/network/ModMessages.java`（合并与重命名若干消息）
  - `src/main/java/com/beigu/yunbeiuc/network/packet/UpdateSignPacket.java`（压缩字段、增加校验）
- 快捷工具与行为
  - `src/main/java/com/beigu/yunbeiuc/item/tool/ClipboardWandItem.java`（创造模式批量复制粘贴工具）
- 渲染与模型
  - `src/main/java/com/beigu/yunbeiuc/render/CrashBarrierConcreteRenderer.java`（配合 Geckolib 模型拆分优化）
  - `resources/assets/yunbeiuc/models/geo/crash_barrier_concrete/*.geo.json`（模型拆分与重命名）
- 本地化与资源
  - `src/main/resources/assets/yunbeiuc/lang/en_us.json`（补全）
  - `src/main/resources/assets/yunbeiuc/lang/zh_cn.json`（补全）

> 说明：以上路径均位于 `src/main/java/com/beigu/yunbeiuc` 与 `src/main/resources` 下，实际改动请以代码仓库为准。

## 技术

下面按技术维度列出本模组中涉及的主要技术点与其职责说明 —— 不包含具体文件或路径：

- 模组入口与生命周期
  - 负责 Mod 的初始化、注册流程以及全局事件的订阅与生命周期管理。
- 注册（Registry）
  - 管理方块、物品、方块实体、界面等对象的注册工作，保证在正确的初始化阶段进行注册。
- 方块类（Blocks）
  - 实现方块的行为、状态（朝向、变体等）、碰撞与形状、交互逻辑和物理属性。
- 方块实体 / BlockEntity
  - 负责保存复杂状态、NBT 持久化、读取/写入并在需要时与客户端/服务端同步数据。
- 物品与工具（Items）
  - 定义可交互物品、工具行为、使用逻辑以及创造模式下的专用工具交互。
- GUI / Screen / Container
  - 实现用户界面、编辑面板和交互流程，并处理客户端输入与服务端命令的协调。
- 网络（Networking）
  - 负责消息的定义、序列化/反序列化、注册与处理逻辑，保证客户端与服务端的数据一致性与安全性验证。
- 配置与热重载
  - 管理可配置项的读取、默认值与运行时热重载能力，用于在不重启的情况下调整行为（受平台限制）。
- 渲染器与模型
  - 实现自定义方块/实体的渲染逻辑、与模型/动画库的适配以及性能优化（如缓存、模型拆分）。
- 资源（Textures / Sounds / Lang）
  - 管理贴图、声音、语言本地化资源，确保资源包兼容与多语言支持。
- 工具类与数据管理
  - 提供模板管理、导入/导出、数据校验和共用工具函数等基础设施，便于复用与维护。
- 粒子与特效
  - 定义并注册粒子效果与特效工厂，用于视觉反馈与动画效果。
- 事件/监听（Event handlers）
  - 实现世界、区块、方块交互、玩家行为等事件的监听与处理逻辑。
- 第三方库与依赖
  - 说明与管理外部库（如模型/动画库、Mod 平台 API 等）的使用及兼容性注意事项。
- 构建与脚本
  - 管理构建配置、依赖声明和发布脚本，确保可复现构建流程。

> 小提示：如果你希望我列出仓库中每个 Java 类并把它们按上述类别分组，我可以扫描代码并生成一个按类别的文件清单（这会包含具体文件路径）。

## 方块与物品清单（新增/变更点）

- 路牌系列
  - 支持模板导入的限速/限重/限宽/限高/禁止类路牌。
- 道旗（Flag）
  - 修复了在大量道旗同时存在时客户端卡顿的问题，保留全部样式选项。
- 混凝土护栏
  - Geckolib 模型拆分后体积更小，渲染更稳；模型文件已重命名，注意资源引用路径。

## 开发者说明（实现细节与注意点）

- 网络与验证
  - 新的 `UpdateSignPacket` 包含简单的 CRC 校验字段与版本号，客户端与服务端需匹配协议版本（`ModMessages` 管理）。
- 配置
  - 在 `config` 中新增的开关项可以在不重启的情况下热重载（受限于 Fabric 环境），建议在多人服务器上线前在测试环境验证配置影响。
- 渲染
  - Geckolib 模型文件已拆分，渲染器中加载路径发生变更，请同步资源引用并检查资源包匹配。
- 向后兼容
  - 模板格式为向后兼容（含版本字段）。旧模板在导入时会尝试自动升级，但强烈建议先备份原模板。
---

感谢测试与反馈，欢迎在模组的反馈渠道（Modrinth、QQ群等）提交 Bug 与建议，便于我们在后续 dev4 与 beta 正式发布中持续改进。
