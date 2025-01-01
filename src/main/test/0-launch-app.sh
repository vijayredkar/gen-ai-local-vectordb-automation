#!/bin/bash
echo
echo building application
cd /c/Vijay/Java/projects/openapi-ai-trials/LLM-gen-ai/langchain4j/gen-ai-local-vectordb-automation
mvn clean install

echo
echo launching application
java -jar target/gen-ai-local-vectordb.jar Standalone-LOCAL-MC internal