#!/bin/bash

#########################################################################
# Android 环境快速配置脚本 - Codespaces
#########################################################################

echo "🔧 配置 Android 开发环境..."

# 检查并设置 Android SDK 路径
setup_android_sdk() {
    echo "📱 配置 Android SDK..."
    
    # 尝试不同的可能路径
    POSSIBLE_PATHS=(
        "/opt/android-sdk-linux"
        "/usr/lib/android-sdk"
        "/android-sdk-linux"
        "$HOME/android-sdk"
    )
    
    for path in "${POSSIBLE_PATHS[@]}"; do
        if [ -d "$path" ]; then
            export ANDROID_HOME="$path"
            export ANDROID_SDK_ROOT="$ANDROID_HOME"
            echo "✅ 找到 Android SDK: $ANDROID_HOME"
            break
        fi
    done
    
    if [ -z "$ANDROID_HOME" ]; then
        echo "❌ Android SDK 未找到，尝试安装..."
        
        # 创建SDK目录
        mkdir -p $HOME/android-sdk
        export ANDROID_HOME="$HOME/android-sdk"
        export ANDROID_SDK_ROOT="$ANDROID_HOME"
        
        # 下载命令行工具
        cd $HOME
        wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
        unzip -q commandlinetools-linux-9477386_latest.zip
        mkdir -p $ANDROID_HOME/cmdline-tools
        mv cmdline-tools $ANDROID_HOME/cmdline-tools/latest
        rm commandlinetools-linux-9477386_latest.zip
        
        echo "✅ Android SDK 安装完成"
    fi
    
    # 设置PATH
    export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
    
    # 写入环境变量到 ~/.bashrc
    {
        echo ""
        echo "# Android SDK Environment"
        echo "export ANDROID_HOME=\"$ANDROID_HOME\""
        echo "export ANDROID_SDK_ROOT=\"\$ANDROID_HOME\""
        echo "export PATH=\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools"
    } >> ~/.bashrc
}

# 安装必要的Android组件
install_android_components() {
    echo "📦 安装 Android 组件..."
    
    # 接受许可证
    yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses >/dev/null 2>&1
    
    # 安装必要组件
    $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
        "platform-tools" \
        "platforms;android-34" \
        "platforms;android-33" \
        "build-tools;34.0.0" \
        "build-tools;33.0.1" >/dev/null 2>&1
        
    echo "✅ Android 组件安装完成"
}

# 检查Java环境
check_java() {
    echo "☕ 检查 Java 环境..."
    
    if command -v java >/dev/null 2>&1; then
        JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
        echo "✅ Java 版本: $JAVA_VERSION"
    else
        echo "❌ Java 未安装，请检查 devcontainer 配置"
        exit 1
    fi
}

# 主要设置流程
main() {
    echo "======================================"
    echo "🚀 Android 环境配置器"
    echo "======================================"
    
    check_java
    setup_android_sdk
    
    # 检查是否在 devcontainer 中，如果是则不需要手动安装组件
    if [ -f "/.dockerenv" ]; then
        echo "🐳 检测到 Docker 环境，使用预配置的 Android SDK"
    else
        install_android_components
    fi
    
    echo ""
    echo "======================================"
    echo "✅ 环境配置完成！"
    echo "======================================"
    echo "环境信息:"
    echo "  ANDROID_HOME: $ANDROID_HOME"
    echo "  Java: $(java -version 2>&1 | head -1)"
    echo ""
    echo "现在可以运行编译脚本:"
    echo "  ./build_codespaces.sh"
    echo "======================================"
}

# 执行主要流程
main