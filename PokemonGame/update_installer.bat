@echo off
set /p VERSION=Enter new version (e.g. 1.2.3): 

echo.
echo Running InstallerPrep with version %VERSION%...
java tools.InstallerPrep %VERSION%

if %errorlevel% neq 0 (
    echo Error occurred during InstallerPrep. Aborting.
    pause
    exit /b %errorlevel%
)

echo.
echo Launching Launch4j Compiler...
"C:\Program Files (x86)\Launch4j\launch4jc.exe" tools\launch4j.xml
if %errorlevel% neq 0 (
    echo Launch4j failed to create EXE.
    pause
    exit /b %errorlevel%
)

echo.
echo Launching Inno Setup Compiler...
start /wait "" "C:\Program Files (x86)\Inno Setup 6\ISCC.exe" tools/setup.iss

echo.
echo Done!

if not exist releases (
    mkdir releases
)

copy /Y tools\Output\PokemonXhenosInstaller.exe releases\PokemonXhenosInstaller_%VERSION%.exe