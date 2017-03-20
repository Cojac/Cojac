set FF=d:\Git-MyRepository\cojac
set COJ=%FF%\target\cojac.jar
set M=com.github.cojac.deltadebugging.App
set S=%FF%\src\test\java\misctests\deltaDebugging\Simpsons.java
set C=com.github.cojac.misctests.deltaDebugging.Simpsons
set C=dd.ConFrac
set J=C:\ProgramData\Oracle\Java\javapath\java.exe
set X=E:\_temp\aaaaa.txt
set X=E:\_temp\ConFrac.txt
set CP=D:\Workspace_plugins\UsingCojac\bin

rem java -cp %J% %M% -h
java -cp %COJ% %M% -behavioursfile %X% -cojac %COJ% -mode std -mainclass %C% -classpath %CP%


rem on bash:  /e/_apps/Java/jdk1.8.0_112/bin/java -cp /d/Git-MyRepository/cojac/target/cojac.jar com.github.cojac.deltadebugging.App -behavioursfile /e/_temp/ConFrac.txt -cojac /d/Git-MyRepository/cojac/target/cojac.jar -mode std -mainclass dd.ConFrac -classpath /d/Workspace_plugins/UsingCojac/bin