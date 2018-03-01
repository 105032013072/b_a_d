package com.bosssoft.platform.apidocs.parser.entity;

import java.io.File;
import java.util.List;


import com.bosssoft.platform.apidocs.ParseUtils;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.parser.mate.AttributeNode;
import com.bosssoft.platform.apidocs.parser.mate.EntityNode;
import com.bosssoft.platform.apidocs.parser.mate.MapperNode;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.javadoc.Javadoc;

public class EntityParser {
	private CompilationUnit compilationUnit;
    private EntityNode entityNode;
    private File javaFile;
    
    public EntityNode parse(File javaFile){
    	entityNode=new EntityNode();
    	this.javaFile=javaFile;
    	this.compilationUnit=ParseUtils.compilationUnit(javaFile);
    	
    	String entityName=Utils.getJavaFileName(javaFile);
    	ClassOrInterfaceDeclaration cid = null;
		List<ClassOrInterfaceDeclaration> list = ParseUtils.compilationUnit(javaFile).getChildNodesByType(ClassOrInterfaceDeclaration.class);
		for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : list) {
			if (entityName.endsWith(classOrInterfaceDeclaration.getNameAsString())) {
				cid = classOrInterfaceDeclaration; 
				break;
			}
		}
		
		if(cid!=null){
			parseEntityClass(cid);
			parseField(cid);
		}
    	
    	return entityNode;
    }

    //解析类的字段
	private void parseField(ClassOrInterfaceDeclaration cid) {
		List<FieldDeclaration> fieldDeclarationList = cid.getChildNodesByType(FieldDeclaration.class);
		for (FieldDeclaration fieldDeclaration : fieldDeclarationList) {
			AttributeNode attributeNode=new AttributeNode();
			
			//字段名和类型
			VariableDeclarator variable=fieldDeclaration.getVariable(0);
			attributeNode.setAttributeName(variable.getNameAsString());
			attributeNode.setAttributeType(variable.getType().toString());
			
			Javadoc fielddoc=fieldDeclaration.getJavadoc();
			if(fielddoc!=null){
				attributeNode.setDescription(fielddoc.getDescription().toText());
			}else{
				if(fieldDeclaration.getComment()!=null){
					attributeNode.setDescription(fieldDeclaration.getComment().getContent());
				}
			}
			
			//注解
			NodeList<AnnotationExpr> annotationList= fieldDeclaration.getAnnotations();
			for (AnnotationExpr annotationExpr : annotationList) {
				String key=annotationExpr.getNameAsString();
				if("Id".equals(key)){
					attributeNode.setIsPrimaryKey(true);
				}else if("Column".equals(key)){
					List<MemberValuePair> list=annotationExpr.getChildNodesByType(MemberValuePair.class);
					for (MemberValuePair memberValuePair : list) {
						if("name".equals(memberValuePair.getNameAsString())){
							attributeNode.setColumnName(memberValuePair.getValue().toString().replace("\"", ""));
						}
					}
				}
			}
			
			if(attributeNode.getIsPrimaryKey()){
				attributeNode.setDescription("主键，"+attributeNode.getDescription());
			}
			entityNode.addAttributeNode(attributeNode);
		}
	}

	private void parseEntityClass(ClassOrInterfaceDeclaration cid) {
		entityNode.setClassName(cid.getNameAsString());
		
		//解析@tabel
		AnnotationExpr annotationExpr= cid.getAnnotationByName("Table");
		List<MemberValuePair> list=annotationExpr.getChildNodesByType(MemberValuePair.class);
		for (MemberValuePair memberValuePair : list) {
			String key=memberValuePair.getNameAsString();
			if("name".equals(key)){
				entityNode.setTableName(memberValuePair.getValue().toString().replace("\"", ""));
			}
		}
		
		//类的注释
		Javadoc javadoc=cid.getJavadoc();
		if(javadoc!=null){
			String description = javadoc.getDescription().toText();
			entityNode.setDescription(description);
	        entityNode.setAuthor(ParseUtils.parserClassAuthor(javadoc));
	        if(entityNode.getDescription()==null){
	        	entityNode.setDescription(entityNode.getClassName());
	        }
		}
		
		if(entityNode.getDescription()==null) entityNode.setDescription(cid.getNameAsString());
	}
	
	
	
}
