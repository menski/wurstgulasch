#!/bin/bash

if [ -z "$BIND_IP" ]; then
  echo "Warning: No DNS server IP defined (export \$BIND_IP)"
  BIND_IP=$(ip route | awk '$1=="default" {print $3}')
  echo "Warning: Using gateway as \$BIND_IP == $BIND_IP"
fi

echo "nameserver $BIND_IP" > /etc/resolv.conf

if [ -z "$RUNS" ]; then
  time /usr/src/app/client.py
else
  time parallel --tag --progress -j 200% /usr/src/app/client.py ::: $(seq $RUNS)
fi

