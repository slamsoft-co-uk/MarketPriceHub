@echo off
setlocal
cd /d "%~dp0"

if defined JAVA_HOME (
    "%JAVA_HOME%\bin\java.exe" -jar "market-price-hub-1.0.jar" --spring.profiles.active=local
) else (
    java -jar "market-price-hub-1.0.jar" --spring.profiles.active=local
)