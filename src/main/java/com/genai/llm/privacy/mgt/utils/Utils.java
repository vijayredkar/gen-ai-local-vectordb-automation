package com.genai.llm.privacy.mgt.utils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.genai.llm.privacy.mgt.service.FileUtilsService;

@Component
public class Utils 
{	
	@Value("${doc.extracts.metadata.extn.type}")
	String extnType;
	@Value("${doc.extracts.max.segment.size:500}")
	int maxSegmentSize;
	
	@Autowired
	FileUtilsService fileUtils;
	
 public void refresh(String fileName) throws Exception 
	{
		 fileUtils.delete(getMetadataPath()
							.concat(fileName)
							.concat(extnType));
	}	
	
  public String handleResponse(String message, String documentTitle) 
	{
	  String result = null;
	  
	  if(message.contains("error"))
		{
			 result = "Error occurred: ".concat(message);	
		}
	  
	  String referencePath = getMetadataPath()
			  					.concat(documentTitle)
			  					.concat(extnType);
		
	  result = "Document extracted and loaded to the Vector DB successfully. "
							  .concat("\n Vector DB Metadata saved at: \n")
							  .concat(referencePath);
		
		return result;
	}

  public boolean isValidExtract(String input) 
  {
	  return !StringUtils.isAllEmpty(input);
  }
  
  public String generateUnique() 
  {
		String suffix;
		int uniqueId = UUID.randomUUID().hashCode();
		uniqueId = Math.abs(uniqueId);
		suffix = String.format("%07d", uniqueId);
		return suffix;
  }  

  public boolean isVectorDBAvailable(String vectorDbToConnect) throws IOException 
  {
	  return
		  fileUtils.readFile(getDbInfoPath())
		  		   .stream()
		  		   .anyMatch(l -> l.contains(vectorDbToConnect));
  }
  
  public String constructTitle(String name) 
  {
		return name.concat("-")
					.concat(generateUnique());
  }
  
  public int manageSegments(List<String> words) 
  {
      int numOfSegments = (int)Math.ceil(words.size()/maxSegmentSize);
      if(numOfSegments == 0)
      {
      	numOfSegments = 1;
      }
	
      return numOfSegments;
  }
  
  public String getMetadataPath() 
	{
	  String currentDir = System.getProperty("user.dir");
	  return currentDir + "\\" + "knowledge-repo\\";
	}
  
  public String getDbInfoPath() 
	{
	  String currentDir = System.getProperty("user.dir");
	  return currentDir + "\\" + "db-info\\available-vectordbs.txt";
	}
}
