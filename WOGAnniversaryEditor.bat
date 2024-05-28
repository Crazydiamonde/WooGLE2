@echo off
set JLINK_VM_OPTIONS=
set DIR=%~dp0
"%DIR%\bin\java" %JLINK_VM_OPTIONS% -m src.main.java.com.woogleFX.engine.Main %*
