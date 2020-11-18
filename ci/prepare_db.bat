@ECHO OFF
SETLOCAL
SET PROP_FILE=%HOMEPATH%\taskanaUnitTest.properties

:MENU
    ECHO.
    ECHO -----------------------------------------------------
    ECHO PRESS a number to select your task - anything to EXIT.
    ECHO -----------------------------------------------------
    ECHO.
    ECHO 1 - Start DB2 11.1
    ECHO 2 - Stop  DB2 11.1
    ECHO.
    ECHO 3 - Start POSTGRES 10
    ECHO 4 - Stop  POSTGRES 10
    ECHO.
    ECHO 5 - Stop all
    ECHO 6 - Remove %PROP_FILE%
    ECHO.
    SET /P MENU=Select task then press ENTER:
    ECHO.
    IF [%MENU%]==[1] GOTO START_DB2_11_1
    IF [%MENU%]==[2] GOTO STOP_DB2_11_1
    IF [%MENU%]==[3] GOTO START_POSTGRES_10
    IF [%MENU%]==[4] GOTO STOP_POSTGRES_10
    IF [%MENU%]==[5] GOTO STOP_ALL
    IF [%MENU%]==[6] GOTO REMOVE_PROP
    EXIT /B

:START_DB2_11_1
    ECHO ---
    ECHO docker-compose -f %~dp0/docker-compose.yml up -d taskana-db2_11-1
    docker-compose -f %~dp0/docker-compose.yml up -d taskana-db2_11-1

    ECHO jdbcDriver=com.ibm.db2.jcc.DB2Driver> %PROP_FILE%
    ECHO jdbcUrl=jdbc:db2://localhost:5101/tskdb>> %PROP_FILE%
    ECHO dbUserName=db2inst1>> %PROP_FILE%
    ECHO dbPassword=db2inst1-pwd>> %PROP_FILE%
    ECHO schemaName=taskana>> %PROP_FILE%
    ECHO ---
    GOTO MENU

:STOP_DB2_11_1
    ECHO ---
    ECHO docker-compose -f %~dp0/docker-compose.yml rm -f -s -v taskana-db2_11-1
    docker-compose -f %~dp0/docker-compose.yml rm -f -s -v taskana-db2_11-1
    ECHO ---
    GOTO REMOVE_PROP

:START_POSTGRES_10
    ECHO ---
    ECHO docker-compose -f %~dp0/docker-compose.yml up -d taskana-postgres_10
    docker-compose -f %~dp0/docker-compose.yml up -d taskana-postgres_10

    ECHO jdbcDriver=org.postgresql.Driver> %PROP_FILE%
    ECHO jdbcUrl=jdbc:postgresql://localhost:5102/postgres>> %PROP_FILE%
    ECHO dbUserName=postgres>> %PROP_FILE%
    ECHO dbPassword=postgres>> %PROP_FILE%
    ECHO schemaName=taskana>> %PROP_FILE%
    ECHO ---
    GOTO MENU

:STOP_POSTGRES_10
    ECHO ---
    ECHO docker stop taskana-postgres_10 
    ECHO docker-compose -f %~dp0/docker-compose.yml rm -f -s -v taskana-postgres_10
    docker-compose -f %~dp0/docker-compose.yml rm -f -s -v taskana-postgres_10
    ECHO ---
    GOTO REMOVE_PROP

:STOP_ALL
    ECHO ---
    ECHO docker-compose -f %~dp0/docker-compose.yml down -v
    docker-compose -f %~dp0/docker-compose.yml down -v
    ECHO ---
    GOTO REMOVE_PROP

:REMOVE_PROP
    ECHO ---
    IF EXIST %PROP_FILE% (
        ECHO DEL /F %PROP_FILE%
        DEL /F %PROP_FILE%
    )
    ECHO ---
    GOTO MENU
