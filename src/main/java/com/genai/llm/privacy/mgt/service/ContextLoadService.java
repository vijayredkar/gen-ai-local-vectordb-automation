package com.genai.llm.privacy.mgt.service;

import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework. stereotype.Service;

import dev. langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.store.embedding.weaviate.WeaviateEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Collections.Distance;

@Service
public class ContextLoadService
{	
	private static final Logger LOGGER = LogManager.getLogger();	
	private EmbeddingStore<TextSegment> embeddingStore = null;	
	
	public EmbeddingStore<TextSegment> getEmbeddingStoreForTests(String embeddingModelFullName, String vectorDbCollection, 
																 String vectorDbToConnect) throws Exception
	{	
		int dimensions = 384;
		try
		{
			
			if("chromadb".equals(vectorDbToConnect))
			{
				embeddingStore = ChromaEmbeddingStore.builder()
													.baseUrl ("http://127.0.0.1:8000")
													.collectionName (vectorDbCollection)
													.build();					
				
			}
			else if("elasticsearch".equals(vectorDbToConnect))
			{					
				LOGGER.info("---- Elasticsearch dimension set to: "+dimensions);
				embeddingStore = ElasticsearchEmbeddingStore.builder()		
														.serverUrl("http://127.0.0.1:9200")											
														.dimension (dimensions)												
														.indexName (vectorDbCollection)												
														.build();
			}
			else if("redis".equals(vectorDbToConnect))
			{					
				LOGGER.info("---- Redis dimension set to: "+dimensions);
				
				embeddingStore = RedisEmbeddingStore.builder()
							            .host("localhost")
							            .port(6379)
							            .dimension(dimensions)
							            .build();
			}
			else if("milvus".equals(vectorDbToConnect))
			{					
				LOGGER.info("---- Milvus dimension set to: "+dimensions);
				
				embeddingStore = MilvusEmbeddingStore.builder()
							                    .uri("localhost:????")
							                    .collectionName("test_collection")
							                    .dimension(dimensions)
							                    .build();
			}
			else if("weaviate".equals(vectorDbToConnect))
			{					
				LOGGER.info("---- Weaviate dimension set to: "+dimensions);
				
				embeddingStore = WeaviateEmbeddingStore.builder()
						                    .scheme("http")
						                    .host("localhost:8080")
						                    /* 
						                     "Default" class is used if not specified. Must start from an uppercase letter!
	                    					  If true (default), then WeaviateEmbeddingStore will generate a hashed ID based on provided 
	                    					*/
						                    .objectClass("Test")							                    
						                    .avoidDups(true) // text segment, which avoids duplicated entries in DB. If false, then random ID will be generated.							                    
						                    .consistencyLevel("ALL") // Consistency level: ONE, QUORUM (default) or ALL.
						                    .build();
			}				
			else if("qdrant".equals(vectorDbToConnect))
			{					
				QdrantClient client = null;					
				try 
				{
					//create Qdrant client
					client = new QdrantClient(QdrantGrpcClient.newBuilder("localhost", 6334, false).build());
					LOGGER.info("---- Qdrant Client created successfully");
				}
				catch(Exception e)
				{
					LOGGER.info("\n**** Error: Qdrant - Fatal: \n"+e.getMessage() + "\n");
					throw new Exception(e);
				}	
					
				try
				{
					//invoke client to create or ensure that the requested collection exists. This is a Qdrant pre-requisite
					client.createCollectionAsync(vectorDbCollection, Collections.VectorParams.newBuilder().setDistance(Distance.Dot).setSize(dimensions)
			    			.build())
			              	.get();
					LOGGER.info("---- Qdrant collection created successfully: "+vectorDbCollection);
				}
				catch(ExecutionException e)
				{
					if(e.getMessage().contains("Wrong input: Collection"))
					{
						LOGGER.info("\n**** Qdrant ExecutionException - can be ignored. Requested collection is already available: \n"+e.getMessage() + "\n");	
					}
					else
					{
						LOGGER.info("\n**** Error: Qdrant - Fatal: \n"+e.getMessage() + "\n");
						throw new Exception(e);	
					}						
				}	
					
				try
				{
					//create db connection with Langchain4J
					embeddingStore = QdrantEmbeddingStore.builder()
														.host("localhost")
														.collectionName (vectorDbCollection)
														.build();	
				}					
				catch(Exception e)
				{
					LOGGER.info("\n**** Error: Qdrant - Fatal: \n"+e.getMessage() + "\n");
					throw new Exception(e);
				}
				
				client.close();			    
			}				
			else
			{
				LOGGER.info("**** Error: Invalid VectorDB configuration. No connection obtained.");
				throw new Exception("**** Error: Invalid VectorDB configuration. No connection obtained.");
			}
			
		}
		catch (Exception e)
		{
			LOGGER.info("**** Error: Cannot connect to VectorDB: " +e);// scenario local machine does not have running instance of vectors.
			throw new Exception("**** Error: Cannot connect to VectorDB");
		}
		
		LOGGER.info("---- completed connect to VectorDB for tests. Got embeddingStore " +embeddingStore);
		return embeddingStore;
	}
}
