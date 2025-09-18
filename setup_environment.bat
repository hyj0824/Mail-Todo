@echo off
chcp 65001 >nul
echo ========================================
echo     Android 开发环境自动配置脚本
echo ========================================
echo.

:: 创建临时目录
set TEMP_DIR=%TEMP%\android_setup
mkdir "%TEMP_DIR%" 2>nul

echo [1/4] 检查 Java 环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java 未安装，正在引导下载...
    echo.
    echo 请手动下载并安装 JDK 11 或更高版本:
    echo https://adoptium.net/temurin/releases/
    echo.
    echo 下载 Windows x64 MSI 安装包，安装后重新运行此脚本
    pause
    exit /b 1
) else (
    echo ✓ Java 环境已安装
)

echo.
echo [2/4] 设置 Android SDK 路径...
set ANDROID_HOME=%USERPROFILE%\AppData\Local\Android\Sdk
set ANDROID_SDK_ROOT=%ANDROID_HOME%
echo Android SDK 将安装到: %ANDROID_HOME%

if not exist "%ANDROID_HOME%" (
    echo 创建 SDK 目录...
    mkdir "%ANDROID_HOME%"
)

echo.
echo [3/4] 下载 Android 命令行工具...
set SDK_TOOLS_URL=https://dl.google.com/android/repository/commandlinetools-win-9477386_latest.zip
set SDK_TOOLS_ZIP=%TEMP_DIR%\commandlinetools.zip

echo 正在下载 Android SDK Command Line Tools...
echo 下载地址: %SDK_TOOLS_URL%
echo.
echo 由于网络限制，请手动完成以下步骤：
echo.
echo 1. 访问: https://developer.android.com/studio/index.html#command-tools
echo 2. 下载 "Command line tools only" for Windows
echo 3. 解压到: %ANDROID_HOME%\cmdline-tools\latest\
echo 4. 运行以下命令安装必要组件:
echo.
echo    cd /d "%ANDROID_HOME%\cmdline-tools\latest\bin"
echo    sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.2"
echo.

echo.
echo [4/4] 创建环境变量设置脚本...

:: 创建环境变量设置脚本
echo @echo off > set_android_env.bat
echo set ANDROID_HOME=%ANDROID_HOME% >> set_android_env.bat
echo set ANDROID_SDK_ROOT=%ANDROID_HOME% >> set_android_env.bat
echo set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\cmdline-tools\latest\bin;%%PATH%% >> set_android_env.bat
echo echo Android 环境变量已设置 >> set_android_env.bat

echo ✓ 环境配置脚本已创建: set_android_env.bat

echo.
echo ========================================
echo           配置完成提示
echo ========================================
echo.
echo 快速配置步骤（推荐）：
echo 1. 运行 set_android_env.bat 设置环境变量
echo 2. 运行 quick_build.bat 编译应用
echo.
echo 或者使用在线编译服务（更简单）：
echo 1. 访问 GitHub Codespaces 或 Gitpod
echo 2. 上传项目代码
echo 3. 在线编译生成APK
echo.
pause
