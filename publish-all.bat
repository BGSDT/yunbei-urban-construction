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
rem    publish-all.bat -x 1.20.1 1.21.1 exclude versions (repeatable;
rem                                      a comma list also works)
rem    publish-all.bat -s                skip versions already published
rem                                      successfully (see .publish-state)
rem    publish-all.bat -r                clear the publish record and exit
rem    publish-all.bat -k                keep going when a version fails
rem    publish-all.bat -n                dry run (print commands only)
rem    publish-all.bat -c                run "clean" before publishing
rem    publish-all.bat -l                list versions + required JDK
rem    publish-all.bat -h                show this help
rem
rem  Publish record (.publish-state):
rem    After a version publishes successfully a line "<MCVER> <mod_version>"
rem    is appended to the repo-root .publish-state. With -s a version whose
rem    mod_version still matches the record is skipped; if mod_version
rem    changed (e.g. 26w40b -> 26w40c) it counts as not published and will
rem    publish again.
rem    Typical use: a build fails midway -> fix it, then run
rem      publish-all.bat -s -k
rem    and only the failed / unpublished versions are retried.
rem
rem  Always-on excludes (no -x needed):
rem    repo-root publish-skip.txt, one MC version per line (# starts a
rem    comment; commas also work). Every run excludes those versions.
rem    Example: a single line "1.16.5" -> 1.16.5 is skipped from now on.
rem    To publish it again, delete that line (or comment it out).
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
set "STATE_FILE=%SCRIPT_DIR%.publish-state"
set "SKIP_FILE=%SCRIPT_DIR%publish-skip.txt"

set "KEEP_GOING=0"
set "DRY_RUN=0"
set "LIST_ONLY=0"
set "DO_CLEAN=0"
set "SKIP_PUBLISHED=0"
set "RESET_STATE=0"
set "WANTED="
set "EXCLUDES="

rem Detect a double-click launch so we can pause at the end.
set "DOUBLE=0"
echo "%cmdcmdline%" | find /i "%~nx0" >nul 2>&1 && set "DOUBLE=1"
if "%PUBLISH_ALL_NO_PAUSE%"=="1" set "DOUBLE=0"

:parse
if "%~1"=="" goto parsed
if /i "%~1"=="-x" goto parseExclude
if /i "%~1"=="--exclude" goto parseExclude
if /i "%~1"=="-s" (set "SKIP_PUBLISHED=1" & shift & goto parse)
if /i "%~1"=="--skip-published" (set "SKIP_PUBLISHED=1" & shift & goto parse)
if /i "%~1"=="-r" (set "RESET_STATE=1" & shift & goto parse)
if /i "%~1"=="--reset-state" (set "RESET_STATE=1" & shift & goto parse)
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

:parseExclude
rem cmd.exe treats commas as arg separators, so -x consumes every
rem following token until the next "-flag" (versions never start with -).
set "GOTEX=0"
:parseExcludeNext
if "%~2"=="" goto parseExcludeEnd
set "TOK=%~2"
if "!TOK:~0,1!"=="-" goto parseExcludeEnd
set "EXCLUDES=!EXCLUDES! %~2"
set "GOTEX=1"
shift
goto parseExcludeNext
:parseExcludeEnd
if "!GOTEX!"=="0" (
    echo [error] -x needs a version, e.g. -x 1.20.1
    goto fail
)
shift
goto parse

:parsed
if not exist "%VERSIONS_DIR%\" (
    echo [error] versions directory not found: "%VERSIONS_DIR%"
    goto fail
)

rem ---- publish-skip.txt: always-on exclude list ------------------------
rem  one MC version per line, '#' starts a comment, commas also work.
rem  NOTE: read via findstr + for /f on the command output. A plain
rem  "for /f ... in ("file")" silently returns nothing for LF-only
rem  files that contain multi-byte (e.g. Chinese) comment lines.
if exist "%SKIP_FILE%" (
    for /f "usebackq delims=" %%L in (`findstr /v /b /c:"#" "%SKIP_FILE%"`) do (
        set "SKIPLINE=%%L"
        for /f "tokens=1 delims=#" %%C in ("!SKIPLINE!") do set "SKIPLINE=%%C"
        set "SKIPLINE=!SKIPLINE: =!"
        set "SKIPLINE=!SKIPLINE:,= !"
        if not "!SKIPLINE!"=="" set "EXCLUDES=!EXCLUDES! !SKIPLINE!"
    )
)

if "%RESET_STATE%"=="1" (
    if exist "%STATE_FILE%" (
        del /f /q "%STATE_FILE%"
        echo [info] cleared publish record: "%STATE_FILE%"
    ) else (
        echo [info] no publish record to clear: "%STATE_FILE%"
    )
    exit /b 0
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
        call :stateHas "%%V"
        set "MARK=not published"
        if "!STATE_HIT!"=="1" set "MARK=published"
        if defined JDK (
            echo %%V    Java !MAJOR!    !MARK!    !JDK!
        ) else (
            echo %%V    Java !MAJOR!    !MARK!    [not found - will use current JAVA_HOME]
        )
    )
    echo.
    echo publish record: "%STATE_FILE%"
    exit /b 0
)

rem ---- exclude versions (-x plus publish-skip.txt) ---------------------
if defined EXCLUDES (
    set "NEWLIST="
    for %%V in (%LIST%) do (
        set "HIT=0"
        for %%E in (!EXCLUDES!) do if /i "%%V"=="%%E" set "HIT=1"
        if "!HIT!"=="1" (
            echo [skip] excluded: %%V
        ) else (
            set "NEWLIST=!NEWLIST! %%V"
        )
    )
    set "LIST=!NEWLIST!"
)

rem ---- skip already-published versions (-s) ---------------------------
set "SKIPPED_PUB="
if "%SKIP_PUBLISHED%"=="1" (
    set "NEWLIST="
    for %%V in (!LIST!) do (
        call :stateHas "%%V"
        if "!STATE_HIT!"=="1" (
            call :stateKey "%%V"
            echo [skip] already published: %%V  ^(record: !STATE_KEY!^)
            set "SKIPPED_PUB=!SKIPPED_PUB! %%V"
        ) else (
            set "NEWLIST=!NEWLIST! %%V"
        )
    )
    set "LIST=!NEWLIST!"
)

if not defined LIST (
    if defined SKIPPED_PUB (
        echo No versions to publish: the rest are already in .publish-state ^(use -r to clear, or drop -s^).
        exit /b 0
    )
    echo [error] nothing left to publish after applying exclusions.
    goto fail
)

echo.
echo Publishing task "%TASK%" for:%LIST%
if defined SKIPPED_PUB echo (skipped already published:%SKIPPED_PUB%)
if defined EXCLUDES echo (excluded:!EXCLUDES!)
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
    ) else (
        if "!DRY_RUN!"=="0" call :stateAdd "%%V"
    )
)

:report
echo.
if defined FAILED (
    echo ==================== RESULT ====================
    echo FAILED:%FAILED%
    echo Fix and resume with: publish-all.bat -s -k  ^(already published versions are skipped automatically^)
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

rem ---------------------------------------------------------------------
rem  Publish record helpers
rem ---------------------------------------------------------------------

rem  %1 = version -> MV = mod_version from gradle.properties (empty if none)
:modVersion
setlocal
set "MV="
set "GP=%VERSIONS_DIR%\%~1\gradle.properties"
if exist "%GP%" (
    for /f "usebackq tokens=1,* delims==" %%A in ("%GP%") do (
        set "K=%%A"
        set "K=!K: =!"
        if /i "!K!"=="mod_version" (
            set "MV=%%B"
            set "MV=!MV: =!"
        )
    )
)
endlocal & set "MV=%MV%"
exit /b 0

rem  %1 = version -> STATE_KEY = "<MCVER> <mod_version>" (or just "<MCVER>")
:stateKey
setlocal
call :modVersion "%~1"
set "KEY=%~1"
if defined MV set "KEY=%~1 !MV!"
endlocal & set "STATE_KEY=%KEY%"
exit /b 0

rem  %1 = version -> STATE_HIT = 1 if already published with this mod_version
rem  The record holds only ASCII "<MCVER> <mod_version>" lines, so a plain
rem  "for /f ... in (file)" is safe here (LF or CRLF both work).
:stateHas
setlocal
call :stateKey "%~1"
set "HIT="
if exist "%STATE_FILE%" (
    for /f "usebackq delims=" %%L in ("%STATE_FILE%") do (
        if "%%L"=="!STATE_KEY!" set "HIT=1"
    )
)
endlocal & set "STATE_HIT=%HIT%"
exit /b 0

rem  %1 = version -> append "<MCVER> <mod_version>" to the record (once)
:stateAdd
call :stateHas "%~1"
if "%STATE_HIT%"=="1" exit /b 0
call :stateKey "%~1"
>>"%STATE_FILE%" echo %STATE_KEY%
echo       recorded in publish record: %STATE_KEY%
exit /b 0

:usage
echo Usage: publish-all.bat [versions...] [-x ver] [-s] [-r] [-k] [-n] [-c] [-l] [-h]
echo.
echo   versions...  publish only these versions (default: all)
echo   -x ver...    exclude versions (repeatable; also -x 1.20.1 1.21.1)
echo   -s           skip versions already published (record: .publish-state)
echo   -r           clear the publish record and exit
echo   -k           keep going when a version fails
echo   -n           dry run, print commands only
echo   -c           run "clean" before publishing
echo   -l           list versions + required JDK + publish state
echo   -h           show this help
echo.
echo Always-on excludes: repo-root publish-skip.txt (one version per line).
echo Resume after a failure: publish-all.bat -s -k
echo.
echo JDK override: set JAVA_HOME_17 / JAVA_HOME_21 ...
exit /b 0

:fail
if "%DOUBLE%"=="1" pause
exit /b 1
