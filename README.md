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

## Build and Debug

1. 最简单方法 - 双击运行  
   直接双击运行：quick_build.bat
   这个脚本会自动编译项目
   生成APK文件到 app\build\outputs\apk\debug\app-debug.apk
   提供3种安装选项（USB、手动、仅编译）
2. 完整环境配置  
   如果第一次使用，按顺序运行：
   setup_environment.bat - 配置开发环境
   set_android_env.bat - 设置环境变量
   quick_build.bat - 编译应用
3. 在线编译（推荐新手）  
   使用GitHub Codespaces或Gitpod
   无需本地安装任何工具
   直接在浏览器中编译
4. 命令行手动编译
```bash
# Windows
gradlew.bat assembleDebug
# 或者
./gradlew assembleDebug
```
