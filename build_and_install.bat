@echo off
echo ========================================
echo   Android 邮件应用编译打包脚本
echo ========================================
echo.

:: 检查Java环境
echo [1/7] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请先安装JDK 8或更高版本
    pause
    exit /b 1
)
echo Java环境检查通过

:: 设置环境变量
echo [2/7] 设置环境变量...
set ANDROID_HOME=%USERPROFILE%\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools;%PATH%

:: 检查Android SDK
echo [3/7] 检查Android SDK...
if not exist "%ANDROID_HOME%" (
    echo 警告: Android SDK未找到，将尝试自动下载...
    call :download_sdk
)

:: 清理项目
echo [4/7] 清理项目...
gradlew clean
if %errorlevel% neq 0 (
    echo 错误: 项目清理失败
    pause
    exit /b 1
)

:: 编译项目
echo [5/7] 编译项目...
gradlew assembleDebug
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败
    pause
    exit /b 1
)

:: 查找生成的APK
echo [6/7] 查找生成的APK...
set APK_PATH=app\build\outputs\apk\debug\app-debug.apk
if not exist "%APK_PATH%" (
    echo 错误: APK文件未找到
    pause
    exit /b 1
)
echo APK文件已生成: %APK_PATH%

:: 检查设备连接
echo [7/7] 检查设备连接...
adb devices
echo.
echo 请确保已启用USB调试并连接手机
echo 是否要安装到手机? (Y/N)
set /p install_choice=
if /i "%install_choice%"=="Y" (
    echo 正在安装到手机...
    adb install -r "%APK_PATH%"
    if %errorlevel% equ 0 (
        echo 安装成功！
        echo 可以在手机上找到"邮件待办助手"应用
    ) else (
        echo 安装失败，请检查设备连接和USB调试设置
    )
) else (
    echo APK文件位置: %CD%\%APK_PATH%
    echo 你可以手动传输到手机进行安装
)

echo.
echo 编译打包完成！
pause
exit /b 0

:download_sdk
echo 正在下载Android SDK命令行工具...
mkdir "%ANDROID_HOME%" 2>nul
cd /d "%ANDROID_HOME%"
:: 这里需要手动下载SDK，实际使用时应该提供下载链接
echo 请手动下载Android SDK Command Line Tools并解压到:
echo %ANDROID_HOME%
echo 下载地址: https://developer.android.com/studio/index.html#command-tools
pause
goto :eof
