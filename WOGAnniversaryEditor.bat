@echo off
set JLINK_VM_OPTIONS=
set DIR=%~dp0
"%DIR%\target\WOGAnniversaryEditor\bin\java" %JLINK_VM_OPTIONS% -m com.example.WOGAnniversaryEditor/com.WooGLEFX.Engine.Main %*
