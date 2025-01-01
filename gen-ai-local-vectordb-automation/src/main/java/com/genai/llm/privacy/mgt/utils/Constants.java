package com.genai.llm.privacy.mgt.utils;

import org.springframework.stereotype.Component;


@Component
public class Constants {
			
	public String handleFormat(String type)
	{
		String result = "";
		if("Y".equals(type.trim())) //only HTML for now
		{
			result = "\n Show the response in a HTML bullet list format for better readability on the browser. ";
		}
		
		return result;		
	}	
		
	public String extractedTextForTesting() 
	{
		String docText = 
				"PCI DSS Self-Assessment Completion Steps \r\n"
        		+ "1. Confirm by review of the eligibility criteria in this SAQ and the Self-Assessment Questionnaire Instructions \r\n"
        		+ "and Guidelines document on the PCI SSC website that this is the correct SAQ for the merchant’s \r\n"
        		+ "environment.  \r\n"
        		+ "2. Confirm that the merchant environment is properly scoped.  \r\n"
        		+ "3. Assess the environment for compliance with PCI DSS requirements. \r\n"
        		+ "4. Complete all sections of this document: \r\n"
        		+ "Section 1: Assessment Information (Parts 1 & 2 of the Attestation of Compliance (AOC) – Contact \r\n"
        		+ "Information and Executive Summary). \r\n"
        		+ "Section 2 – Self-Assessment Questionnaire A. \r\n"
        		+ "Section 3: Validation and Attestation Details (Parts 3 & 4 of the AOC – PCI DSS Validation and Action \r\n"
        		+ "Plan for Non-Compliant Requirements (if Part 4 is applicable)). \r\n"
        		+ "5. Submit the SAQ and AOC, along with any other requested documentation—such as ASV scan reports—to \r\n"
        		+ "the requesting organization (those organizations that manage compliance programs such as payment brands \r\n"
        		+ "and acquirers). \r\n"
        		+ "Expected Testing \r\n"
        		+ "The instructions provided in the “Expected Testing” column are based on the testing procedures in PCI DSS and \r\n"
        		+ "provide a high-level description of the types of testing activities that a merchant is expected to perform to verify \r\n"
        		+ "that a requirement has been met.  \r\n"
        		+ "The intent behind each testing method is described as follows: \r\n"
        		+ "Examine: The merchant critically evaluates data evidence. Common examples include documents \r\n"
        		+ "(electronic or physical), screenshots, configuration files, audit logs, and data files. \r\n"
        		+ "Observe: The merchant watches an action or views something in the environment. Examples of observation \r\n"
        		+ "subjects include personnel performing a task or process, system components performing a function or \r\n"
        		+ "responding to input, environmental conditions, and physical controls.\r\n"
        		+ "PCI DSS Self-Assessment Completion Steps \r\n"
        		+ "1. Confirm by review of the eligibility criteria in this SAQ and the Self-Assessment Questionnaire Instructions \r\n"
        		+ "and Guidelines document on the PCI SSC website that this is the correct SAQ for the merchant’s \r\n"
        		+ "environment.  \r\n"
        		+ "2. Confirm that the merchant environment is properly scoped.  \r\n"
        		+ "3. Assess the environment for compliance with PCI DSS requirements. \r\n"
        		+ "4. Complete all sections of this document: \r\n"
        		+ "Section 1: Assessment Information (Parts 1 & 2 of the Attestation of Compliance (AOC) – Contact \r\n"
        		+ "Information and Executive Summary). \r\n"
        		+ "Section 2 – Self-Assessment Questionnaire A. \r\n"
        		+ "Section 3: Validation and Attestation Details (Parts 3 & 4 of the AOC – PCI DSS Validation and Action \r\n"
        		+ "Plan for Non-Compliant Requirements (if Part 4 is applicable)). \r\n"
        		+ "5. Submit the SAQ and AOC, along with any other requested documentation—such as ASV scan reports—to \r\n"
        		+ "the requesting organization (those organizations that manage compliance programs such as payment brands \r\n"
        		+ "and acquirers). \r\n"
        		+ "Expected Testing \r\n"
        		+ "The instructions provided in the “Expected Testing” column are based on the testing procedures in PCI DSS and \r\n"
        		+ "provide a high-level description of the types of testing activities that a merchant is expected to perform to verify \r\n"
        		+ "that a requirement has been met.  \r\n"
        		+ "The intent behind each testing method is described as follows: \r\n"
        		+ "Examine: The merchant critically evaluates data evidence. Common examples include documents \r\n"
        		+ "(electronic or physical), screenshots, configuration files, audit logs, and data files. \r\n"
        		+ "Observe: The merchant watches an action or views something in the \r\n"
        		+ "\r\n"
        		+ "\r\n"
        		+ "\r\n"
        		+ "";
        docText =  docText.replaceAll("(?m)^[ \t]*\r?\n", "");
		return docText;
	}
}
