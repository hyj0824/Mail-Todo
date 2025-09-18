# MailToDo


## 核心功能
- 多邮箱支持 - 支持Outlook和QQ邮箱的IMAP连接
- 智能日期识别 - 自动识别邮件内容中的中英文日期信息 
- 内容总结 - 自动总结邮件关键内容 
- Microsoft To Do集成 - 一键同步到Microsoft To Do应用

## 技术架构
- MVVM架构 - 使用ViewModel和LiveData
- 依赖注入 - 使用Hilt进行依赖管理 
- 数据库 - Room数据库存储邮件和待办事项 
- 后台同步 - WorkManager实现后台邮件同步 
- 现代UI - Material Design 3界面设计

## 主要组件
- 数据层: Room数据库、DAO、实体类 
- 服务层: 邮件服务、日期提取服务、内容摘要服务、Microsoft集成服务 
- UI层: 主界面、邮箱设置界面、Microsoft认证界面 
- 后台服务: 邮件同步Worker、后台服务

## 如何使用

### 添加邮箱账户 
- 点击"添加邮箱"按钮 
- 选择邮箱类型（Outlook/QQ邮箱） 
- 输入邮箱地址和应用专用密码 
- 系统会自动配置IMAP服务器设置 
- 连接Microsoft To Do 
- 点击"连接微软"按钮 
- 完成OAuth认证流程 
### 智能日程功能 
- 应用会自动扫描邮件内容 
- 识别日期相关信息（如"明天下午2点开会"） 
- 点击邮件可快速创建待办事项 
- 自动同步到Microsoft To Do 

## 安全特性 
- IMAP SSL加密连接 
- 密码本地存储（生产环境建议加密） 
- OAuth 2.0认证Microsoft服务

这个应用已经具备了完整的功能框架，可以直接编译运行。如果需要在生产环境使用，建议添加密码加密、错误处理优化、网络状态检查等增强功能。

## 编译说明

### 在 GitHub Codespaces 中编译（推荐）

这个项目已经完全配置好了在 GitHub Codespaces 环境中的编译环境。

#### 快速编译
```bash
# 编译 Debug 版本 APK
./build_apk.sh

# 编译 Release 版本 APK  
./build_release.sh
```

#### 手动编译
```bash
# 清理项目
./gradlew clean --no-daemon

# 编译 Debug APK
./gradlew assembleDebug --no-daemon

# 编译 Release APK
./gradlew assembleRelease --no-daemon
```

#### 生成的文件位置
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `app/build/outputs/apk/release/app-release-unsigned.apk`

### 本地开发环境

如果需要在本地开发，需要安装：

1. **Java 11 或更高版本**
2. **Android SDK** (API Level 34)
3. **Android Studio** (可选，用于调试)

#### 本地编译命令
```bash
# Windows
gradlew.bat assembleDebug

# Linux/macOS  
./gradlew assembleDebug
```

### 项目状态

当前版本是一个**简化版本**，包含：
- ✅ 基本的Android项目结构
- ✅ Material Design 3 UI框架
- ✅ 可编译的APK
- ✅ 基础的MainActivity

**暂时移除的功能**（由于Java 21兼容性）：
- ⏸️ Hilt 依赖注入（需要迁移到KSP）
- ⏸️ Room 数据库（需要启用编译器）
- ⏸️ 复杂的业务逻辑类

### 恢复完整功能

要恢复完整功能，需要：

1. **迁移到KSP**（替代Kapt）
2. **重新添加业务逻辑类**
3. **启用Room数据库编译器**

这个版本主要用于演示完整的Android项目编译流程。
