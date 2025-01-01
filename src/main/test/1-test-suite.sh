#!/bin/bash
echo running tests 
cd /c/Vijay/Java/projects/openapi-ai-trials/LLM-gen-ai/langchain4j/gen-ai-local-vectordb-automation/src/main/test
echo
echo testing data-load
./data-load.sh 

echo
sleep 2s
echo testing document-load
./document-load.sh 
echo

echo
sleep 2s
echo fetch-extracts
./fetch-extracts.sh
