curl -X 'POST' \
  'http://localhost:8888/gen-ai/v1/llm/data-extracts?title=extract-PCI-DSS&vectorDbToConnect=chromadb&embeddingModelFullName=sentence-transformers%2Fall-MiniLM-L6-v2' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d 'PCI DSS Self-Assessment Completion Steps 
1. Confirm by review of the eligibility criteria in this SAQ and the Self-Assessment Questionnaire Instructions 
and Guidelines document on the PCI SSC website that this is the correct SAQ for the merchant’s 
environment.  
2. Confirm that the merchant environment is properly scoped.  
3. Assess the environment for compliance with PCI DSS requirements. 
4. Complete all sections of this document: 
Section 1: Assessment Information (Parts 1 & 2 of the Attestation of Compliance (AOC) – Contact 
Information and Executive Summary). 
Section 2 – Self-Assessment Questionnaire A. 
Section 3: Validation and Attestation Details (Parts 3 & 4 of the AOC – PCI DSS Validation and Action 
Plan for Non-Compliant Requirements (if Part 4 is applicable)). 
5. Submit the SAQ and AOC, along with any other requested documentation—such as ASV scan reports—to 
the requesting organization (those organizations that manage compliance programs such as payment brands 
and acquirers). 
Expected Testing 
The instructions provided in the “Expected Testing” column are based on the testing procedures in PCI DSS and 
provide a high-level description of the types of testing activities that a merchant is expected to perform to verify 
that a requirement has been met.  
The intent behind each testing method is described as follows: 
Examine: The merchant critically evaluates data evidence. Common examples include documents 
(electronic or physical), screenshots, configuration files, audit logs, and data files. 
Observe: The merchant watches an action or views something in the environment. Examples of observation 
subjects include personnel performing a task or process, system components performing a function or 
responding to input, environmental conditions, and physical controls.
PCI DSS Self-Assessment Completion Steps 
1. Confirm by review of the eligibility criteria in this SAQ and the Self-Assessment Questionnaire Instructions 
and Guidelines document on the PCI SSC website that this is the correct SAQ for the merchant’s 
environment.  
2. Confirm that the merchant environment is properly scoped.  
3. Assess the environment for compliance with PCI DSS requirements. 
4. Complete all sections of this document: 
Section 1: Assessment Information (Parts 1 & 2 of the Attestation of Compliance (AOC) – Contact 
Information and Executive Summary). 
Section 2 – Self-Assessment Questionnaire A. 
Section 3: Validation and Attestation Details (Parts 3 & 4 of the AOC – PCI DSS Validation and Action 
Plan for Non-Compliant Requirements (if Part 4 is applicable)). '

echo