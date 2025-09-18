#!/bin/bash

echo "🚀 GitHub Codespaces 快速编译启动器"
echo "=========================================="

# 检查是否为首次运行
if [ ! -f ".codespaces_initialized" ]; then
    echo "⚙️  首次初始化 Codespaces 环境..."

    # 设置权限
    chmod +x gradlew
    chmod +x build_codespaces.sh

    # 创建gradle.properties优化配置
    cat >> gradle.properties << 'EOF'

# Codespaces 优化配置
org.gradle.daemon=false
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
org.gradle.jvmargs=-Xmx2g -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
EOF

    # 标记已初始化
    touch .codespaces_initialized
    echo "✅ 环境初始化完成"
fi

echo ""
echo "🎯 选择编译选项："
echo "1. 快速编译 (调试版)"
echo "2. 完整编译 (包含清理)"
echo "3. 发布版编译"
echo "4. 仅清理项目"
echo ""

read -p "请选择 (1-4): " choice

case $choice in
    1)
        echo "🔨 开始快速编译..."
        ./gradlew assembleDebug --no-daemon
        ;;
    2)
        echo "🧹 清理并完整编译..."
        ./build_codespaces.sh
        ;;
    3)
        echo "📦 编译发布版..."
        ./gradlew assembleRelease --no-daemon
        ;;
    4)
        echo "🧹 清理项目..."
        ./gradlew clean --no-daemon
        ;;
    *)
        echo "❌ 无效选择，默认执行快速编译"
        ./gradlew assembleDebug --no-daemon
        ;;
esac

# 检查编译结果
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ] || [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    echo ""
    echo "🎉 编译成功完成！"
    echo ""
    echo "📱 APK 文件位置："
    find app/build/outputs/apk -name "*.apk" -type f | while read apk; do
        size=$(du -h "$apk" | cut -f1)
        echo "  📁 $apk (大小: $size)"
    done
    echo ""
    echo "💾 下载方法："
    echo "  1. 在左侧文件管理器中找到 APK 文件"
    echo "  2. 右键点击 → Download"
    echo "  3. 或使用 VS Code 命令面板: Ctrl+Shift+P → 'Download'"
fi
