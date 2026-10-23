@echo off
setlocal enabledelayedexpansion
title DNA Sequence Alignment Studio - Windows Build
echo ==================================================
echo   DNA Sequence Alignment Studio - Windows Build
echo ==================================================

:: 1. Check if javac is in system PATH
where javac >nul 2>nul
if %ERRORLEVEL% EQU 0 goto JDK_FOUND

:: 2. Check JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set "PATH=%JAVA_HOME%\bin;%PATH%"
        goto JDK_FOUND
    )
)

:: 3. Check VS Code extensions, user profile, and system JDK locations
for %%D in (
    "%USERPROFILE%\.vscode\extensions"
    "%USERPROFILE%\AppData\Local\Programs"
    "C:\Program Files\Eclipse Adoptium"
    "C:\Program Files\Java"
    "C:\Program Files\Microsoft"
    "C:\Program Files\Amazon Corretto"
    "C:\Program Files\Zulu"
) do (
    if exist "%%~D" (
        for /f "delims=" %%J in ('dir /b /s "%%~D\javac.exe" 2^>nul') do (
            set "JAVAC_PATH=%%~dpJ"
            set "PATH=!JAVAC_PATH!;%PATH%"
            goto JDK_FOUND
        )
    )
)

:: 4. Check local portable folder
if exist "lib\jdk\bin\javac.exe" (
    set "PATH=%CD%\lib\jdk\bin;%PATH%"
    goto JDK_FOUND
)

:: 5. Download Portable JDK 17 if no JDK is found on the entire machine
echo [!] No JDK found on system or VS Code. Downloading portable JDK 17...
if not exist lib mkdir lib
powershell -Command "Invoke-WebRequest -Uri 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%%2B7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.10_7.zip' -OutFile 'lib\jdk.zip'"
powershell -Command "Expand-Archive -Path 'lib\jdk.zip' -DestinationPath 'lib\'"
for /d %%G in (lib\jdk-17*) do ren "%%G" jdk
del lib\jdk.zip
set "PATH=%CD%\lib\jdk\bin;%PATH%"

:JDK_FOUND
echo [+] JDK detected!

:: 6. Auto-fetch JavaFX SDK
set JAVAFX_LIB=lib\javafx-windows\lib
if exist "%JAVAFX_LIB%" goto BUILD

echo [!] Downloading JavaFX SDK for Windows...
powershell -Command "Invoke-WebRequest -Uri 'https://download2.gluonhq.com/openjfx/17.0.10/openjfx-17.0.10_windows-x64_bin-sdk.zip' -OutFile 'lib\openjfx.zip'"
powershell -Command "Expand-Archive -Path 'lib\openjfx.zip' -DestinationPath 'lib\'"
ren lib\javafx-sdk-17.0.10 javafx-windows
del lib\openjfx.zip

:BUILD
:: Clean and build
if exist bin rmdir /s /q bin
mkdir bin

if exist src\assets (
    mkdir bin\assets
    xcopy /s /e /y src\assets bin\assets\ >nul
)

echo [+] Compiling Java source files...
javac -d bin --module-path "%JAVAFX_LIB%" --add-modules javafx.controls src\userinterface\*.java src\algorithms\*.java src\*.java

if %ERRORLEVEL% EQU 0 (
    echo [+] Build Successful! Launching Application...
    java -cp bin --module-path "%JAVAFX_LIB%" --add-modules javafx.controls userinterface.Launcher
) else (
    echo [-] Compilation failed.
    pause
)