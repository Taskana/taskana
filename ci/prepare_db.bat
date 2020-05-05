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
    IF [%MENU%]==[3] GOTO START_DB2_11_1
    IF [%MENU%]==[4] GOTO STOP_DB2_11_1
    IF [%MENU%]==[5] GOTO START_POSTGRES_10
    IF [%MENU%]==[6] GOTO STOP_POSTGRES_10
    IF [%MENU%]==[7] GOTO STOP_ALL
    IF [%MENU%]==[8] GOTO REMOVE_PROP
    EXIT /B

:START_DB2_11_1
    ECHO ---
    docker ps -aq -f name=^/taskana-db2_11_1$ -f status=running > %TEMP%\temp
    SET /P CONTAINER_RUNNING=< %TEMP%\temp
    docker ps -aq -f name=^/taskana-db2_11_1$ > %TEMP%\temp
    SET /P CONTAINER_EXISTS=< %TEMP%\temp
    del %TEMP%\temp

    IF DEFINED CONTAINER_EXISTS (
        ECHO docker start taskana-db2_11_1
        docker start taskana-db2_11_1
    )

    IF NOT DEFINED CONTAINER_EXISTS (
        ECHO docker run -d -p 50101:50000 --name taskana-db2_11_1 taskana/db2:11.1 -d
        docker run -d -p 50101:50000 --name taskana-db2_11_1 taskana/db2:11.1 -d
    )

    ECHO jdbcDriver=com.ibm.db2.jcc.DB2Driver> %PROP_FILE%
    ECHO jdbcUrl=jdbc:db2://localhost:50101/tskdb>> %PROP_FILE%
    ECHO dbUserName=db2inst1>> %PROP_FILE%
    ECHO dbPassword=db2inst1-pwd>> %PROP_FILE%
    ECHO schemaName=taskana>> %PROP_FILE%
    ECHO ---
    GOTO MENU

:STOP_DB2_11_1
    ECHO ---
    ECHO docker stop taskana-db2_11_1 
    docker stop taskana-db2_11_1
    ECHO ---
    GOTO MENU

:START_POSTGRES_10
    ECHO docker-compose -f %~dp0/docker-compose.yml up -d
    docker-compose -f %~dp0/docker-compose.yml up -d

    ECHO jdbcDriver=org.postgresql.Driver> %PROP_FILE%
    ECHO jdbcUrl=jdbc:postgresql://localhost:50102/postgres>> %PROP_FILE%
    ECHO dbUserName=postgres>> %PROP_FILE%
    ECHO dbPassword=postgres>> %PROP_FILE%
    ECHO schemaName=taskana>> %PROP_FILE%
    ECHO ---
    GOTO MENU

:STOP_POSTGRES_10
    ECHO ---
    ECHO docker stop taskana-postgres_10 
    docker stop taskana-postgres_10
    ECHO ---
    GOTO MENU

:STOP_ALL
    ECHO ---
    ECHO docker stop taskana-db2_11_1
    docker stop taskana-db2_11_1
    ECHO docker stop taskana-postgres_10
    docker stop takana-postgres_10
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
