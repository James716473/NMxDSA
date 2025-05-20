@echo off
setlocal enabledelayedexpansion

:: Create bin directory if it doesn't exist
if not exist bin mkdir bin

:: Set classpath to include exp4j library
set CLASSPATH=.;%CD%\lib\exp4j-0.4.8.jar

:: Compile
echo Compiling Java files...
javac -d bin -cp "%CLASSPATH%" src\*.java
if errorlevel 1 (
    echo Compilation failed!
    exit /b 1
)

:: Run
echo Running application...
java -cp "bin;%CLASSPATH%" App
if errorlevel 1 (
    echo Execution failed!
    exit /b 1
)

echo Build and execution completed successfully.
exit /b 0