#!/bin/bash
cat << EOF > network-up-probe.out

Boot time:        $(uptime -s)
Network up time:  $(date '+%Y-%m-%d %H:%M:%S')

$(ping -c 5 fotahub.com)

EOF