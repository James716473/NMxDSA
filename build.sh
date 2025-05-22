#!/bin/bash

echo "Building project..."

if [ ! -d "lib" ]; then
    echo "Error: lib directory not found!"
    echo "Please ensure you have the required libraries in the lib directory"
    exit 1
fi

if [ ! -d "bin" ]; then
    mkdir bin
fi

echo "Compiling Java files..."
javac -cp "lib/*" -d bin src/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "Running application..."
cd bin
java -cp ".:../lib/*" CalculatorApp
if [ $? -ne 0 ]; then
    echo "Application execution failed!"
    cd ..
    exit 1
fi
cd .. 