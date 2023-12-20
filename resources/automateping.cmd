::ping -n 10 -w 7000 bajanaval.com
@echo off
Title Multi-Ping hosts Tester with colors by Rakesh Chaudhari
call :init
set MyFile=%~1
If Not exist %MyFile% goto error
mode con cols=65 lines=30
set "Cname= PingResults - "
set "LogFile=PingResults.txt"
If exist %LogFile% Del %LogFile%
echo(
call :color 0E "      ------- Ping status of targets hosts -------" 1
echo(
(
    echo ******************************************************
    echo   PingTest executed on %Date% @ Time %Time%
    echo ******************************************************
    echo(
) > %LogFile%
Setlocal EnableDelayedExpansion
for /f "usebackq delims=" %%a in ("%MyFile%") do (
    ping -n 1 %%a | find "Packets: Sent" >nul 
    if errorlevel 1 (
        call :color 0C " Host %%a not reachable KO" 1 & echo Host %%a not reachable KO >>%LogFile%
    ) else (
        call :color 0A " Host %%a reachable OK" 1 & echo Host %%a reachable OK >>%LogFile%
    )
)
EndLocal
Start "" %LogFile%
pause>nul & exit
::************************************************************************************* 
:init
prompt $g
for /F "delims=." %%a in ('"prompt $H. & for %%b in (1) do rem"') do set "BS=%%a"
exit /b
::*************************************************************************************
:color
set nL=%3
if not defined nL echo requires third argument & pause > nul & goto :eof
if %3 == 0 (
    <nul set /p ".=%bs%">%2 & findstr /v /a:%1 /r "^$" %2 nul & del %2 2>&1 & goto :eof
) else if %3 == 1 (
    echo %bs%>%2 & findstr /v /a:%1 /r "^$" %2 nul & del %2 2>&1 & goto :eof
)
exit /b
::*************************************************************************************
:error
mode con cols=70 lines=3
color 0C
cls
echo(
 echo       ATTENTION !!!  Check if the file "%MyFile%" exist !
pause>nul & exit
::*************************************************************************************