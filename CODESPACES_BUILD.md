# GitHub Codespaces 编译指南

## 🚀 在 GitHub Codespaces 中编译 Android 应用

### 方法1：自动化一键编译（推荐）

1. **打开 Codespaces**
   - 在 GitHub 项目页面点击绿色的 `Code` 按钮
   - 选择 `Codespaces` → `Create codespace on main`
   - 等待环境初始化（约2-3分钟）

2. **运行编译脚本**
   ```bash
   chmod +x build_codespaces.sh
   ./build_codespaces.sh
   ```

3. **下载 APK**
   - 编译完成后，在左侧文件管理器找到：`app/build/outputs/apk/debug/app-debug.apk`
   - 右键点击 → `Download`

### 方法2：手动编译

```bash
# 1. 设置权限
chmod +x gradlew

# 2. 清理项目
./gradlew clean

# 3. 编译调试版本
./gradlew assembleDebug

# 4. 编译发布版本（可选）
./gradlew assembleRelease
```

### 方法3：使用 VS Code 任务

按 `Ctrl+Shift+P` 打开命令面板，输入 `Tasks: Run Task`，选择：
- `Build Debug APK`
- `Build Release APK`
- `Clean Project`

## 📱 下载和安装 APK

### 从 Codespaces 下载
1. **文件管理器下载**：
   - 左侧文件管理器 → `app/build/outputs/apk/debug/`
   - 右键 `app-debug.apk` → `Download`

2. **命令行查看**：
   ```bash
   # 查看 APK 信息
   ls -la app/build/outputs/apk/debug/
   
   # 创建下载脚本
   ./download_apk.sh
   ```

### 安装到手机
1. **直接传输**：将下载的 APK 文件传输到手机
2. **启用安装**：设置 → 安全 → 允许未知来源
3. **点击安装**：在手机上点击 APK 文件进行安装

## 🔧 常见问题解决

### 编译失败
```bash
# 查看详细错误
./gradlew assembleDebug --stacktrace --info

# 清理后重新编译
./gradlew clean assembleDebug
```

### 依赖下载慢
项目已配置阿里云镜像，通常下载速度较快。如仍有问题：
```bash
# 强制刷新依赖
./gradlew assembleDebug --refresh-dependencies
```

### 内存不足
```bash
# 使用较少内存编译
./gradlew assembleDebug -Dorg.gradle.jvmargs="-Xmx1g"
```

## ⚡ 编译加速技巧

1. **使用 Gradle Daemon**：
   ```bash
   echo "org.gradle.daemon=true" >> gradle.properties
   ```

2. **并行编译**：
   ```bash
   echo "org.gradle.parallel=true" >> gradle.properties
   ```

3. **缓存优化**：
   ```bash
   echo "org.gradle.caching=true" >> gradle.properties
   ```

## 📊 编译结果

编译成功后你会得到：
- **调试版 APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **发布版 APK**: `app/build/outputs/apk/release/app-release.apk` (如果编译发布版)

APK 文件大小约 15-25MB，包含所有必要的依赖库。

## 🎯 快速开始命令

```bash
# 一键编译
./build_codespaces.sh

# 查看编译状态
./gradlew tasks

# 清理项目
./gradlew clean
```
