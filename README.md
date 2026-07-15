# Yunbei Urban Construction

本仓库在同一个 `master` 分支维护两个 Minecraft 版本：

- `versions/1.20.1`：Fabric + Forge，Java 17
- `versions/1.21.1`：Fabric + NeoForge，Java 21

## 本地构建

```bash
cd versions/1.20.1
./gradlew clean build

cd ../1.21.1
./gradlew clean build
```

GitHub Actions 会分别构建两个目录，并上传四个平台的可分发 JAR。
