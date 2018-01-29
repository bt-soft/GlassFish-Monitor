@echo off
REM set GF_ASDAMIN_HOME=c:\JEE\glassfish5\bin
set GF_ASDAMIN_HOME=d:\JEE\glassfish-4.1.1\bin
setlocal EnableDelayedExpansion

rem --- fileReaml
echo create gfmon-fileRealm
call %GF_ASDAMIN_HOME%\asadmin create-auth-realm --classname com.sun.enterprise.security.auth.realm.file.FileRealm --property file=${com.sun.aas.instanceRoot}/config/gfmon-keyfile:jaas-context=fileRealm gfmon-fileRealm

rem --- admin user
echo create admin user:
call %GF_ASDAMIN_HOME%\asadmin.bat create-file-user --groups GFMON_USER:GFMON_ADMIN --authrealmname gfmon-fileRealm --passwordfile pass.txt admin

echo create user user:
call %GF_ASDAMIN_HOME%\asadmin.bat create-file-user --groups GFMON_USER --authrealmname gfmon-fileRealm --passwordfile pass.txt user

