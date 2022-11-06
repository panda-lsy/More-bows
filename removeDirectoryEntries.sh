#!/bin/bash

# Rezips all built .jar files, to remove directory entries.
# This reduces the file size by a small amount.
for file in ./build/libs/*.jar
do
  unzip "$file" -d ./build/libs/temp
  rm "$file"
  for jsonFile in ./build/libs/temp/**/mcmod.info
  do
    jq -c . < "$jsonFile" > "$jsonFile-tempOut"
    mv "$jsonFile-tempOut" "$jsonFile"
  done
  # TODO replace this with standard zip
  advzip "$file" --shrink-store --pedantic -a ./build/libs/temp/**
  rm -rf ./build/libs/temp/
done
