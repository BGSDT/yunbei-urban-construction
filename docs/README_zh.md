# 云北城建<img src="assets/logo.png" align="right" width="180px"/>

English ([Standard](README.md)) | **中文** ([简体](README_zh.md))

---

[![QQ群](https://img.shields.io/badge/QQ-云北城建-bright?label=&logo=qq&logoColor=ffffff&color=1EBAFC&labelColor=1DB0EF&logoSize=auto)](https://qm.qq.com/q/uDgtwOJ2Ks)[![Modrinth](https://img.shields.io/modrinth/dt/yunbeiuc?logo=modrinth&label=Modrhtih&color=242629&labelColor=5CA424&logoColor=FFFFFF)](https://modrinth.com/mod/yunbeiuc)[![Curseforge](https://img.shields.io/curseforge/dt/1644040?logo=curseforge&label=Curseforge&color=242629&labelColor=F16436&logoColor=FFFFFF)](https://www.curseforge.com/minecraft/mc-mods/yunbeiuc)

**中文** (**简体**)

## 简介

云北城建是一款**城市建设**模组，专注于**城市交通基础设施还原**，集道路方块、道路标识、市政设施、交通设备及实用道具于一体，均按照现实还原。

**方块物品：**

- **道路方块**：提供全套标准化道路方块体系，涵盖基础道路及白黄双色、单双线、粗标线、斜角标线、直角标线、T型标线、箭头标线、十字标线、菱形标线等多种标线样式，所有纹理均严格参照国家现行交通规范依据 GB 5768.3-2025 设计，确保规范性。
- **道路标线**：包含直行、转弯、掉头、合流、减速让行、停车让行、人行横道预告、车距确认、最低/最高限速、专用车道（公交、非机动车、电动自行车、多乘员车辆等）及残疾人专用等各类地面标识，依据 GB 5768.3-2025 标准制作，满足道路场景构建需求。
- **道路标识**：还原交通标志库，涵盖交叉路口、急弯、陡坡、窄桥、隧道、注意行人/儿童/非机动车、禁止通行、限制速度/高度/宽度/质量、停车让行、会车让行、单行路、环岛行驶、车道指引、停车位等数百种标志共计366个模型，参照 GB 5768.2-2022 标准。
- **市政设施**：涵盖道路照明灯、路杆组件、文字显示屏、可自定义旗帜、检测摄像头、雷达测速器、补光灯、龙门架系统（含LED屏）、限高杆、警示网、防眩网/防眩板、多色系隔离墩（含斜向/双格/斜切面变体）、反光标识、安全岛（含边缘条及内部填充）、仪器杆组件、高速公路护栏（铁质/绿色）、道路花箱、垃圾桶、施工路障、警示柱、铁马、防撞桶、交通锥、减速带、振动标线、车位挡轮杆等模型，覆盖城市道路与公路配套设施。
- **交通设备**：提供可自动联动的交通信号灯系统（直行/左转/人行道），支持魔杖切换与链接魔杖联动控制；包含道闸系统（闸机主体、台阶型、横杆/纵杆）、限高杆、铁马、警示柱、护栏等多类交通控制与安全防护设备，满足交通管理场景。
- **实用道具**：配备多类自定义功能工具——通用魔杖、树木魔杖、链接魔杖、水源魔杖、旋转魔杖、文本复制魔杖，提升场景搭建与交互效率。

## 画廊

| ![[YUC]云北城建 (Yunbei Urban Construction)-第2张图片](https://i.mcmod.cn/editor/upload/20260717/1784300728_1045433_SeNs.webp) | ![[YUC]云北城建 (Yunbei Urban Construction)-第3张图片](https://i.mcmod.cn/editor/upload/20260717/1784300853_1045433_jOca.webp) | ![[YUC]云北城建 (Yunbei Urban Construction)-第4张图片](https://i.mcmod.cn/editor/upload/20260717/1784302090_1045433_GVFe.webp) | ![[YUC]云北城建 (Yunbei Urban Construction)-第5张图片](https://i.mcmod.cn/editor/upload/20260717/1784300729_1045433_yxEt.webp) |
|:----------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|
|                                                        自定义相位红绿灯                                                        |                                                        人行道交通信号灯                                                        |                                                          道路标识                                                          |                                                         龙门架体系                                                          |

## 下载

请前往 [Modrinth 项目页](https://modrinth.com/mod/yunbeiuc) [CurseForge 项目页](https://www.curseforge.com/minecraft/mc-mods/yunbeiuc) 下载最新版本。

## 开源协议

本项目采用 MIT 开源协议发布。

- 你可以自由使用、复制、修改、合并、发布、分发、再授权和出售本软件及其副本。
- 使用和分发时必须保留原作者的版权声明和许可声明。
- 本软件按“原样”提供，不作任何明示或暗示的担保，作者不承担任何因使用本软件产生的责任。

## 本地构建

- `versions/1.20.1`：Fabric + Forge，Java 17
- `versions/1.21.1`：Fabric + NeoForge，Java 21

```bash
cd versions/1.20.1
./gradlew clean build

cd ../1.21.1
./gradlew clean build
```

GitHub Actions 会分别构建两个目录，并上传四个平台的可分发 JAR。

## 联系与交流

加入 [QQ群](https://qm.qq.com/q/uDgtwOJ2Ks) 或在 [Github Issues](https://github.com/BGSDT/yunbei-urban-construction/issues) 反馈问题。
