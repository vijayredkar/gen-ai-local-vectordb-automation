@echo off

cls
echo.
echo.
echo --------- Automation: setup and launch multiple local Vector DBs --------
echo.
echo  Pre-requisites: please ensure below bare minimum machine setup
echo   1- Maven
echo   2- JDK 8/higher
echo   3- Docker 
echo   4- Git
echo   5- minimum 16GB RAM, 4 CPUs, 10GB disk space
pause


REM --------------------- chromadb -----------------------------
echo.
echo Do you want to setup Chroma Vector DB? [Y/N]:
set /p "USER_PREF="

IF /I "%USER_PREF%" NEQ "Y" (
    echo skipping chromaDB setup
    goto :endchromadb
)

IF /I %USER_PREF%==Y (
   echo launching ChromaDB in a new command console.
   start cmd.exe /c docker run -p 8000:8000 chromadb/chroma
   timeout /t 10 /nobreak
)

set process_name=chromadb
set max_iterations=5
set iteration=0

:startchromadb
rem Check if the process is running
docker ps | findstr "%process_name%" | findstr "Up " >nul
if %errorlevel%==0 (
    echo Process is running
    goto :endchromadb
) else (
    set /a iteration+=1
    if %iteration% geq %max_iterations% (
        echo Process not found after %max_iterations% checks. Exiting...
        goto :end
    )
    echo Process not found, checking again in 10 seconds...
    timeout /t 10 >nul
    goto :startchromadb
)
:endchromadb





REM --------------------- elasticsearch -----------------------------
echo.
echo Do you want to setup elasticsearch vector DB? [Y/N]:
set /p "USER_PREF="

IF /I "%USER_PREF%" NEQ "Y" (
    echo skipping endelasticsearch setup
    goto :endelasticsearch
)

IF /I %USER_PREF%==Y (
   echo launching elasticsearch in a new command console.
   start cmd.exe /c docker run -d -p 9200:9200 -p 9300:9300 -e discovery.type=single-node -e xpack.security.enabled=false docker.elastic.co/elasticsearch/elasticsearch:8.9.0
   timeout /t 10 /nobreak
)

set process_name=elasticsearch
set max_iterations=5
set iteration=0

:startelasticsearch
rem Check if the process is running
docker ps | findstr "%process_name%" | findstr "Up " >nul
if %errorlevel%==0 (
    echo Process is running
    goto :endelasticsearch
) else (
    set /a iteration+=1
    if %iteration% geq %max_iterations% (
        echo Process not found after %max_iterations% checks. Exiting...
        goto :end
    )
    echo Process not found, checking again in 10 seconds...
    timeout /t 10 >nul
    goto :startelasticsearch
)
:endelasticsearch



REM --------------------- qdrant -----------------------------
echo.
echo Do you want to setup qdrant vector DB? [Y/N]:
set /p "USER_PREF="

IF /I "%USER_PREF%" NEQ "Y" (
    echo skipping qdrant setup
    goto :endqdrant
)

echo Please provide an existing empty directory path to store qdrant related data as shown in this example format   /c:/qdrant/data
set /p "QDRANT_STORE_LOC="

IF /I %USER_PREF%==Y (

   echo launching qdrant in a new command console.
   start cmd.exe /c    docker run -p 6333:6333 -p 6334:6334 -v %QDRANT_STORE_LOC%:z qdrant/qdrant
   timeout /t 10 /nobreak
)

set process_name=qdrant
set max_iterations=5
set iteration=0

:startqdrant
rem Check if the process is running
docker ps | findstr "%process_name%" | findstr "Up " >nul
if %errorlevel%==0 (
    echo Process is running
    goto :endqdrant
) else (
    set /a iteration+=1
    if %iteration% geq %max_iterations% (
        echo Process not found after %max_iterations% checks. Exiting...
        goto :end
    )
    echo Process not found, checking again in 10 seconds...
    timeout /t 10 >nul
    goto :startqdrant
)
:endqdrant


REM --------------------- redis -----------------------------
echo.
echo Do you want to setup redis vector DB? [Y/N]:
set /p "USER_PREF="

IF /I "%USER_PREF%" NEQ "Y" (
    echo skipping endredis setup
    goto :endredis
)

IF /I %USER_PREF%==Y (
   echo launching redis in a new command console.
   start cmd.exe /c docker run -p 6379:6379  redis/redis-stack-server:latest
   timeout /t 10 /nobreak
)

set process_name=redis
set max_iterations=5
set iteration=0

:startredis
rem Check if the process is running
docker ps | findstr "%process_name%" | findstr "Up " >nul
if %errorlevel%==0 (
    echo Process is running
    goto :endredis
) else (
    set /a iteration+=1
    if %iteration% geq %max_iterations% (
        echo Process not found after %max_iterations% checks. Exiting...
        goto :end
    )
    echo Process not found, checking again in 10 seconds...
    timeout /t 10 >nul
    goto :startredis
)
:endredis


REM --------------------- weaviate -----------------------------
echo.
echo Do you want to setup weaviate vector DB? [Y/N]:
set /p "USER_PREF="

IF /I "%USER_PREF%" NEQ "Y" (
    echo skipping endweaviate setup
    goto :endweaviate
)

IF /I %USER_PREF%==Y (
   echo launching weaviate in a new command console.
   start cmd.exe /c docker run -p 8080:8080 -p 50051:50051 cr.weaviate.io/semitechnologies/weaviate:1.27.6
   timeout /t 10 /nobreak
)

set process_name=weaviate
set max_iterations=5
set iteration=0

:startweaviate
rem Check if the process is running
docker ps | findstr "%process_name%" | findstr "Up " >nul
if %errorlevel%==0 (
    echo Process is running
    goto :endweaviate
) else (
    set /a iteration+=1
    if %iteration% geq %max_iterations% (
        echo Process not found after %max_iterations% checks. Exiting...
        goto :end
    )
    echo Process not found, checking again in 10 seconds...
    timeout /t 10 >nul
    goto :startweaviate
)
:endweaviate





cls

echo.
set current_dir=%cd%
set db_info_path=db-info\available-vectordbs.txt
set db_info_full_path=%current_dir%\%db_info_path% 
echo %db_info_full_path%

del %db_info_full_path%
echo deleted %db_info_full_path%

echo.
echo Selected DBs launched sucessfully.
docker ps --format "table {{.Image}}\t{{.Status}}\t{{.Names}}"
docker ps --format "table {{.Image}}" > %db_info_full_path% 2>&1
echo.
echo created %db_info_full_path%


echo.
echo Launching the application to operate on the Vector DBs

call mvn clean install
start cmd.exe /c java -jar target/gen-ai-local-vectordb.jar Standalone-LOCAL-MC internal


timeout /t 35 /nobreak
echo launch Swagger UI
start "" "http://localhost:8888/swagger-ui/index.html"



echo Process complete. Exiting ..
