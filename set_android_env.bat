@echo off
echo 设置Android环境变量...
set ANDROID_HOME=%USERPROFILE%\AppData\Local\Android\Sdk
set ANDROID_SDK_ROOT=%ANDROID_HOME%
set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\cmdline-tools\latest\bin;%PATH%
echo Android环境变量已设置
echo ANDROID_HOME=%ANDROID_HOME%
echo.
echo 现在可以运行 quick_build.bat 编译应用了

