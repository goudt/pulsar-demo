#!/bin/bash
# Wait for the server to start
sleep 20
# Create a topic
bin/pulsar-admin topics create persistent://public/default/my-topic -n 1
