package com.umlparser;

import java.io.*;
import java.awt.image.BufferedImage;
import java.nio.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;

import net.sourceforge.plantuml.SourceStringReader;
//import test1.FileInfo;

import com.umlparser.PopulateFiles;
import com.umlparser.FileDetails;

public class umlParser {

	static List<String> classesinterpreted = new ArrayList<String>();
	static List<String> interfacesinterpreted = new ArrayList<String>();
	static List<String> classes_interfaceinterpreted = new ArrayList<String>();
	
	public static Set<String> finalset_collection_association = new HashSet<String>();
	
	static final String otoM = new String("\"1\"--\"*\""); //plantuml notation one to many
	static final String ztoM = new String("\"0\"--\"*\""); //plantuml notation zero to many

	static final String ztoO = new String("\"0\"--\"1\""); //plantuml notation zero to one
	static final String otoO = new String("\"1\"--\"1\""); //plantuml notation one to one

	static final String word_ztoM = new String("\"0\"--\"many\"");
	
	public static List<String> collection_association = new CopyOnWriteArrayList<String>();
	public static List<String> ass = new CopyOnWriteArrayList<String>();

	public static Set<String> finalset_ass = new HashSet<String>();
	public static Set<String> finalset_collectionarrassociation = new HashSet<String>();
	
	public umlParser() {
		collection_association = new CopyOnWriteArrayList<String>();
		ass = new CopyOnWriteArrayList<String>();
		classesinterpreted = new ArrayList<String>();
		interfacesinterpreted = new ArrayList<String>();
		classes_interfaceinterpreted = new ArrayList<String>();
	}
	
	public  static void main(String[] args)
	{
		FileInputStream input = null;
		CompilationUnit cu = null;
		List<ClassOrInterfaceType> extend = null;
		List<ClassOrInterfaceType> interfaceL = null;
		String inputpath = args[0];
		
		PopulateFiles f=new PopulateFiles();
		f.PopulatePFiles(inputpath);
		umlParser pu=new umlParser();
		
		classesinterpreted=f.getclassesparsed();
		classes_interfaceinterpreted=f.getclassesinterfacesparsed();
		interfacesinterpreted=f.getinterfaceparsed();
		
		FileDetails filedetailst = null;
		List<FileDetails> listFileDetails = new ArrayList<FileDetails>();
		ClassOrInterfaceDeclaration ci = null;

		final File Path = new File(inputpath);
		final String CONSTANT = "java";

		for (final File Entrypath : Path.listFiles()) {
			ci = null;

			String absolutePath = Entrypath.getAbsolutePath();

			String filetype = absolutePath.substring(absolutePath.lastIndexOf('.') + 1);
			
			if ((!Entrypath.isDirectory()) && (filetype.equals(CONSTANT))) {

				String filename = Entrypath.getName();

				String[] absfilename = filename.split("\\.");

				filedetailst = new FileDetails();
				filedetailst.name = absfilename[0];
				filedetailst.path = absolutePath;
				try {
					input = new FileInputStream(Entrypath);
					cu = JavaParser.parse(input);
					Object node = cu.getData();

					for (TypeDeclaration typeDeclaration : cu.getTypes()) {
						if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
							ci = (ClassOrInterfaceDeclaration) typeDeclaration;
							break;
						}
					}

					if (ci.isInterface()) {
						filedetailst.isInterface = true;
					}
					
					extend = ci.getExtends();

					if (extend != null) {
						for (ClassOrInterfaceType ciType : extend) {

							filedetailst.classesExtends.add(ciType.toString());
							filedetailst.classesExtends.retainAll(classesinterpreted);
						}
					}
					interfaceL = ci.getImplements();
					if (interfaceL != null) {
						for (ClassOrInterfaceType ciType : interfaceL) {

							filedetailst.interfaceImplemented.add(ciType.toString());
							filedetailst.interfaceImplemented.retainAll(interfacesinterpreted);
						}
					}
					List<Node> nodes = ci.getChildrenNodes();
					if (nodes != null) {
						for (Node nodeChild : nodes) {
							if (nodeChild instanceof MethodDeclaration) {
								pu.populateMethodS((MethodDeclaration) nodeChild, filedetailst);
							}
							if (nodeChild instanceof FieldDeclaration) {
								pu.populateAtt((FieldDeclaration) nodeChild, filedetailst);
							}
							if (nodeChild instanceof ConstructorDeclaration) {
								pu.populateConstructors((ConstructorDeclaration) nodeChild, filedetailst);
							}
						}
					}
					filedetailst.interfaceclassesUsed_Methods.retainAll(classes_interfaceinterpreted);

					listFileDetails.add(filedetailst);
		} catch (FileNotFoundException | ParseException e) {

			e.printStackTrace();
		}
	}
	

		}
		
		String outputpath = args[0];
		String outputfilename = args[1];
		transform_associations();
		finalset_ass = new HashSet<>(ass);
		finalset_collectionarrassociation = new HashSet<>(collection_association);

		createDiagram(listFileDetails, outputpath, outputfilename);
		System.out.println("UML output generated ..!!");
	}
	
	private static void transform_associations() {
		System.out.println("inside transform_associations");
		String[] splitstring = null;

		for (Iterator<String> it = ass.iterator(); it.hasNext();) {

			String s = it.next();

			StringBuilder chkr = new StringBuilder();
			splitstring = s.split(ztoO);
			if (!splitstring[0].equals(s)) {
				chkr.append(splitstring[1]);
				chkr.append(ztoO);
				chkr.append(splitstring[0]);

				if (ass.contains(chkr.toString())) {
					ass.remove(chkr.toString());
					ass.remove(s);

					StringBuilder new1to1entry = new StringBuilder();
					new1to1entry.append(splitstring[1]);
					new1to1entry.append(otoO);
					new1to1entry.append(splitstring[0]);
					ass.add(new1to1entry.toString());
				}
			}
		}

		String iterator0toMany = null;
		for (Iterator<String> it = collection_association.iterator(); it.hasNext();) {

			iterator0toMany = it.next();

			StringBuilder chkr = new StringBuilder();
			splitstring = iterator0toMany.split(word_ztoM);

			if (!iterator0toMany.equals(splitstring[0]))
				chkr.append(splitstring[1]);
			chkr.append(ztoO);
			chkr.append(splitstring[0]);

			String chkrString = chkr.toString();

			if (ass.contains(chkrString)) {

				ass.remove(chkrString);

				collection_association.remove(iterator0toMany);

				StringBuilder newentry_bldr = new StringBuilder();
				newentry_bldr.append(splitstring[0]);
				newentry_bldr.append(otoM);
				newentry_bldr.append(splitstring[1]);

				collection_association.add(newentry_bldr.toString());
			}
		}

		for (int i = 0; i < collection_association.size(); i++) {

			collection_association.set(i, collection_association.get(i).replace(word_ztoM, ztoM));
		}

	}

private static void createDiagram(List<FileDetails> listFileDetails, String path, String outputfilename) {
		
		System.out.println("inside create diagram..");
		ByteArrayOutputStream bous = new ByteArrayOutputStream();

		final String interfaceConstant = " <|.. ";
		final String classExtdConstant = " <|-- ";
		final String usesConstant = " ..> ";

		StringBuilder sbr = new StringBuilder();
		sbr.append("\n");
		sbr.append("@startuml\n");
		sbr.append("skinparam classAttributeIconSize 0\n");

		String filename = "";
		for (FileDetails fileDetails : listFileDetails) {
			filename = fileDetails.name;

			if (fileDetails.isInterface)
				sbr.append("interface ");

			else
				sbr.append("class ");

			sbr.append(filename);
			sbr.append("{\n");

			for (String s : fileDetails.att) {

				sbr.append(s);
				sbr.append("\n");

			}
			for (String s : fileDetails.methodsDec) {

				sbr.append(s);
				sbr.append("\n");

			}

			for (String s : fileDetails.constDeclared) {

				sbr.append(s);
				sbr.append("\n");

			}

			sbr.append("}\n");
			
			for (String interfaceimplt : fileDetails.interfaceImplemented) {
				sbr.append(interfaceimplt);
				sbr.append(interfaceConstant);
				sbr.append(filename);
				sbr.append("\n");
			}

		
			for (String classextd : fileDetails.classesExtends) {
				sbr.append(classextd);
				sbr.append(classExtdConstant);
				sbr.append(filename);
				sbr.append("\n");
			}

			
			for (String uses : fileDetails.interfaceclassesUsed_Methods) {
				sbr.append(filename);
				sbr.append(usesConstant);
				sbr.append(uses);
				sbr.append("\n");
			}

		}
		

		for (String uses : finalset_ass) {
			sbr.append(uses);
			sbr.append("\n");
		}

		for (String uses : finalset_collectionarrassociation) {
			sbr.append(uses);
			sbr.append("\n");
		}

		sbr.append("@enduml\n");
		System.out.println(sbr);
		OutputStream png;
		String filePath = "classDiagram.png";
		try {
			png = new FileOutputStream(new File(path + "/" + outputfilename + ".png"));

			SourceStringReader reader = new SourceStringReader(sbr.toString());
			reader.generateImage(png);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	
	void populateMethodS(MethodDeclaration m, FileDetails fileDetails) {
		System.out.println("inside method declaration");
		//BlockStmt blockStmt = n.getBody();

		String string = m.getDeclarationAsString(true, false);

		List<Parameter> parlist = m.getParameters();

		StringBuilder s = new StringBuilder();

		boolean morePars = false;

		for (Parameter p : parlist) {

			if (morePars) {
				s.append(",");
			}
			morePars = true;

			s.append(p.getId().getName());
			s.append(":");
			s.append(p.getType());

		}

		StringBuilder sbrmtd = new StringBuilder();

		if (m.getModifiers() == 1 || m.getModifiers() == 9 || m.getModifiers() == 0) {
			sbrmtd.append("+ ");

			sbrmtd.append(m.getName());
			sbrmtd.append("(");
			sbrmtd.append(s);
			sbrmtd.append(")");
			sbrmtd.append(" : ");
			sbrmtd.append(m.getType());
			fileDetails.methodsDec.add(sbrmtd.toString());
		}

		if (m.getParameters().size() > 0)
			populateUsesMtdDecln(m.getParameters(), fileDetails);
		if (m.getBody() != null)
			populateUsesMtdBody(m.getBody(), fileDetails);

	}
	
	private void populateUsesMtdBody(BlockStmt body, FileDetails fileDetails) {
		for (Node nblock : body.getChildrenNodes()) {

			if (nblock instanceof ExpressionStmt) {
				ExpressionStmt exprStmt = (ExpressionStmt) nblock;
				List<Node> nodeExpr = exprStmt.getChildrenNodes();
				if (nodeExpr.size() > 0 && nodeExpr.get(0) instanceof VariableDeclarationExpr) {

					VariableDeclarationExpr vd = (VariableDeclarationExpr) nodeExpr.get(0);

					if (vd.getType().getChildrenNodes().size() > 0) {

						ClassOrInterfaceType cit = (ClassOrInterfaceType) vd.getType().getChildrenNodes().get(0);

						fileDetails.interfaceclassesUsed_Methods
								.add(cit.getTypeArgs() != null && !cit.getTypeArgs().isEmpty() ? cit.getTypeArgs().get(0).toString() : cit.toString());
						if ((cit.getTypeArgs() != null && !cit.getTypeArgs().isEmpty())
								&& (!(classesinterpreted.contains(cit.getTypeArgs().get(0).toString())))) {
							fileDetails.interfaceclassesUsed_Methods.add(cit.getTypeArgs().get(0).toString());
						}
						if ((cit.getTypeArgs() == null && !cit.getTypeArgs().isEmpty()) && (!(classesinterpreted.contains(cit.toString())))) {
							fileDetails.interfaceclassesUsed_Methods.add(cit.toString());

						}

					}
				}

			}
		}
	}
	
void populateUsesMtdDecln(List<Parameter> plist, FileDetails fileDetails) {
		

		for (Parameter p : plist) {

			if (p.getType() instanceof ReferenceType) {
				if ((p.getType().getChildrenNodes().get(0).getChildrenNodes().size() == 0)
						&& (!(classesinterpreted.contains(p.getType().getChildrenNodes().get(0).toString()))))
					fileDetails.interfaceclassesUsed_Methods.add(p.getType().getChildrenNodes().get(0).toString());

				if (!(p.getType().getChildrenNodes().get(0).getChildrenNodes().size() == 0)) {

					if (!classesinterpreted
							.contains(p.getType().getChildrenNodes().get(0).getChildrenNodes().get(0).toString()))
						fileDetails.interfaceclassesUsed_Methods
								.add(p.getType().getChildrenNodes().get(0).getChildrenNodes().get(0).toString());
				}

			}
			if (p.getType() instanceof ClassOrInterfaceType) {

				if (!classesinterpreted.contains(p.getType().getChildrenNodes().get(0).toString()))
					fileDetails.interfaceclassesUsed_Methods.add(p.getType().getChildrenNodes().get(0).toString());
			}
		}
	}

void populateAtt(FieldDeclaration fd, FileDetails fileDetails) {
	int mod = fd.getModifiers();

	if ((mod == 1) || (mod == 2) || ((mod == 4))) {

		String modifier_ ;
		if(mod==1)
		{
			modifier_="+ ";
		}
		else
		{
			modifier_="- ";
		}
		

		VariableDeclarator vd = (VariableDeclarator) (fd.getVariables()).get(0);
		String vdid_str = vd.getId().toString();

		List<Node> node = fd.getChildrenNodes();

		if (fd.getType() instanceof ReferenceType) {
		
			String cassocn = null;
			ReferenceType rf = (ReferenceType) node.get(0);

			String str = fd.getType().toString();
			StringBuilder sbr = new StringBuilder();

			if (str.contains("[")) {
				cassocn = node.get(0).getChildrenNodes().get(0).toString();

				if (!classes_interfaceinterpreted.contains(cassocn)) {
					fileDetails.att.add(modifier_ + vdid_str + ":" + fd.getType());
				} else
					pAssocnC(cassocn, fileDetails);
			}

			else if (str.contains("<")) {
				cassocn = node.get(0).getChildrenNodes().get(0).getChildrenNodes().get(0).toString();
				if (!classes_interfaceinterpreted.contains(cassocn)) {
					fileDetails.att.add(modifier_ + vdid_str + ":" + fd.getType());
				} else {
					pAssocnC(cassocn, fileDetails);

				}

			} else {
				cassocn = str;
				if (!classes_interfaceinterpreted.contains(cassocn)) {
					fileDetails.att.add(modifier_ + vdid_str + ":" + fd.getType());
				} else {
					pAssocn(rf.toString(), fileDetails);
				}
			}

		}
		if (fd.getType() instanceof PrimitiveType) {
			fileDetails.att.add(modifier_ + vdid_str + ":" + fd.getType());

		}
	}
}

private void pAssocnC(String c_assoc, FileDetails fileDetails) {
	System.out.println("inside Association");
	String fname = fileDetails.name;
	StringBuilder s = new StringBuilder();
	s.append(fname);
	s.append(word_ztoM);
	s.append(c_assoc);

	if (!collection_association.contains(s.toString()))
		collection_association.add(s.toString());

}

private void pAssocn(String c_assoc, FileDetails fileDetails) {
	String fname = fileDetails.name;
	StringBuilder sbr = new StringBuilder();

	sbr.append(fname);
	sbr.append(ztoO);
	sbr.append(c_assoc);
	if (!ass.contains(sbr.toString()))
		ass.add(sbr.toString());

}

private void populateConstructors(ConstructorDeclaration n, FileDetails fileDetails) {

	System.out.println("inside constructor");
	if (n.getModifiers() == 1) {
		StringBuilder sbldr = new StringBuilder();

		List<Parameter> plist = n.getParameters();

		StringBuilder sbr = new StringBuilder();

		boolean moreParams = false;

		for (Parameter p : plist) {

			if (moreParams) {
				sbr.append(",");
			}
			moreParams = true;

			sbr.append(p.getId().getName());
			sbr.append(":");
			sbr.append(p.getType());

		}

		StringBuilder sbrmtd = new StringBuilder();

		if (n.getModifiers() == 1 || n.getModifiers() == 9) {
			sbrmtd.append("+ ");

			sbrmtd.append(n.getName());
			sbrmtd.append("(");
			sbrmtd.append(sbr);
			sbrmtd.append(")");
			fileDetails.constDeclared.add(sbrmtd.toString());
		}
		if (n.getParameters().size() > 0)
			populateUsesMtdDecln(n.getParameters(), fileDetails);
		if (n.getBlock() != null)
			populateUsesMtdBody(n.getBlock(), fileDetails);

	}
}


}
