@echo off
chcp 65001 >nul
echo ==========================================
echo     Android 邮件应用 - 快速编译脚本
echo ==========================================
echo.

:: 检查gradlew是否存在
if not exist "gradlew.bat" (
    echo 错误: gradlew.bat 不存在，正在创建...
    call :create_gradlew
)

:: 简单编译
echo [1/3] 开始编译...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo.
    echo 编译失败！可能的原因：
    echo 1. 没有安装JDK 8+
    echo 2. 没有下载Android SDK
    echo 3. 网络连接问题
    echo.
    echo 解决方案：
    echo 1. 安装JDK: https://adoptium.net/
    echo 2. 运行: setup_environment.bat
    pause
    exit /b 1
)

echo [2/3] 编译成功！查找APK文件...
set APK_FILE=app\build\outputs\apk\debug\app-debug.apk
if exist "%APK_FILE%" (
    echo.
    echo ✓ APK已生成: %APK_FILE%
    echo 文件大小:
    dir "%APK_FILE%" | find "app-debug.apk"
) else (
    echo 错误: APK文件未找到
    exit /b 1
)

echo.
echo [3/3] 选择安装方式：
echo 1. USB连接手机安装 (需要ADB)
echo 2. 手动传输安装
echo 3. 仅编译，稍后安装
echo.
set /p choice=请选择 (1/2/3):

if "%choice%"=="1" goto usb_install
if "%choice%"=="2" goto manual_install
if "%choice%"=="3" goto finish

:usb_install
echo.
echo 正在通过USB安装...
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo ADB未找到，请先运行 setup_environment.bat 安装SDK
    goto manual_install
)

adb devices
echo.
echo 如果上面显示了你的设备，按回车继续安装
pause
adb install -r "%APK_FILE%"
if %errorlevel% equ 0 (
    echo.
    echo ✓ 安装成功！可以在手机上找到"邮件待办助手"应用
) else (
    echo 安装失败，请检查：
    echo 1. 手机是否开启USB调试
    echo 2. 是否允许USB安装应用
    echo 3. 手机是否信任此电脑
)
goto finish

:manual_install
echo.
echo 手动安装步骤：
echo 1. 将以下APK文件传输到手机：
echo    %CD%\%APK_FILE%
echo 2. 在手机上找到该文件并点击安装
echo 3. 可能需要在设置中允许"未知来源"应用安装
goto finish

:finish
echo.
echo ==========================================
echo              编译完成！
echo ==========================================
pause
exit /b 0

:create_gradlew
echo 创建gradlew.bat...
echo @echo off > gradlew.bat
echo set DIRNAME=%%~dp0 >> gradlew.bat
echo if "%%DIRNAME%%" == "" set DIRNAME=. >> gradlew.bat
echo set APP_BASE_NAME=%%~n0 >> gradlew.bat
echo set APP_HOME=%%DIRNAME%% >> gradlew.bat
echo %%APP_HOME%%\gradle\wrapper\gradle-wrapper.jar %%* >> gradlew.bat
goto :eof
