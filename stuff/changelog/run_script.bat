@echo off
if "%1"=="" (
    python ./convert_changelog.py
) else (
    python ./convert_changelog.py %1
)
pause