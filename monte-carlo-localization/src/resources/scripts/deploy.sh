
# move to project directory for jar
cd ../../../

# build localizer into fat jar
# need call before gradle shadowJar because without call gradle
# doesnt go back to command line so commands after gradle dont run
# https://stackoverflow.com/questions/18896154/calling-gradle-from-bat-causes-batch-execution-to-stop
gradle shadowJar --no-daemon

# transfer files to pi
sshpass -p "team766bears" scp ./build/libs/monte-carlo-localizer-all.jar pi@10.7.66.69:localizer//

# start localizer on pi with script
sshpass -p "team766bears" ssh pi@10.7.66.69 'bash -s' < src/resources/scripts/putty-start-localizer.sh 