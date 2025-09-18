#!/bin/bash

# Mail-Todo Android 应用构建脚本
# 在 GitHub Codespaces 环境中使用

set -e

echo "======================================"
echo "Mail-Todo Android 应用构建脚本"
echo "======================================"

# 检查Java版本
echo "检查 Java 环境..."
java -version

# 检查Android SDK
echo ""
echo "检查 Android SDK..."
if [ -z "$ANDROID_HOME" ]; then
    echo "⚠️  警告：ANDROID_HOME 环境变量未设置"
    echo "使用默认路径：/usr/lib/android-sdk"
    export ANDROID_HOME=/usr/lib/android-sdk
fi

if [ -d "$ANDROID_HOME" ]; then
    echo "✓ Android SDK 路径：$ANDROID_HOME"
else
    echo "❌ Android SDK 未找到：$ANDROID_HOME"
    exit 1
fi

# 清理之前的构建
echo ""
echo "清理之前的构建..."
./gradlew clean --no-daemon

# 开始构建
echo ""
echo "开始构建 Debug APK..."
./gradlew assembleDebug --no-daemon

# 检查构建结果
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(ls -lh "$APK_PATH" | awk '{print $5}')
    echo ""
    echo "✅ 构建成功！"
    echo "📱 APK 文件：$APK_PATH"
    echo "📦 文件大小：$APK_SIZE"
    echo ""
    echo "你可以下载此 APK 文件并在 Android 设备上安装。"
    echo "APK 文件完整路径：$(realpath $APK_PATH)"
else
    echo ""
    echo "❌ 构建失败：APK 文件未生成"
    exit 1
fi

echo ""
echo "======================================"
echo "构建完成"
echo "======================================"