@echo off
set port=9091
for /f "tokens=5" %%m in ('netstat -ano ^| findstr ":%port%"') do (
    echo kill the process %%m who use the port %port%
    taskkill /f /pid %%m
)
