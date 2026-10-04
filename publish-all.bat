@echo off
setlocal enabledelayedexpansion

rem ======================================================================
rem  One-click Modrinth publishing for every Minecraft version.
rem  Runs the "publishModrinth" task inside each versions\<MCVER> Gradle
rem  root, which uploads that version's Fabric / Forge / NeoForge jars.
rem
rem  Usage:
rem    publish-all.bat                   publish every version
rem    publish-all.bat 1.20.1 1.21.1     publish only the listed versions
rem    publish-all.bat -k                keep going when a version fails
rem    publish-all.bat -n                dry run (print commands only)
rem    publish-all.bat -c                run "clean" before publishing
rem    publish-all.bat -l                list versions + required JDK
rem    publish-all.bat -h                show this help
rem
rem  JDK:
rem    The right JDK is picked automatically from each version's
rem    build.gradle (options.release): 1.16.5..1.20.1 -> Java 17,
rem    1.21.1 -> Java 21. Common install locations are searched.
rem    Override with an environment variable, e.g.
rem      set JAVA_HOME_21=D:\Program Files\Java\jdk-21
rem
rem  Requires a valid token in the repo-root modrinth.properties.
rem  Set PUBLISH_ALL_NO_PAUSE=1 to skip the "press any key" at the end.
rem ======================================================================

set "SCRIPT_DIR=%~dp0"
set "VERSIONS_DIR=%SCRIPT_DIR%versions"
set "TASK=publishModrinth"

set "KEEP_GOING=0"
set "DRY_RUN=0"
set "LIST_ONLY=0"
set "DO_CLEAN=0"
set "WANTED="

rem Detect a double-click launch so we can pause at the end.
set "DOUBLE=0"
echo "%cmdcmdline%" | find /i "%~nx0" >nul 2>&1 && set "DOUBLE=1"
if "%PUBLISH_ALL_NO_PAUSE%"=="1" set "DOUBLE=0"

:parse
if "%~1"=="" goto parsed
if /i "%~1"=="-k" (set "KEEP_GOING=1" & shift & goto parse)
if /i "%~1"=="--keep-going" (set "KEEP_GOING=1" & shift & goto parse)
if /i "%~1"=="-n" (set "DRY_RUN=1" & shift & goto parse)
if /i "%~1"=="--dry-run" (set "DRY_RUN=1" & shift & goto parse)
if /i "%~1"=="-l" (set "LIST_ONLY=1" & shift & goto parse)
if /i "%~1"=="--list" (set "LIST_ONLY=1" & shift & goto parse)
if /i "%~1"=="-c" (set "DO_CLEAN=1" & shift & goto parse)
if /i "%~1"=="--clean" (set "DO_CLEAN=1" & shift & goto parse)
if /i "%~1"=="-h" goto usage
if /i "%~1"=="--help" goto usage
set "WANTED=!WANTED! %~1"
shift
goto parse

:parsed
if not exist "%VERSIONS_DIR%\" (
    echo [error] versions directory not found: "%VERSIONS_DIR%"
    goto fail
)

rem ---- build the list of publishable versions -------------------------
set "LIST="
if defined WANTED (
    for %%V in (%WANTED%) do (
        if exist "%VERSIONS_DIR%\%%V\gradlew.bat" (
            set "LIST=!LIST! %%V"
        ) else (
            echo [warn] unknown or non-publishable version: %%V
        )
    )
) else (
    for /f "delims=" %%V in ('dir /b /ad /o:n "%VERSIONS_DIR%" 2^>nul') do (
        if exist "%VERSIONS_DIR%\%%V\gradlew.bat" set "LIST=!LIST! %%V"
    )
)

if not defined LIST (
    echo [error] no publishable versions found under "%VERSIONS_DIR%".
    goto fail
)

if "%LIST_ONLY%"=="1" (
    for %%V in (%LIST%) do (
        call :requiredMajor "%%V"
        call :resolveJdk "!MAJOR!"
        if defined JDK (
            echo %%V    Java !MAJOR!    !JDK!
        ) else (
            echo %%V    Java !MAJOR!    [not found - will use current JAVA_HOME]
        )
    )
    exit /b 0
)

echo.
echo Publishing task "%TASK%" for:%LIST%
if "%DO_CLEAN%"=="1" echo (clean enabled)
if "%DRY_RUN%"=="1" echo (dry run - nothing will be uploaded)
echo.

set "FAILED="
for %%V in (%LIST%) do (
    call :requiredMajor "%%V"
    call :resolveJdk "!MAJOR!"
    call :publish "%%V" "!JDK!" "!MAJOR!"
    if errorlevel 1 (
        set "FAILED=!FAILED! %%V"
        if "!KEEP_GOING!"=="0" (
            echo.
            echo [abort] stopping after failure of %%V. Use -k to continue.
            goto report
        )
    )
)

:report
echo.
if defined FAILED (
    echo ==================== RESULT ====================
    echo FAILED:%FAILED%
    if "%DOUBLE%"=="1" pause
    exit /b 1
)
echo ==================== RESULT ====================
echo All versions published successfully.
if "%DOUBLE%"=="1" pause
exit /b 0

rem ---------------------------------------------------------------------
rem  Run publishModrinth inside one version directory.
rem  %1 = version, %2 = JAVA_HOME to use, %3 = required Java major
rem ---------------------------------------------------------------------
:publish
setlocal
set "VER=%~1"
set "JDK=%~2"
set "MAJ=%~3"
echo ==================== %VER% ====================
if defined JDK (
    echo JDK: !JDK!   ^(needs Java !MAJ!^)
) else (
    echo [warn] no JDK found for Java !MAJ! - using current JAVA_HOME.
)
if "%DRY_RUN%"=="1" (
    echo [dry-run] cd /d "%VERSIONS_DIR%\%VER%"  then  call gradlew.bat %TASK%
    endlocal & exit /b 0
)
if defined JDK set "JAVA_HOME=%JDK%"
pushd "%VERSIONS_DIR%\%VER%" || (endlocal & exit /b 1)
if "%DO_CLEAN%"=="1" call gradlew.bat clean
call gradlew.bat %TASK%
set "RC=%ERRORLEVEL%"
popd
if not "%RC%"=="0" echo [error] %VER% failed with exit code %RC%.
endlocal & exit /b %RC%

rem ---------------------------------------------------------------------
rem  %1 = version -> MAJOR = required Java major (from build.gradle)
rem ---------------------------------------------------------------------
:requiredMajor
setlocal
set "BG=%VERSIONS_DIR%\%~1\build.gradle"
set "MAJ="
if exist "%BG%" (
    for /f "delims=" %%L in ('findstr /c:"options.release" "%BG%" 2^>nul') do (
        set "LINE=%%L"
        for /f "tokens=2 delims== " %%a in ("!LINE!") do set "MAJ=%%a"
    )
)
if not defined MAJ (
    if exist "%BG%" (
        for /f "delims=" %%L in ('findstr /c:"JavaVersion.VERSION_" "%BG%" 2^>nul') do (
            set "LINE=%%L"
            for /f "tokens=2 delims=_ " %%a in ("!LINE!") do set "MAJ=%%a"
        )
    )
)
if not defined MAJ set "MAJ=17"
endlocal & set "MAJOR=%MAJ%"
exit /b 0

rem ---------------------------------------------------------------------
rem  %1 = jdk home, %2 = output variable -> set to major version (17/21)
rem ---------------------------------------------------------------------
:javaMajor
setlocal
set "REL=%~1\release"
set "M="
set "RAW="
if exist "%REL%" (
    for /f "usebackq tokens=1,* delims==" %%a in ("%REL%") do (
        if /i "%%a"=="JAVA_VERSION" set "RAW=%%~b"
    )
)
if defined RAW (
    for /f "tokens=1 delims=." %%x in ("!RAW!") do set "M=%%x"
    if "!M!"=="1" (
        for /f "tokens=2 delims=." %%x in ("!RAW!") do set "M=%%x"
    )
)
endlocal & set "%~2=%M%"
exit /b 0

rem ---------------------------------------------------------------------
rem  %1 = required major -> JDK = matching JAVA_HOME (empty if none)
rem  Order: JAVA_HOME_<major> env  >  JAVA_HOME (if matching)  >  scan
rem ---------------------------------------------------------------------
:resolveJdk
setlocal
set "WANT=%~1"
set "JDK="

call set "CAND=%%JAVA_HOME_%WANT%%%"
if defined CAND (
    if exist "%CAND%\bin\java.exe" (
        set "JDK=%CAND%"
        goto :resolveJdk_out
    )
)

if defined JAVA_HOME (
    call :javaMajor "%JAVA_HOME%" JM
    if "!JM!"=="%WANT%" (
        set "JDK=%JAVA_HOME%"
        goto :resolveJdk_out
    )
)

for %%L in (C D E F G H I J K L M N O P Q R S T U V W X Y Z) do (
    if exist "%%L:\Program Files\Java\" call :scanRoot "%%L:\Program Files\Java" %WANT%
    if defined JDK goto :resolveJdk_out
    if exist "%%L:\Program Files\Eclipse Adoptium\" call :scanRoot "%%L:\Program Files\Eclipse Adoptium" %WANT%
    if defined JDK goto :resolveJdk_out
)
if exist "%USERPROFILE%\.jdks\" call :scanRoot "%USERPROFILE%\.jdks" %WANT%
if defined JDK goto :resolveJdk_out
if exist "%USERPROFILE%\.gradle\jdks\" call :scanRoot "%USERPROFILE%\.gradle\jdks" %WANT%

:resolveJdk_out
endlocal & set "JDK=%JDK%"
exit /b 0

rem ---------------------------------------------------------------------
rem  %1 = root dir, %2 = wanted major -> sets JDK if a match is found
rem ---------------------------------------------------------------------
:scanRoot
setlocal
set "ROOT=%~1"
set "WANT=%~2"
set "HIT="
for /d %%D in ("%ROOT%\*") do (
    if exist "%%D\bin\java.exe" (
        call :javaMajor "%%D" M
        if "!M!"=="%WANT%" (
            set "HIT=%%D"
            goto :scanRoot_out
        )
    )
)
:scanRoot_out
endlocal & if not "%HIT%"=="" set "JDK=%HIT%"
exit /b 0

:usage
echo Usage: publish-all.bat [versions...] [-k] [-n] [-c] [-l] [-h]
echo.
echo   versions...  publish only these versions (default: all)
echo   -k           keep going when a version fails
echo   -n           dry run, print commands only
echo   -c           run "clean" before publishing
echo   -l           list versions + required JDK
echo   -h           show this help
echo.
echo JDK override: set JAVA_HOME_17 / JAVA_HOME_21 ...
exit /b 0

:fail
if "%DOUBLE%"=="1" pause
exit /b 1
