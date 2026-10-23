#!/bin/bash
cd "$(dirname "$0")"

echo "=================================================="
echo "    DNA Sequence Alignment Studio - macOS Build   "
echo "=================================================="

# 1. Detect Architecture (Apple Silicon arm64 vs Intel x86_64)
ARCH=$(uname -m)
if [ "$ARCH" = "arm64" ]; then
    JFX_ARCH="aarch64"
    echo "[+] Architecture: Apple Silicon ($ARCH)"
else
    JFX_ARCH="x64"
    echo "[+] Architecture: Intel ($ARCH)"
fi

# 2. Check for System JDK
if command -v javac &> /dev/null; then
    echo "[+] System JDK compiler detected!"
elif [ -d "/Library/Java/JavaVirtualMachines" ] && [ $(ls -A /Library/Java/JavaVirtualMachines 2>/dev/null | wc -l) -gt 0 ]; then
    echo "[+] Found macOS JDK in /Library/Java/JavaVirtualMachines"
    export PATH=$(ls -d /Library/Java/JavaVirtualMachines/*/Contents/Home/bin 2>/dev/null | tail -n 1):$PATH
elif [ -f "lib/jdk/Contents/Home/bin/javac" ]; then
    echo "[+] Local portable JDK detected!"
    export PATH="$(pwd)/lib/jdk/Contents/Home/bin:$PATH"
else
    echo "[!] No JDK found on system. Auto-downloading portable JDK 17 for macOS ($ARCH)..."
    mkdir -p lib
    if [ "$ARCH" = "arm64" ]; then
        curl -sL "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B7/OpenJDK17U-jdk_aarch64_mac_hotspot_17.0.10_7.tar.gz" -o lib/jdk.tar.gz
    else
        curl -sL "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B7/OpenJDK17U-jdk_x64_mac_hotspot_17.0.10_7.tar.gz" -o lib/jdk.tar.gz
    fi
    mkdir -p lib/jdk_temp
    tar -xzf lib/jdk.tar.gz -C lib/jdk_temp
    mv lib/jdk_temp/jdk-17* lib/jdk
    rm -rf lib/jdk.tar.gz lib/jdk_temp
    export PATH="$(pwd)/lib/jdk/Contents/Home/bin:$PATH"
fi

# 3. Auto-fetch JavaFX SDK for macOS
JAVAFX_LIB="lib/javafx-mac-$JFX_ARCH/lib"
if [ ! -d "$JAVAFX_LIB" ]; then
    echo "[!] Downloading JavaFX SDK for macOS ($JFX_ARCH)..."
    mkdir -p lib
    curl -sL "https://download2.gluonhq.com/openjfx/17.0.10/openjfx-17.0.10_osx-${JFX_ARCH}_bin-sdk.zip" -o lib/openjfx.zip
    unzip -q lib/openjfx.zip -d lib/
    mv lib/javafx-sdk-17.0.10 "lib/javafx-mac-$JFX_ARCH"
    rm -f lib/openjfx.zip
fi

# 4. Clean and compile
rm -rf bin
mkdir -p bin

if [ -d "src/assets" ]; then
    mkdir -p bin/assets
    cp -r src/assets/* bin/assets/ 2>/dev/null
fi

find src -name "*.java" > sources.txt

echo "[+] Compiling Java source files..."
javac -d bin --module-path "$JAVAFX_LIB" --add-modules javafx.controls @sources.txt
COMPILE_STATUS=$?
rm -f sources.txt

if [ $COMPILE_STATUS -eq 0 ]; then
    echo "[+] Build Successful! Launching Application..."
    java -cp bin --module-path "$JAVAFX_LIB" --add-modules javafx.controls userinterface.Launcher
else
    echo "[-] Compilation failed."
    read -p "Press Enter to exit..."
fi