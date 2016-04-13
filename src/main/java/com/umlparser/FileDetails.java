package com.umlparser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileDetails {
	public String path="";
	public String name="";
	public boolean isInterface=false;
	public List<String	> classesExtends= new ArrayList<String>();
	public Set<String	> interfaceclassesUsed_Methods= new HashSet<String>();
	public List<String	> interfaceImplemented= new ArrayList<String>();
	
	public	List<String	> methodsDec= new ArrayList<String>();
	
	public List<String	> att= new ArrayList<String>();
	
	public List<String> constDeclared=new ArrayList<String>();
	
	@Override
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append("filepath:" + path);
		sb.append("\r\n "+"name:" +name);
		sb.append("\r\n "+"isInterface:" + isInterface);
		sb.append("\r\n "+"classesExtends:"+classesExtends);
		sb.append("\r\n "+"interfaceclassesUsed_Methods:"+interfaceclassesUsed_Methods);
		sb.append("\r\n "+"interfaceImplemented:"+interfaceImplemented);
		sb.append("\r\n "+"methodsDec:"+methodsDec);
		sb.append("\r\n "+"methodsDec:"+methodsDec);
		sb.append("\r\n "+"attributes:"+att);
		sb.append("\r\n "+"Done" + "\r\n ");
		
		return sb.toString() ;
		
	}

}
