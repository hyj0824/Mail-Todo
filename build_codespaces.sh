#!/bin/bash

#########################################################################
# Android Mail Todo App - Codespaces 编译脚本
# 适用于 GitHub Codespaces 环境
#########################################################################

set -e  # 遇到错误时退出

echo "========================================"
echo "   GitHub Codespaces - Android 编译器"
echo "========================================"
echo

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查环境
echo -e "${YELLOW}[1/5]${NC} 检查编译环境..."
if [ ! -f "./gradlew" ]; then
    echo -e "${RED}错误: gradlew 文件不存在${NC}"
    exit 1
fi

# 设置Android环境变量
export ANDROID_HOME=/usr/local/lib/android
export ANDROID_SDK_ROOT=/usr/local/lib/android
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
echo "✓ Android SDK: $ANDROID_HOME"

# 设置权限
chmod +x ./gradlew

# 清理项目
echo -e "${YELLOW}[2/5]${NC} 清理项目..."
./gradlew clean --no-daemon

# 下载依赖
echo -e "${YELLOW}[3/5]${NC} 下载依赖..."
./gradlew dependencies --no-daemon

# 编译项目
echo -e "${YELLOW}[4/5]${NC} 编译应用..."
./gradlew assembleDebug --no-daemon --stacktrace

# 检查APK
echo -e "${YELLOW}[5/5]${NC} 检查编译结果..."
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo -e "${GREEN}✓ 编译成功！${NC}"
    echo
    echo "APK 文件信息："
    echo "  路径: $APK_PATH"
    echo "  大小: $APK_SIZE"
    echo
    echo "下载方法："
    echo "1. 在左侧文件资源管理器中找到: $APK_PATH"
    echo "2. 右键点击 -> Download"
    echo "3. 或者运行: ./download_apk.sh"
    echo

    # 创建下载链接
    echo "creating download script..."
    cat > download_apk.sh << 'EOF'
#!/bin/bash
echo "APK file location: $(pwd)/app/build/outputs/apk/debug/app-debug.apk"
echo "You can download it by:"
echo "1. Right-click on the file in VS Code Explorer"
echo "2. Select 'Download'"
echo "3. Or use the VS Code command palette: Ctrl+Shift+P -> 'Download'"
EOF
    chmod +x download_apk.sh

else
    echo -e "${RED}✗ 编译失败！APK 文件未找到${NC}"
    echo "请检查编译日志中的错误信息"
    exit 1
fi

echo
echo "========================================"
echo -e "${GREEN}      编译完成！${NC}"
echo "========================================"