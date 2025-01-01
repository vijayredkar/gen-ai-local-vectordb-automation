package com.genai.llm.privacy.mgt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.genai.llm.privacy.mgt.utils.Utils;

@Service
public class RetrievalService 
{
	@Value("${doc.extracts.max.batch.size}")
	private int maxBatchSize;	
	
	@Autowired
	private VectorDataStoreService vectorDataSvc;	
	@Autowired
	FileUtilsService fileUtils;
	@Autowired
	Utils utils;

	private static final Logger LOGGER = LogManager.getLogger();
	
	/*
	 * invoke Vector DB
	*/
	public String handleDataOperations(String category, String text, boolean testMode, String embeddingsMinScore, 
										  String retrievalLimitMax, String embeddingModelFullName, String vectorDbToConnect) 
										  throws Exception
	{	
		System.out.println("\n---- started orchestrateVectorDBOnly");
		
		//--step -1  : enhance the user prompt with the context information from the DB 
		String contextFromVectorDb = vectorDataSvc.retrieve(category, text, embeddingsMinScore, retrievalLimitMax, embeddingModelFullName, vectorDbToConnect);
		System.out.println("**** Ollama LLM server de-activated");
		String response = contextFromVectorDb;

		System.out.println("---- completed orchestrateVectorDBOnly response : \n"+ response);
		return response;
	}

	public List<String> createBatches(String textFullVolume, String category)
	{							
		List<String> batchesToProcess = new ArrayList<String>();
		if(textFullVolume.length() < maxBatchSize) // i/p size is under tolerance limits	
		 {
			 batchesToProcess.add(textFullVolume); 
		 }
		else
		 {
			 while(textFullVolume.length() > maxBatchSize)
			 {
				 String subBatch = textFullVolume.substring(0, maxBatchSize);
				 batchesToProcess.add(subBatch);
				 textFullVolume = textFullVolume.substring(maxBatchSize);

				 if(textFullVolume.length() < maxBatchSize)
				 { 
					batchesToProcess.add(textFullVolume);
					break;
				 }					 
			 }
		 } 			 

		 return batchesToProcess;
	}
	
	public String process(String text, String title, String vectorDbToConnect, 
						   String embeddingModelFullName) 
						   throws Exception 
	{      
		String category = "generic";		
		
		//clean up stale artifacts
		utils.refresh(title);
		
		//cleanup data
		String docText = text.replaceAll("(?m)^[ \t]*\r?\n", "");
		
		//check data extract
		if(!utils.isValidExtract(docText))
		{
			LOGGER.info("Error: cannot process data");
			return "Error: cannot process document";
		}
		
		//collect data extracts
		List<String> chunks = fileUtils.createChunks(docText);
		String result =  saveAllExtracts(title, vectorDbToConnect, 
						  	  		     embeddingModelFullName, category, chunks);		
		
		//send response
		String finalResponse = utils.handleResponse(result, title);
		return finalResponse;
	}
	
	public String saveAllExtracts(String documentTitle, String vectorDbToConnect, 
								    String embeddingModelFullName,
			  						String category, List<String> chunks) 
	{
		StringBuilder result = new StringBuilder("Successfully loaded to Vector DB");
		
		chunks.stream()
		.filter(c -> !StringUtils.isAllEmpty(c))
		.map(s -> {
			try 
			{
				vectorDataSvc.loadData(s, category, true, null, embeddingModelFullName, 
										vectorDbToConnect, documentTitle);
			}
			catch (Exception e) 
			{
				result.append("Error occurred during Vector DB load");
			}
				return s.trim();
			})
		.collect(Collectors.toList());
		
		return result.toString();
	}
	
	public void examineAllExtracts(String text, String vectorDbToConnect, String embeddingModelFullName,
								  boolean testMode, StringBuilder combinedResult, String newEmbeddingsMinScore, 
								  String newRetrievalLimitMax,  List<String> chunks) 
	{
		chunks.stream()
		.forEach(v -> {
			try 
			{
				String relevantChunk = handleDataOperations(v.trim(), text, testMode, newEmbeddingsMinScore, newRetrievalLimitMax, embeddingModelFullName, vectorDbToConnect);
				combinedResult.append(relevantChunk)
						  .append("\n\n");
			}
			catch (Exception e) 
			{
				LOGGER.error("Error occurred: ", e);
			}
			});
	}	
}
