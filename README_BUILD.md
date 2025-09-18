# Android 邮件应用 - 无需 Android Studio 编译指南

## 🚀 快速开始（3种方法）

### 方法1：本地命令行编译（推荐）

#### 前提条件
1. **安装 Java JDK 11+**
   - 下载地址：https://adoptium.net/temurin/releases/
   - 选择 Windows x64 MSI 安装包
   - 安装后验证：在命令行输入 `java -version`

2. **运行自动化脚本**
   ```bash
   # 1. 双击运行环境配置
   setup_environment.bat
   
   # 2. 设置环境变量
   set_android_env.bat
   
   # 3. 编译应用
   quick_build.bat
   ```

#### 详细步骤
1. **配置环境**：运行 `setup_environment.bat` 
2. **编译应用**：运行 `quick_build.bat`
3. **安装到手机**：选择USB安装或手动安装

---

### 方法2：在线编译（最简单）

#### GitHub Codespaces（推荐）
1. 将项目上传到 GitHub
2. 点击 "Code" → "Codespaces" → "Create codespace"
3. 在在线环境中运行：
   ```bash
   ./gradlew assembleDebug
   ```
4. 下载生成的 APK：`app/build/outputs/apk/debug/app-debug.apk`

#### Gitpod（备选）
1. 访问：https://gitpod.io/
2. 输入你的 GitHub 项目链接
3. 自动创建在线开发环境
4. 编译并下载 APK

---

### 方法3：使用预构建工具

#### AppCenter（微软）
1. 访问：https://appcenter.ms/
2. 连接 GitHub 仓库
3. 配置自动构建
4. 获取构建的 APK

---

## 📱 手机安装步骤

### USB 连接安装
1. **启用开发者选项**：
   - 设置 → 关于手机 → 版本号（连点7次）
2. **启用 USB 调试**：
   - 设置 → 开发者选项 → USB 调试
3. **连接电脑**：使用 USB 数据线
4. **运行安装脚本**：选择 USB 安装选项

### 手动安装
1. **传输 APK**：将 `app-debug.apk` 复制到手机
2. **允许未知来源**：设置 → 安全 → 未知来源
3. **安装应用**：在手机上点击 APK 文件

---

## 🛠️ 常见问题解决

### 编译失败
```bash
# 清理项目
./gradlew clean

# 重新编译
./gradlew assembleDebug --stacktrace
```

### 依赖下载失败
```bash
# 使用阿里云镜像（在 build.gradle 中添加）
repositories {
    maven { url 'https://maven.aliyun.com/repository/google' }
    maven { url 'https://maven.aliyun.com/repository/central' }
    google()
    mavenCentral()
}
```

### ADB 连接问题
1. 重新插拔 USB 线
2. 重启 ADB：`adb kill-server && adb start-server`
3. 检查驱动程序安装

---

## 📋 文件说明

- `quick_build.bat` - 快速编译脚本
- `setup_environment.bat` - 环境配置脚本
- `set_android_env.bat` - 环境变量设置
- `build_and_install.bat` - 完整构建安装脚本

---
## 🔧 高级选项

### 自定义签名
```bash
# 生成签名密钥
keytool -genkey -v -keystore my-release-key.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000

# 签名 APK
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore my-release-key.keystore app-debug.apk my-key-alias
```

### 优化 APK 大小
```bash
# 构建发布版本
./gradlew assembleRelease

# 生成 AAB 包（Google Play）
./gradlew bundleRelease
```

---

## 📞 技术支持

如果遇到问题，请检查：
1. Java 版本是否正确（JDK 11+）
2. 网络连接是否正常
3. 磁盘空间是否充足（至少 5GB）
4. 防火墙是否阻止了 Gradle 下载

建议使用在线编译方案，避免本地环境配置问题。

