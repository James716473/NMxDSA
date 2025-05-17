@echo off
setlocal enabledelayedexpansion

:: Create bin directory if it doesn't exist
if not exist bin mkdir bin
if not exist bin\fx mkdir bin\fx
if not exist bin\fx\resources mkdir bin\fx\resources

:: Copy FXML and resource files
xcopy /y /s src\fx\*.fxml bin\fx\
xcopy /y /s src\fx\*.css bin\fx\
xcopy /y /s src\fx\resources\*.* bin\fx\resources\

:: Set paths
set PATH_TO_FX=%CD%\lib
set PATH_TO_FX_BIN=%PATH_TO_FX%\bin
set CLASSPATH=lib\exp4j-0.4.8.jar

:: Add native libraries to PATH
set PATH=%PATH_TO_FX_BIN%;%PATH%

:: Compile
echo Compiling Java files...
javac --release 21 ^
    --module-path "%PATH_TO_FX%" ^
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
    -d bin -cp "%CLASSPATH%" src\fx\*.java src\*.java
if errorlevel 1 (
    echo Compilation failed!
    exit /b 1
)

:: Run
echo Running application...
java ^
    --module-path "%PATH_TO_FX%" ^
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
    -Djava.library.path="%PATH_TO_FX_BIN%" ^
    -cp "bin;%CLASSPATH%" fx.Main
if errorlevel 1 (
    echo Execution failed!
    exit /b 1
)

echo Build and execution completed successfully.
exit /b 0