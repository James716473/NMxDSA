@echo off
echo Building project...

if not exist "lib" (
    echo Error: lib directory not found!
    echo Please ensure you have the required libraries in the lib directory
    pause
    exit /b 1
)

if not exist "bin" mkdir bin

echo Compiling Java files...
javac -cp "lib/*" -d bin src/*.java
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Running application...
cd bin
java -cp ".;../lib/*" CalculatorApp
if errorlevel 1 (
    echo Application execution failed!
    cd ..
    pause
    exit /b 1
)
cd ..

pause