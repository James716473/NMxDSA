@echo off
javac -d bin -cp "lib/exp4j-0.4.8.jar" src\App.java src\Tuple.java
java -cp "bin;lib/exp4j-0.4.8.jar" src.App