#!/bin/bash
echo "=== Android 开发环境配置 ==="

# 更新系统
sudo apt-get update

# 设置Android环境变量
export ANDROID_HOME=/usr/lib/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# 创建环境变量文件
cat >> ~/.bashrc << 'EOF'
export ANDROID_HOME=/usr/lib/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
EOF

# 重新加载环境变量
source ~/.bashrc

# 设置Gradle权限
chmod +x ./gradlew

# 下载依赖并编译（首次运行）
echo "正在下载依赖并进行首次编译..."
./gradlew assembleDebug --no-daemon

echo "=== 环境配置完成 ==="
echo "现在可以运行: ./build_codespaces.sh 来编译应用"
