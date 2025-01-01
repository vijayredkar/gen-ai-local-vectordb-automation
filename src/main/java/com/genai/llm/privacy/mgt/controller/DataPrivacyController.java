package com.genai.llm.privacy.mgt.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation. PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation. RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind. annotation. RestController;

import com.genai.llm.privacy.mgt.service.FileUtilsService;
import com.genai.llm.privacy.mgt.service.RetrievalService;
import com.genai.llm.privacy.mgt.utils.Utils;

@RestController
@RequestMapping(value="/gen-ai/v1/llm")
public class DataPrivacyController
{
	@Autowired
	private RetrievalService retrievalSvc;
	@Autowired
	FileUtilsService fileUtils;
	@Autowired
	Utils utils;
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	/* 
	 * endpoint to load data directly in to the VectorDB
	*/	
	@PostMapping("/data-extracts")
	public ResponseEntity<String> saveDataExtractToVectorDb(@RequestBody String text, 
															@RequestParam(required = true) String title,
															@RequestParam(defaultValue = "chromadb") String vectorDbToConnect,
															@RequestParam(defaultValue = "sentence-transformers/all-MiniLM-L6-v2") String embeddingModelFullName)																
														throws Exception
	{
		String finalResponse = retrievalSvc.process(text, title, vectorDbToConnect, embeddingModelFullName);
		return new ResponseEntity<>(finalResponse, HttpStatus.OK);
	}
	
	/* 
	 * endpoint to load document directly in to the VectorDB
	*/
	@PostMapping("/document-extracts")
	public ResponseEntity<String> saveDocExtractToVectorDb( @RequestParam(required = true) String fileNameWithFullPath,
															@RequestParam(required = true) String title,
															@RequestParam(defaultValue = "chromadb") String vectorDbToConnect,
															@RequestParam(defaultValue = "sentence-transformers/all-MiniLM-L6-v2") String embeddingModelFullName)																
														throws Exception
	{
		//clean up stale artifacts
		utils.refresh(title);
		
		//extract document
		String docText = fileUtils.extractText(fileNameWithFullPath);
		docText = docText.replaceAll("(?m)^[ \t]*\r?\n", "");
		
		String finalResponse = retrievalSvc.process(docText, title, vectorDbToConnect, embeddingModelFullName);
		return new ResponseEntity<>(finalResponse, HttpStatus.OK);
	}
	
	/*
	 * endpoint to fetch response directly from the VectorDB only
	*/
	@GetMapping("/extracts")
	public ResponseEntity<String> invokeVectorDBOnlyDocPayload(@RequestParam("text") String text,
													 @RequestParam(required = true) String vectorDbToConnect,
													 @RequestParam(value = "vectorDbMetadataPath", required = true) String vectorDbMetadataPath,	
													 @RequestParam(value = "embeddingsMinScore", required = false, defaultValue = "0.5") String embeddingsMinScore,
													 @RequestParam(defaultValue = "sentence-transformers/all-MiniLM-L6-v2") String embeddingModelFullName,				
												     @RequestParam(value = "temperature", required = false, defaultValue = "0.5") String temperature,
												     @RequestParam(value = "retrievalLimitMax", required = false, defaultValue = "5") String retrievalLimitMax,
												     @RequestParam(value = "htmlOutput", required = false, defaultValue = "") String htmlOutput) throws Exception 
	{
		boolean testMode= true;
		String response = null;
		System.out.println("\n---- started invokeVectorDBOnlyDocPayload - mode : "+testMode);
		
		if(!utils.isVectorDBAvailable(vectorDbToConnect))
		{
			response = "Specified Vector Db is unavailable. Valid options are: ";
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);	
		}
		
		 StringBuilder combinedResult = new StringBuilder();		 
		 List<String> docChunks = fileUtils.readFile(vectorDbMetadataPath);		
		  retrievalSvc.examineAllExtracts(text, vectorDbToConnect, embeddingModelFullName, testMode, combinedResult,
				 						   		embeddingsMinScore, retrievalLimitMax, docChunks);
		 
		return new ResponseEntity<>(combinedResult.toString().trim(), HttpStatus.OK);
	}	
}
