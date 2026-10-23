#!/bin/bash
cd "$(dirname "$0")"


ARCH=$(uname -m)
if [ "$ARCH" = "aarch64" ] || [ "$ARCH" = "arm64" ]; then
    SYS_ARCH="aarch64"
else
    SYS_ARCH="x64"
fi

if command -v javac &> /dev/null; then
    echo "[+] System JDK compiler detected"
elif [ -d "/usr/lib/jvm" ] && [ $(ls -d /usr/lib/jvm/java-17* 2>/dev/null | wc -l) -gt 0 ]; then
    echo "[+] Found system JDK in /usr/lib/jvm"
    export PATH=$(ls -d /usr/lib/jvm/java-17*/bin 2>/dev/null | tail -n 1):$PATH
elif [ -f "lib/jdk/bin/javac" ]; then
    echo "[+] Local portable JDK detected"
    export PATH="$(pwd)/lib/jdk/bin:$PATH"
else
    echo "[!] No JDK found on system. Auto-downloading portable JDK 17 for Linux ($SYS_ARCH)..."
    mkdir -p lib
    if [ "$SYS_ARCH" = "aarch64" ]; then
        curl -sL "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B7/OpenJDK17U-jdk_aarch64_linux_hotspot_17.0.10_7.tar.gz" -o lib/jdk.tar.gz
    else
        curl -sL "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.10_7.tar.gz" -o lib/jdk.tar.gz
    fi
    mkdir -p lib/jdk_temp
    tar -xzf lib/jdk.tar.gz -C lib/jdk_temp
    mv lib/jdk_temp/jdk-17* lib/jdk
    rm -rf lib/jdk.tar.gz lib/jdk_temp
    export PATH="$(pwd)/lib/jdk/bin:$PATH"
fi

JAVAFX_LIB="lib/javafx-linux-$SYS_ARCH/lib"
if [ ! -d "$JAVAFX_LIB" ]; then
    echo "[!] Downloading JavaFX SDK for Linux ($SYS_ARCH)..."
    mkdir -p lib
    curl -sL "https://download2.gluonhq.com/openjfx/17.0.10/openjfx-17.0.10_linux-${SYS_ARCH}_bin-sdk.zip" -o lib/openjfx.zip
    unzip -q lib/openjfx.zip -d lib/
    mv lib/javafx-sdk-17.0.10 "lib/javafx-linux-$SYS_ARCH"
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

echo "[+] Compiling Java source files"
javac -d bin --module-path "$JAVAFX_LIB" --add-modules javafx.controls @sources.txt
COMPILE_STATUS=$?
rm -f sources.txt

if [ $COMPILE_STATUS -eq 0 ]; then
    echo "[+] Build Successful. Launching Application"
    java -cp bin --module-path "$JAVAFX_LIB" --add-modules javafx.controls userinterface.Launcher
else
    echo "[-] Compilation failed."
    read -p "Press Enter to exit..."
fi