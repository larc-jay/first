package com.es;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class FormatData {
	public static void main(String[] args) throws IOException {
		/*try (Stream<String> stream = Files.lines(Paths.get("D:\\result\\User6"))) {

			stream.forEach(System.out::println);
		}catch(Exception e){
			e.printStackTrace();
		}*/
		List<Path> files = new ArrayList<Path>();
		try (Stream<Path> filePathStream=Files.walk(Paths.get("D:/result/User6/"))) {
			filePathStream.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					files.add(filePath);
				}
			});
		}


		File fout = new File("D:/result/dataTwo.json");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		for(Path path: files){
			BufferedReader br = new BufferedReader(new FileReader(path.getParent()+"\\"+path.getFileName()));
			String line ="";
			StringBuffer sb = new StringBuffer();
			while((line=br.readLine())!=null){
				if(line!=null){
					sb.append(line);
				}
			}
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(sb.toString());
			JsonNode jsonNode = json.path("allposts");
			if(jsonNode.isArray()){
				for(JsonNode js : jsonNode){
					//System.out.println(js);
					bw.write(js.toString());
					bw.newLine();
				}
			}
		}
		bw.close();
	}
}
