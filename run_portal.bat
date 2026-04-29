@echo off
setlocal

if not exist out (
    mkdir out
)

javac -cp "lib\mysql-connector-j-9.6.0.jar" -d out src\MainApp.java src\model\*.java src\dao\*.java src\ui\*.java src\util\*.java
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)

java -cp "out;lib\mysql-connector-j-9.6.0.jar" MainApp
