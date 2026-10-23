cd "$(dirname "$0")"

JAVAFX_LIB=""

if [ -d "/usr/share/openjfx/lib" ]; then
    JAVAFX_LIB="/usr/share/openjfx/lib"
elif [ -d "lib/javafx-sdk/lib" ]; then
    JAVAFX_LIB="lib/javafx-sdk/lib"
else
    echo "System JavaFX not found. Downloading local JavaFX SDK"
    mkdir -p lib
    wget -q --show-progress https://download2.gluonhq.com/openjfx/17.0.10/openjfx-17.0.10_linux-x64_bin-sdk.zip -O lib/openjfx.zip
    unzip -q lib/openjfx.zip -d lib/
    mv lib/javafx-sdk-17.0.10 lib/javafx-sdk
    rm lib/openjfx.zip
    JAVAFX_LIB="lib/javafx-sdk/lib"
fi

echo "[+] JavaFX Library Path: $JAVAFX_LIB"

rm -rf bin
mkdir -p bin

cp -r src/assets bin/ 2>/dev/null || true

echo "Compiling"
javac -d bin \
      --module-path "$JAVAFX_LIB" \
      --add-modules javafx.controls \
      $(find src -name "*.java")

if [ $? -eq 0 ]; then
    echo "[+] Build Successful"
    java -cp bin \
         --module-path "$JAVAFX_LIB" \
         --add-modules javafx.controls \
         userinterface.Launcher
else
    echo "Compilation failed."
fi