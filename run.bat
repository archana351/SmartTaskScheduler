@echo off
echo ========================================================
echo   Smart Task Scheduler - Launching JavaFX Desktop App
echo ========================================================
if not exist out (
    mkdir out
)
echo Compiling Java source files...
javac --module-path lib --add-modules javafx.controls -d out src/*.java
copy /Y "src\style.css" "out\style.css" >nul
echo Starting application...
java --module-path lib --add-modules javafx.controls -cp out Main
