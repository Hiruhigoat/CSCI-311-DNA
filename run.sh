cd "$(dirname "$0")"

if ! command -v javac &> /dev/null || [ ! -d "/usr/share/openjfx/lib" ]; then
    echo "OpenJFX missing. Installing required packages..."
    sudo apt update
    sudo apt install -y openjdk-17-jdk openjfx
fi

echo "Cleaning build"
rm -rf bin
mkdir -p bin
cp -r src/assets bin/ 2>/dev/null || true

echo "Compiling"
javac -d bin \
      --module-path /usr/share/openjfx/lib \
      --add-modules javafx.controls \
      $(find src -name "*.java")

if [ $? -eq 0 ]; then
    echo "Starting"
    java -cp bin \
         --module-path /usr/share/openjfx/lib \
         --add-modules javafx.controls \
         userinterface.Launcher
else
    echo "Build Failed"
fi