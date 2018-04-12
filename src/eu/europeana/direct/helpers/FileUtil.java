package eu.europeana.direct.helpers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

	public String readFile(String path) throws IOException{
		StringBuilder sb = new StringBuilder();
		FileInputStream is = new FileInputStream(path);
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");

		try (BufferedReader br = new BufferedReader(isr)){
		String sCurrentLine;
		    while ((sCurrentLine = br.readLine()) != null) {
		        sb.append(sCurrentLine);
		    }
		
		}
      return sb.toString();
	}
}
