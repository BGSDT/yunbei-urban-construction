# 云北城建 25w39a

[![Downloads](https://img.shields.io/badge/downloads-云北城建-bright?style=flat)](https://modrinth.com/mod/yunbeiuc)
[![QQ群](https://img.shields.io/badge/QQ-云北城建-bright?label=&logo=qq&logoColor=ffffff&color=1EBAFC&labelColor=1DB0EF&logoSize=auto)](https://qm.qq.com/q/uDgtwOJ2Ks)

> [!NOTE]
>
> - 轻量数据驱动：使用 `YunbeiUrbanConstructionDataGenerator`，将方块、物品、模型和类型尽量由数据生成，以便未来扩展。
> - 性能优化：连接型方块实现面消隐（连接时隐藏内侧面）以减少渲染开销。
> - 调试工具：提供多款魔杖（状态切换、生成水源、生成树木），加速测试流程。
> - 基础交通组件：包含道路方块、路杆/路灯基座、红绿灯等基础构件，便于搭建城市路网。

## 介绍

本次版本为最基础版本，为之后的开发打下了框架基础，模组开发采用的是 Fabric 官方提供的模板生成器，选用了数据生成，采用 Yarn 映射。

本说明在保留原始简洁说明与截图的基础上，补充了更详细的方块/物品概览、源码定位、使用玩法建议与开发者检查表，方便玩家快速上手与开发者定位代码。

### 创造模式选项卡

模组中共有 3 个创造模式选项卡：

- `云北城建 | 物品/工具`：包含调试工具、魔杖等特殊物品，便于开发者和玩家测试方块功能。
- `云北城建 | 道路方块`：收录所有道路相关方块，如路面、标线、护栏等，方便城市道路建设。
- `云北城建 | 其他方块`：包括建筑装饰与功能性方块，丰富城市景观。

每个选项卡都经过分类优化，便于查找和使用。

![image-20251112203207644](..\assets\update_beta1_dev1_group_1.png) ![image-20251112203304262](..\assets\update_beta1_dev1_group_2.png)

![image-20251112204158692](..\assets\update_beta1_dev1_group_3.png)

## 方块（详述）

### 道路方块

**道路方块**：无特殊行为，但实现了面消隐，当相邻为同类方块时取消对应面渲染，降低显卡负担。支持多种材质与配色，适配不同风格的城市道路。部分道路方块在摆放时会尝试与相邻道路对齐以提高建造效率。

![image-20251112203703706](..\assets\update_beta1_dev1_block_1.png)

代表文件（源码位置）：
- `src/main/java/com/beigu/yunbeiuc/block/RoadBlocks.java`
- `src/main/java/com/beigu/yunbeiuc/block/custom/*`（道路相关实现）

### 城建方块

**城建方块**：包含墙体、栏杆、路灯基座等可连接组件，支持多种组合方式。当前版本路杆体系为基础实现，后续会逐步扩展为可编辑/可上色的复杂体系。

![image-20251112204654632](..\assets\update_beta1_dev1_block_2.png)
### 红绿灯

红绿灯支持通过魔杖或逻辑触发器切换状态（红/绿/黄），为道路交通设计提供基础管控手段。后续版本计划加入自动化控制器与定时器支持。

![image-20251112204838081](..\assets\update_beta1_dev1_block_3.png)

代表文件（源码位置）：
- 红绿灯相关实现分散在 `block/custom` 与 `screen`（如有 GUI 配置）中，具体类请在 `src/main/java/com/beigu/yunbeiuc/block/custom/` 中查找。

### 魔杖（调试工具）

- 魔杖：用于一些方块的调试，可右键切换方块状态或属性，便于开发和测试。
- 水源魔杖：生成 3×3×3 水源方块，适合快速布置水体或测试流体行为。
- 树木魔杖：采用官方算法生成橡树样式，支持多次使用，便于快速绿化场景。

说明：图片暂不展示，物品临时占位图遵守开源协议。

## 方块与物品总览（按源码映射）

为方便开发者快速定位实现，下列为本仓库中与基础版本密切相关的文件位置：

- 主入口：`src/main/java/com/beigu/yunbeiuc/YunbeiUrbanConstruction.java`
- 客户端入口：`src/main/java/com/beigu/yunbeiuc/YunbeiUrbanConstructionClient.java`
- 数据生成：`src/main/java/com/beigu/yunbeiuc/YunbeiUrbanConstructionDataGenerator.java`
- 方块注册：`src/main/java/com/beigu/yunbeiuc/block/*`
- 物品与分组：
  - `src/main/java/com/beigu/yunbeiuc/item/ModItems.java`
  - `src/main/java/com/beigu/yunbeiuc/item/ModItemGroups.java`
- 屏幕（GUI）：`src/main/java/com/beigu/yunbeiuc/screen/*`
- 网络消息：`src/main/java/com/beigu/yunbeiuc/network/ModMessages.java`

> 注：如果需要我可以为每个具体方块生成单独的快速定位表（类名 ⇢ 功能 ⇢ 资源路径）。

## 使用与玩法建议

- 使用 `云北城建 | 道路方块` 中的道路组件搭建道路与人行道，结合路灯和红绿灯构建完整路口。
- 使用魔杖快速布置测试场景：用水源魔杖快速生成水体，或用树木魔杖快速装饰绿化带。
- 尝试组合城建方块（栏杆、墙体、框架）以生成城市装饰与隔断。

## 开发者说明与注意点

- 数据驱动优先：尽量将可变类型与模型数据放入 `DataGenerator` 生成的结构中，减小硬编码数量，便于热更新与扩展。
- 渲染性能：连接型方块使用面消隐减少渲染开销。避免在同一区域大量使用高开销渲染的实体（如复杂 Tile Entity 渲染器）。

## 开发者快速检查表（上手时请确认）

- [ ] 能否用 `./gradlew` 在开发环境中成功编译并运行客户端（在 Windows PowerShell 下使用 `.\gradlew`）。
- [ ] 资源（模型/贴图/语言文件）路径与代码中的字符串一致。

## 变更要点摘要

- 首个基础版本，建立了项目框架与常用道路/城建方块的初步实现。
- 引入数据生成器与创造模式分组，提供调试工具以加速开发。

---

感谢测试与反馈，欢迎在模组的反馈渠道（Modrinth、QQ群等）提交 Bug 与建议，便于我们在后续 dev2 中持续改进。