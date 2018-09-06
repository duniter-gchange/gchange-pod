#!/bin/bash

# Get Gchange-pod PID
# (we use /tmp/ps_out as a workaround, to skip Warning message that can be sent by /bin/ps
PID=`ps -efl | grep gchange-pod | grep lib/elasticsearch > /tmp/ps_out && cat /tmp/ps_out | awk '{printf "%s", $4}'`

if [ "$PID" != "" ];
then
        echo "Stopping Gchange-pod running on PID $PID..."
        sudo kill -15 $PID

        sleep 5s

        # Check if still alive
        PID=`ps -efl | grep gchange-pod | grep lib/elasticsearch > /tmp/ps_out && cat /tmp/ps_out | awk '{printf "%s", $4}'`
        if [ "$PID" != "" ];
        then
                sleep 10s
        fi

        PID=`ps -efl | grep gchange-pod | grep lib/elasticsearch > /tmp/ps_out && cat /tmp/ps_out | awk '{printf "%s", $4}'`
        if [ "$PID" != "" ];
        then 
                echo "Error: Unable to stop Gchange-pod !"
                exit -1
        else
                echo "Gchange-pod stopped"
        fi

else
        echo "Gchange-pod not running!"
fi

