#!/bin/sh

IFTTT_EVENT="temperature_alarm"
IFTTT_KEY="j0-wI-LTDh1lU7yJ9-T2MOEG8S5rz54s8c-FCDaVSzU"

ALARM_THRESHOLD=58

SIMULATED="y"
SIMULATED_MIN=40
SIMULATED_MAX=60

DEGREE_CELSIUS=$(echo -e '\xc2\xb0')C

read_core_temperature()
{
    # Requires 'userland' package to be installed through IMAGE_INSTALL - won't work from within a Docker container though 
    echo $(vcgencmd measure_temp) | egrep -o '[0-9]{2}.[0-9]{1}'
}

read_simulated_temperature()
{
    shuf -i $SIMULATED_MIN-$SIMULATED_MAX -n 1
}
 
while true
do
    if [ -z "$SIMULATED" ]; then
        coreTemp=$(read_core_temperature)
    else
        coreTemp=$(read_simulated_temperature)
    fi
   
    echo "BCM2835 SoC core temperature: $coreTemp$DEGREE_CELSIUS"
    
    if [ $coreTemp -ge $ALARM_THRESHOLD ]; then
        echo "Alarm level reached or exceeded"
        # echo "Alarm level reached or exceeded, sending notification..."
        # curl --silent --output /dev/null --show-error --fail \
        #   -H "Content-Type: application/json" -d "{\"CoreTemperature\":\"$coreTemp\"}" \
        #   https://maker.ifttt.com/trigger/$IFTTT_EVENT/json/with/key/$IFTTT_KEY
    fi
    
    sleep 1
done
