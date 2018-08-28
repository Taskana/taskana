@ECHO OFF
SETLOCAL
SET PROP_FILE=%HOMEPATH%\taskanaUnitTest.properties

:MENU
    ECHO.
    ECHO -----------------------------------------------------
    ECHO PRESS a number to select your task - anthing to EXIT.
    ECHO -----------------------------------------------------
    ECHO.
    ECHO 1 - Start DB2 10.5
    ECHO 2 - Stop  DB2 10.5
    ECHO.
    ECHO 3 - Start DB2 11.1
    ECHO 4 - Stop  DB2 11.1
    ECHO.
    ECHO 5 - Start POSTGRES 10.4
    ECHO 6 - Stop  POSTGRES 10.4
    ECHO.
    ECHO 7 - Stop all
    ECHO 8 - Remove %PROP_FILE%
    ECHO.
    SET /P MENU=Select task then press ENTER:
    ECHO.
    IF [%MENU%]==[1] GOTO START_DB2_10_5
    IF [%MENU%]==[2] GOTO STOP_DB2_10_5
    IF [%MENU%]==[3] GOTO START_DB2_11_1
    IF [%MENU%]==[4] GOTO STOP_DB2_11_1
    IF [%MENU%]==[5] GOTO START_POSTGRES_10_4
    IF [%MENU%]==[6] GOTO STOP_POSTGRES_10_4
    IF [%MENU%]==[7] GOTO STOP_ALL
    IF [%MENU%]==[8] GOTO REMOVE_PROP
    EXIT /B

:START_DB2_10_5
    ECHO ---
    docker ps -aq -f name=^/taskana-db2_10_5$ -f status=running > %TEMP%\temp
    SET /P CONTAINER_RUNNING=< %TEMP%\temp
    docker ps -aq -f name=^/taskana-db2_10_5$ > %TEMP%\temp
    SET /P CONTAINER_EXISTS=< %TEMP%\temp
    del %TEMP%\temp

    IF NOT DEFINED CONAINER_RUNNING IF DEFINED CONTAINER_EXISTS (
        ECHO docker start taskana-db2_10_5
        docker start taskana-db2_10_5
    )

    IF NOT DEFINED CONAINER_RUNNING IF NOT DEFINED CONTAINER_EXISTS (
        ECHO docker run -d -p 50100:50000 --name taskana-db2_10_5 taskana/db2:10.5 -d
        docker run -d -p 50100:50000 --name taskana-db2_10_5 taskana/db2:10.5 -d
    )

    ECHO jdbcDriver=com.ibm.db2.jcc.DB2Driver > %PROP_FILE%
    ECHO jdbcUrl=jdbc:db2://localhost:50100/tskdb >> %PROP_FILE%
    ECHO dbUserName=db2inst1 >> %PROP_FILE%
    ECHO dbPassword=db2inst1-pwd >> %PROP_FILE%
    ECHO schemaName=taskana >> %PROP_FILE%
    ECHO ---
    GOTO MENU

:STOP_DB2_10_5
    ECHO ---
    ECHO docker stop taskana-db2_1 
    docker stop taskana-db2_1
    ECHO ---
    GOTO MENU

:START_DB2_11_1
    ECHO ---
    docker ps -aq -f name=^/taskana-db2_11_1$ -f status=running > %TEMP%\temp
    SET /P CONTAINER_RUNNING=< %TEMP%\temp
    docker ps -aq -f name=^/taskana-db2_11_1$ > %TEMP%\temp
    SET /P CONTAINER_EXISTS=< %TEMP%\temp
    del %TEMP%\temp

    IF NOT DEFINED CONAINER_RUNNING IF DEFINED CONTAINER_EXISTS (
        ECHO docker start taskana-db2_11_1
        docker start taskana-db2_11_1
    )

    IF NOT DEFINED CONAINER_RUNNING IF NOT DEFINED CONTAINER_EXISTS (
        ECHO docker run -d -p 50101:50000 --name taskana-db2_11_1 taskana/db2:11.1 -d
        docker run -d -p 50101:50000 --name taskana-db2_11_1 taskana/db2:11.1 -d
    )

    ECHO jdbcDriver=com.ibm.db2.jcc.DB2Driver > %PROP_FILE%
    ECHO jdbcUrl=jdbc:db2://localhost:50101/tskdb >> %PROP_FILE%
    ECHO dbUserName=db2inst1 >> %PROP_FILE%
    ECHO dbPassword=db2inst1-pwd >> %PROP_FILE%
    ECHO schemaName=taskana >> %PROP_FILE%
    ECHO ---
    GOTO MENU

:STOP_DB2_11_1
    ECHO ---
    ECHO docker stop taskana-db2_11_1 
    docker stop taskana-db2_11_1
    ECHO ---
    GOTO MENU

:START_POSTGRES_10_4
    ECHO ---
    docker ps -aq -f name=^/taskana-postgres_10_4$ -f status=running > %TEMP%\temp
    SET /P CONTAINER_RUNNING=< %TEMP%\temp
    docker ps -aq -f name=^/taskana-postgres_10_4$ > %TEMP%\temp
    SET /P CONTAINER_EXISTS=< %TEMP%\temp
    del %TEMP%\temp

    IF NOT DEFINED CONAINER_RUNNING IF DEFINED CONTAINER_EXISTS (
        ECHO docker start taskana-postgres_10_4
        docker start taskana-postgres_10_4
    )

    IF NOT DEFINED CONAINER_RUNNING IF NOT DEFINED CONTAINER_EXISTS (
        ECHO docker run -d -p 50102:5432 --name taskana-postgres_10_4 -e POSTGRES_PASSWORD=postgres postgres:10.4
        docker run -d -p 50102:5432 --name taskana-postgres_10_4 -e POSTGRES_PASSWORD=postgres postgres:10.4
    )

    ECHO jdbcDriver=org.postgresql.Driver > %PROP_FILE%
    ECHO jdbcUrl=jdbc:postgresql://localhost:50001/postgres >> %PROP_FILE%
    ECHO dbUserName=postgres >> %PROP_FILE%
    ECHO dbPassword=postgres >> %PROP_FILE%
    ECHO schemaName=taskana >> %PROP_FILE%
    ECHO ---
    GOTO MENU

:STOP_POSTGRES_10_4
    ECHO ---
    ECHO docker stop taskana-postgres_10_4 
    docker stop taskana-postgres_10_4
    ECHO ---
    GOTO MENU

:STOP_ALL
    ECHO ---
    ECHO docker stop taskana-db2_10_5
    docker stop taskana-db2_10_5
    ECHO docker stop taskana-db2_11_1
    docker stop taskana-db2_11_1
    ECHO docker stop taskana-postgres_10_4
    docker stop takana-postgres_10_4
    ECHO ---
    GOTO MENU

:REMOVE_PROP
    ECHO ---
    IF EXIST %PROP_FILE% (
        ECHO DEL /F %PROP_FILE%
        DEL /F %PROP_FILE%
    )
    ECHO ---
    GOTO MENU
