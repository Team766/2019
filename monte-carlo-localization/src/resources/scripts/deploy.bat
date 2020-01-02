SET piIpAddress=10.7.66.69

rem move to project directory for jar
cd ../../../

rem build localizer into fat jar
rem need call before gradle shadowJar because without call gradle
rem doesnt go back to command line so commands after gradle dont run
rem https://stackoverflow.com/questions/18896154/calling-gradle-from-bat-causes-batch-execution-to-stop

call gradle shadowJar --no-daemon

rem transfer files to pi
pscp -pw team766bears -r ./build/libs/monte-carlo-localizer-all.jar pi@%piIpAddress%:localizer//

rem start localizer on pi with script
putty.exe -ssh -pw team766bears -t -m src/resources/scripts/putty-start-localizer.sh pi@%piIpAddress% 22