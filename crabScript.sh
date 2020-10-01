#!/bin/bash

# crab script crabs all.
# this script crabs tabs, and strips trailing whitespace.

unameOut="$(uname -s)"
case "${unameOut}" in
    Linux*)                            machine="Linux";;
    Darwin*)                           machine="MacOS";;
    CYGWIN*|MINGW32*|MSYS*|MINGW*)     machine="Windows";;
    *BSD*)                             machine="ProbablyBSD";;
    GNU*)                              machine="ProbablyLinux";;
    *ix|*IX|*iX|*Ix)                   machine="ProbablyUnix";;
    *ix*|*IX*|*iX*|*Ix*)               machine="PossiblyUnix";;
    *)                                 machine="UNKNOWN"
esac
echo "INFO: Only tested under a custom funky setup on MacOS. Running on ${machine}:${unameOut}."

echo "Crabbing whitespace..."

for file in build.gradle build.properties settings.gradle crabScript.sh
do
    expand -t 4 "${file}" | sponge "${file}"
    if [ ${machine} == MacOS ]; then
        gsed -i 's/[[:blank:]]*$//' ${file}
    else
        sed -i 's/[[:blank:]]*$//' ${file}
    fi
    echo "Crabbed ${file}!"
done

cd src

find -E . -iregex '.*\.(java|info|lang|cfg)' -type f -exec bash -c 'expand -t 4 "$1" | sponge "$1"' _ {} \;
if [ ${machine} == MacOS ]; then
    find -E . -iregex '.*\.(java|info|lang|cfg)' -type f -exec gsed -i 's/[[:blank:]]*$//' {} \;
else
    find -E . -iregex '.*\.(java|info|lang|cfg)' -type f -exec sed -i 's/[[:blank:]]*$//' {} \;
fi
find -E . -iregex '.*\.(java|info|lang|cfg)' -type f -exec echo "Crabbed {}!" \;

echo ":crab: WHITESPACE IS GONE :crab:"
