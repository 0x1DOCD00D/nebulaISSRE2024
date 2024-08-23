#!/bin/sh
echo "Welcome to the Spawner application!"
echo "I'll init the ActorSystems for you..."

pssh -A -i -H "10.7.44.49 10.7.44.71" -l andrea java -jar spawner.jar
