@echo off
setlocal enabledelayedexpansion

:: Prompt for version
set /p VERSION=Enter new version (e.g. 1.2.3): 

echo.
echo === Running InstallerPrep with version %VERSION% ===
java tools.InstallerPrep %VERSION%

if %errorlevel% neq 0 (
    echo [ERROR] InstallerPrep failed. Aborting.
    pause
    exit /b %errorlevel%
)

echo.
echo === Compiling Windows EXE with Launch4j ===
"C:\Program Files (x86)\Launch4j\launch4jc.exe" tools\launch4j.xml
if %errorlevel% neq 0 (
    echo [ERROR] Launch4j failed to create EXE. Aborting.
    pause
    exit /b %errorlevel%
)

echo.
echo === Building Windows Installer with Inno Setup ===
start /wait "" "C:\Program Files (x86)\Inno Setup 6\ISCC.exe" tools\setup.iss
if %errorlevel% neq 0 (
    echo [ERROR] Inno Setup build failed. Aborting.
    pause
    exit /b %errorlevel%
)

:: Ensure releases folder exists
if not exist releases (
    mkdir releases
)

:: Copy final installer to releases with versioned filename
copy /Y "tools\Output\PokemonXhenosInstaller.exe" "releases\PokemonXhenosInstaller_v%VERSION%.exe"

echo.
echo === Creating macOS .app bundle ===

:: Define mac build variables
set APP_DIR=tools\mac_bundle
set JAR_NAME=PokemonXhenos-v%VERSION%.jar
set APP_NAME=PokemonXhenos
set APP_BUNDLE=%APP_NAME%.app
set APP_CONTENTS=%APP_DIR%\%APP_BUNDLE%\Contents

:: Clean old build
rmdir /s /q "%APP_DIR%" >nul 2>&1

:: Create required structure
mkdir %APP_CONTENTS%\MacOS
mkdir %APP_CONTENTS%\Resources
if not exist tools\dist\%JAR_NAME% (
    echo ERROR: JAR file not found: tools\dist\%JAR_NAME%
    pause
    exit /b 1
)

:: Create PlugIns directory and copy JRE
mkdir %APP_CONTENTS%\PlugIns
xcopy /E /I /Y tools\build\jre %APP_CONTENTS%\PlugIns\jre\

copy tools\mac_template\PokemonXhenos.icns %APP_CONTENTS%\Resources\PokemonXhenos.icns
copy tools\dist\%JAR_NAME% %APP_CONTENTS%\Resources\%JAR_NAME%

:: Copy template resources into bundle
copy /Y tools\mac_template\Info.plist "%APP_CONTENTS%\Info.plist"
copy /Y tools\mac_template\PokemonXhenos "%APP_CONTENTS%\MacOS\PokemonXhenos"
copy /Y tools\mac_template\PokemonXhenos.icns "%APP_CONTENTS%\Resources\PokemonXhenos.icns"
copy /Y tools\dist\%JAR_NAME% "%APP_CONTENTS%\Resources\"

:: Zip the .app bundle
powershell -Command "tar -czf releases/PokemonXhenos_Mac_%VERSION%.tar.gz -C tools/mac_bundle %APP_BUNDLE%"

echo.
echo All done! Final release files:
echo - releases\PokemonXhenosInstaller_%VERSION%.exe (Windows)
echo - releases\PokemonXhenos_Mac_%VERSION%.zip (macOS)
pause
endlocal
