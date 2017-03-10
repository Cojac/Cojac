set FF=d:\Git-MyRepository\cojac
set COJ=%FF%\target\cojac.jar
set M=com.github.cojac.deltadebugging.App
set S=%FF%\src\test\java\misctests\deltaDebugging\Simpsons.java
set C=com.github.cojac.misctests.deltaDebugging.Simpsons
set J=C:\ProgramData\Oracle\Java\javapath\java.exe
set X=E:\_temp\aaaaa.txt

rem java -cp %J% %M% -h
java -cp %COJ% %M% -behavioursfile %X% -cojac %COJ% -mode std -mainclass %C% -java %J%