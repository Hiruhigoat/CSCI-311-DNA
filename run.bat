@echo off
setlocal enabledelayedexpansion
title DNA Sequence Alignment Studio - Windows Build
echo ==================================================
echo   DNA Sequence Alignment Studio - Windows Build
echo ==================================================

:: 1. Check if javac is already available in system PATH
where javac >nul 2>nul
if %ERRORLEVEL% EQU 0 goto JDK_FOUND

:: 2. Check standard JAVA_HOME environment variable
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set "PATH=%JAVA_HOME%\bin;%PATH%"
        goto JDK_FOUND
    )
)

:: 3. Dynamically search common JDK installation folders on Windows
for %%D in (
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

:: 4. If no JDK is found on the machine
echo [-] ERROR: Java Development Kit (JDK) not found.
echo [*] Anyone running this Java project must have a JDK installed (e.g. JDK 17+).
pause
exit /b 1

:JDK_FOUND
echo [+] JDK compiler detected!

:: 5. Auto-fetch local JavaFX SDK for Windows if missing
set JAVAFX_LIB=lib\javafx-windows\lib

if exist "%JAVAFX_LIB%" goto BUILD

echo [!] Downloading JavaFX SDK for Windows...
if not exist lib mkdir lib
powershell -Command "Invoke-WebRequest -Uri 'https://download2.gluonhq.com/openjfx/17.0.10/openjfx-17.0.10_windows-x64_bin-sdk.zip' -OutFile 'lib\openjfx.zip'"
powershell -Command "Expand-Archive -Path 'lib\openjfx.zip' -DestinationPath 'lib\'"
ren lib\javafx-sdk-17.0.10 javafx-windows
del lib\openjfx.zip

:BUILD
:: Clean bin directory
if exist bin rmdir /s /q bin
mkdir bin

:: Copy UI assets if present
if exist src\assets (
    mkdir bin\assets
    xcopy /s /e /y src\assets bin\assets\ >nul
)

:: Compile source files
echo [+] Compiling Java source files...
javac -d bin --module-path "%JAVAFX_LIB%" --add-modules javafx.controls src\userinterface\*.java src\algorithms\*.java src\*.java

if %ERRORLEVEL% EQU 0 (
    echo [+] Build Successful! Launching Application...
    java -cp bin --module-path "%JAVAFX_LIB%" --add-modules javafx.controls userinterface.Launcher
) else (
    echo [-] Compilation failed. Check syntax errors above.
    pause
)