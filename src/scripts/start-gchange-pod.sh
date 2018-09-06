#!/bin/bash


PID=`ps -efl | grep gchange-pod | grep lib/elasticsearch > /tmp/ps_out && cat /tmp/ps_out | awk '/^([0-9]+) ([^\s]) ([a-zA-Z0-9]+) ([0-9]+).*/ {printf "%s", $4}'`

if [ "$PID" != "" ];
then
        echo "Error: Gchange-pod already started!"
        exit -1
else
        cd /opt/gchange-pod/bin
        ./elasticsearch -d
        echo "Gchange-pod started !"
        echo "  To follow log, execute the command:"
        echo "  > tail -f /opt/gchange-pod/logs/gchange-pod.log"
fi

