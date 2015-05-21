@ECHO OFF

REM If Java compiler is not in your search path, set it here:
REM SET JAVAC="C:\Program Files\Java\jdk1.6.0_10"


REM Make sure NDDSHOME is set correctly
IF NOT DEFINED NDDSHOME (
    ECHO NDDSHOME environment variable is not set
    GOTO ENDSCRIPT
)

REM Make sure javac.exe is in the search path
FOR %%F IN (javac.exe) DO IF NOT EXIST %%~$PATH:F (
    ECHO Error: javac.exe not found in current search path.
    ECHO Make sure that Java SDK is correctly installed and you have the
    ECHO compiler in the search path.
    GOTO ENDSCRIPT
)

REM Ensure this script is invoked from the root directory of the project
IF NOT EXIST SRC (
    ECHO You must run this script from the example root directory
    GOTO ENDSCRIPT
)

REM Re-generate the type code from the IDL
IF NOT EXIST SRC\ES\UGR\DISHA\IDL\FILESEGMENTd.java (
    ECHO Generating type-support code from IDL file
    CD SRC
    CALL "%NDDSHOME%\scripts\rtiddsgen.bat" -package es.ugr.ddsbox.idl -language Java -ppDisable -replace res/disha.idl
    CD ..
)    

GOTO ENDSCRIPT

:ENDSCRIPT:
