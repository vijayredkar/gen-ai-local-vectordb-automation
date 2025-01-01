package com.genai.llm.privacy.mgt.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.genai.llm.privacy.mgt.utils.Utils;


@Service
public class FileUtilsService 
{			
	@Value("${doc.extracts.max.segment.size:500}")
	int maxSegmentSize;
	@Value("${doc.extracts.metadata.extn.type}")
	String extnType;
	
	@Autowired
	Utils utils;
	
	private static final Logger LOGGER = LogManager.getLogger();

	/*
	 * general file operations
	 */
	public List<String> readFile(String fileNameWithFullPath) throws IOException 
	{
		List<String> lines = new ArrayList<String>();
		Path path = Paths.get(fileNameWithFullPath.trim());		
		lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		
		return lines;
	}
	
	public void write(String documentTitle, String holderDBIdxName) throws IOException 
	{		
		Path path = Paths.get(utils.getMetadataPath()
						 .concat(documentTitle)
						 .concat(extnType));
		
		if(Files.notExists(path))
		{
			Files.createFile(path);
		}
		
		Files.write(path, holderDBIdxName.getBytes(), StandardOpenOption.APPEND);
		Files.write(path, "\n".getBytes(), StandardOpenOption.APPEND);
	}
	
	public void delete(String fileNameWithFullPath) throws IOException
	{
		Path path = Paths.get(fileNameWithFullPath);
		if(Files.exists(path))
		{
			Files.delete(path);
			LOGGER.info("Specified file deleted successfully");
		}
		else
		{
			LOGGER.info("Specified file does not exist. Nothing to delete.");
		}
		
	}	
	
	public String extractText(String fileNameWithFullPath) throws Exception 
	{
		BodyContentHandler handler = new BodyContentHandler();  
        AutoDetectParser parser = new AutoDetectParser();  
        Metadata metadata = new Metadata();
        
        File file = new File(fileNameWithFullPath);
        try (InputStream stream = new FileInputStream(file)) 
        {
            parser.parse(stream, handler, metadata); 
        }
        catch (IOException e) 
        {
            throw new Exception(e);
        }
        
        return handler.toString();
	}
	
	public List<String> createChunks(String text) 
	{
        List<String> words = Stream.of(text.split("\\s+")).toList();
        int numOfSegments = utils.manageSegments(words);        
        
        List<String>  chunks = IntStream.range(0, numOfSegments)
						        		 .mapToObj(i -> words.stream()
						        				 			 .skip(i * maxSegmentSize) 
						        				 			 .limit(maxSegmentSize)
						        				 			 .collect(Collectors.joining(" ")))
						        		 .collect(Collectors.toList());
		return chunks;
	}		
}