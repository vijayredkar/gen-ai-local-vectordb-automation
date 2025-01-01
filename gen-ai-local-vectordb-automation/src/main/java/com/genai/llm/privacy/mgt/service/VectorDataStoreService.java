package com.genai.llm.privacy.mgt.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.genai.llm.privacy.mgt.utils.Constants;
import com.genai.llm.privacy.mgt.utils.Utils;

import dev.langchain4j.data.document.Metadata;
import dev. langchain4j.data.embedding. Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding. EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;

@Service
public class VectorDataStoreService
{		
	@Autowired 	
	private ContextLoadService contextLoadSvc;		
	@Autowired  
	Constants constants;	
	@Autowired  
	private RetrievalService retrievalSvc;	
	@Autowired  
	private Utils utils;
	@Autowired
	FileUtilsService fileUtils;
	
	private static final Logger LOGGER = LogManager.getLogger();

	public void loadData (String text, String category, boolean testMode, String suffix, 
							String embeddingModelFullName, String vectorDbToConnect, String documentTitle)
							throws Exception
	{
		LOGGER.info("\n---- started loading data to Vector DB ");
		String vectorDbName = null;
		vectorDbName = "generic";
		suffix = handleSuffix(suffix);		
		try
		{ 
			handleInsert(text, category, testMode, embeddingModelFullName, 
						 vectorDbName, vectorDbToConnect, documentTitle);
		}
		catch(Exception e )
		{
			LOGGER.info("**** error occurred loadData VectorDB ", e);
			throw new Exception("**** error occurred loadData VectorDB ");
		}
	}
	
	/* 
	 * VectorDB operations to fetch records
	 */
	public String retrieve(String category, String userPrompt, 
						   String embeddingsMinScore, String retrievalLimitMax, String embeddingModelFullName, 
						   String vectorDbToConnect) throws Exception
	{
		LOGGER.info("\n--- started VectorDB operations");
		List<EmbeddingMatch<TextSegment>> result = fetchRecords(category, userPrompt, null, embeddingsMinScore, 
																retrievalLimitMax, embeddingModelFullName, vectorDbToConnect);
		
		StringBuilder responseBldr = new StringBuilder(); 
		StringBuilder tempResponseBldr = new StringBuilder();
		
		for (EmbeddingMatch<TextSegment> segment: result)		
		{
			responseBldr.append(segment.embedded().text());			
			tempResponseBldr.append(segment.embedded().text());
			tempResponseBldr.append("- with embedding score : ");		
			tempResponseBldr.append(segment.score());		
			tempResponseBldr.append("\n");
		}		
		LOGGER.info("--- Got most relevant record from VectorDB : \n"+tempResponseBldr.toString()); 
		LOGGER.info("--- completed VectorDB operations");		
		return responseBldr.toString();
	} 
	
	/*
	* fetches records from VectorDB based on semantic similarities
	*/	
	public List<EmbeddingMatch<TextSegment>> fetchRecords(String category, String query, String suffix, 
														  String embeddingsMinScore, String retrievalLimitMax, String embeddingModelFullName, 
														  String vectorDbToConnect) throws Exception
	{	
		EmbeddingModel embdgModel= new AllMiniLmL6V2EmbeddingModel();	
		String vectorDbCollection = category;
		EmbeddingStore<TextSegment> embdgStore = contextLoadSvc.getEmbeddingStoreForTests(embeddingModelFullName, vectorDbCollection, vectorDbToConnect);
		Embedding queryEmbedding = embdgModel.embed (query).content();
	    return  embdgStore.findRelevant(queryEmbedding, Integer.parseInt(retrievalLimitMax.trim()), Double.valueOf(embeddingsMinScore.trim()));
	}	

	private String handleSuffix(String suffix)  
	{
		if(StringUtils.isAllBlank(suffix))
		{
			return utils.generateUnique();
		}
		 return suffix;
	}
	
	private void handleInsert(String text, String category, boolean testMode,
								String embeddingModelFullName, String vectorDbName, 
								String vectorDbToConnect, String documentTitle) 
	{
		List<String> batches = retrievalSvc.createBatches(text, category);		
		final StringBuilder vectorDBNameForKnowledgeBase = new StringBuilder();
		
		batches.forEach(b -> {
			try 
			{
				StringBuilder vectorDbNameLogExtract = new StringBuilder(vectorDbName + "-"+utils.generateUnique());				
				String holderDBIdxName =insert (embeddingModelFullName, new AllMiniLmL6V2EmbeddingModel(), b, 
														  testMode, vectorDbNameLogExtract.toString(), 
														  vectorDbToConnect, documentTitle);
				
				vectorDBNameForKnowledgeBase.append(holderDBIdxName).append(",");
				fileUtils.write(documentTitle, holderDBIdxName);				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		});
	}	
		
	/*
	* inserts to VectorDB	
	*/	
	private String insert (String embeddingModelFullName, EmbeddingModel embeddingModel, String text, 
								   boolean testMode, String vectorDbCollection, String vectorDbToConnect, String documentTitle)
								   throws Exception
	{
	    String metadataIdxName ="";
	    TextSegment segment1 = null;
		Embedding embedding1 = null;
		
		LOGGER.info("---- VectorDB testMode "+testMode);			
		EmbeddingStore<TextSegment> embdStore = null;
			
		segment1 = TextSegment.from(text, new Metadata()); 
		embedding1 = embeddingModel.embed (segment1).content();
		embdStore = contextLoadSvc.getEmbeddingStoreForTests(embeddingModelFullName, vectorDbCollection, vectorDbToConnect);
		LOGGER.info("---- VectorDB connection is good ");
		if (embdStore!=null)
		{						
			embdStore.add(embedding1, segment1);
			LOGGER.info("---- loaded to VectorDB context : "+text);
			metadataIdxName = vectorDbCollection;
		}
			
		LOGGER.info("---- insertVectorData data executed");			
		return metadataIdxName;
	}
	
	/*	
	* inserts to VectorDB	
	*/	
	private void insert(String embeddingModelFullName, EmbeddingModel embeddingModel, List<String> lines, 
								   String vectorDbIndexName, String vectorDbToConnect) 
								   throws Exception 
	{
		for (String text: lines)			
		{
			TextSegment segment1 = TextSegment.from(text, new Metadata()); 
			Embedding embedding1 = embeddingModel.embed (segment1).content();
			EmbeddingStore<TextSegment> embdStore = contextLoadSvc.getEmbeddingStoreForTests(embeddingModelFullName, vectorDbIndexName, 
																							 vectorDbToConnect);			
			if (embdStore!=null)
			{
				LOGGER.info("---- VectorDB connection is good ");			
				embdStore.add(embedding1, segment1);			
				LOGGER.info("---- loaded to VectorDB context : "+text);
			}
		} 
		
		LOGGER.info("---- insertVectorData executed");
	}
	
	/*
	* loads context to VectorDB
	*/	
	public void storeRecords (String text, String embeddingModelFullName, String vectorDbIndexName, String vectorDbToConnect) 
							  throws Exception
	{
		LOGGER.info("\n---- started loading context to Vector DB ");
		List<String> lines = new ArrayList<String>();			
		lines.add(text);
		
		insert(embeddingModelFullName, new AllMiniLmL6V2EmbeddingModel(), lines, vectorDbIndexName, vectorDbToConnect);  
		LOGGER.info("---- completed loading context to VectorDB");
	}	
}
