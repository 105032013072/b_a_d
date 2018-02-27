package com.bosssoft.platform.apidocs.parser.entity;

import java.io.File;
import java.util.List;



import com.bosssoft.platform.apidocs.ParseUtils;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.parser.mate.EntityNode;
import com.bosssoft.platform.apidocs.parser.mate.MapperNode;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;

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
		}
    	
    	return entityNode;
    }

	private void parseEntityClass(ClassOrInterfaceDeclaration cid) {
		//解析@tabel
		AnnotationExpr annotationExpr= cid.getAnnotationByName("Table");
		List<MemberValuePair> list=annotationExpr.getChildNodesByType(MemberValuePair.class);
		for (MemberValuePair memberValuePair : list) {
			
		}
		
		System.out.println(annotationExpr.getNameAsString());
	}
}
