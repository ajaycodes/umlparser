package com.umlparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class PopulateFiles {
	public List<String> classesParsed = new ArrayList<String>();
	public List<String> interfaceParsed = new ArrayList<String>();
	public List<String> classes_interfaceParsed = new ArrayList<String>();
	
	public void PopulatePFiles(String path){
		final String CONSTANT = "java";
		try {
			Files.walk(Paths.get(path)).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {

					File file1 = filePath.toFile();
					String[] splitted = file1.getName().split("\\.");

					if (splitted[1].equals(CONSTANT)) {
						try {
							FileInputStream in = new FileInputStream(filePath.toString());
							CompilationUnit cu = JavaParser.parse(in);

							//Object node = cu.getData();
							ClassOrInterfaceDeclaration cx = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
							if (cx.isInterface()) {
								interfaceParsed.add(splitted[0]);
							} else
								classesParsed.add(splitted[0]);
						} catch (Exception e) {

							e.printStackTrace();
						}

					}
				}
			});

			classes_interfaceParsed = new ArrayList<String>(classesParsed);
			classes_interfaceParsed.addAll(interfaceParsed);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public List<String> getclassesparsed()
	{
		return classesParsed;
	}
	public List<String> getinterfaceparsed()
	{
		return interfaceParsed;
	}
	public List<String> getclassesinterfacesparsed()
	{
		return 	classes_interfaceParsed;
	}
}
