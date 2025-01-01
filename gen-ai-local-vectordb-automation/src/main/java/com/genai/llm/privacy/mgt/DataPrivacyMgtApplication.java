package com.genai.llm.privacy.mgt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataPrivacyMgtApplication 
{	
public static void main(String[] args) 
	{
		System.out.println("\n******************** Pre-requisites: ********************");
		System.out.println("******************** Launch setup.bat located under the parent dir ********************");
		System.out.println("******************** Ensure that your Vector DB running instances are reachable ********************\n");
	    
		SpringApplication.run(DataPrivacyMgtApplication.class, args);		
	}	
}
