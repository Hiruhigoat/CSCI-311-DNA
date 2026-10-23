@echo off
title DNA Sequence Alignment Studio - Windows Build
echo ==================================================
echo   DNA Sequence Alignment Studio - Windows Build
echo ==================================================

:: Clean and create bin
if exist bin rmdir /s /q bin
mkdir bin

:: Copy assets
if exist src\assets (
    mkdir bin\assets
    xcopy /s /e /y src\assets bin\assets\ >nul
)

:: Compile
echo Compiling source files...
javac -d bin --add-modules javafx.controls src\userinterface\*.java src\algorithms\*.java src\*.java

if %ERRORLEVEL% EQU 0 (
    echo Build Successful! Launching Application...
    java -cp bin --add-modules javafx.controls userinterface.Launcher
) else (
    echo Compilation failed.
    pause
)