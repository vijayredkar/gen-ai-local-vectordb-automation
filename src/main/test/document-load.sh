curl -X 'POST' \
  'http://localhost:8888/gen-ai/v1/llm/document-extracts?fileNameWithFullPath=C%3A%5CVijay%5CJava%5Cprojects%5Copenapi-ai-trials%5CLLM-gen-ai%5Clangchain4j%5Cgen-ai-local-vectordb-automation%5Ctraining-docs%5Cformat-pdf%5CPCI-DSS-4.pdf&title=PCI-DSS-4&vectorDbToConnect=chromadb&embeddingModelFullName=sentence-transformers%2Fall-MiniLM-L6-v2' \
  -H 'accept: */*' \
  -d ''

echo