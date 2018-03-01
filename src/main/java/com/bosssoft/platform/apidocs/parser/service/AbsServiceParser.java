package com.bosssoft.platform.apidocs.parser.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosssoft.platform.apidocs.ParseUtils;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.parser.mate.Explain;
import com.bosssoft.platform.apidocs.parser.mate.InterfaceNode;
import com.bosssoft.platform.apidocs.parser.mate.MapperNode;
import com.bosssoft.platform.apidocs.parser.mate.ParamNode;
import com.bosssoft.platform.apidocs.parser.mate.ServiceNode;
import com.bosssoft.platform.common.utils.StringUtils;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

public abstract  class AbsServiceParser {
	private CompilationUnit compilationUnit;
    private ServiceNode serviceNode;
    private File javaFile;
    private Map<String,MapperNode> mapperNodeMap=new HashMap<>();
    
    public ServiceNode parse(File javaFile,Map<String,MapperNode> mapperNodeMap){
    	this.mapperNodeMap=mapperNodeMap;
    	this.javaFile=javaFile;
    	this.compilationUnit= ParseUtils.compilationUnit(javaFile);
    	this.serviceNode=new ServiceNode();
        
    	String serviceName=Utils.getJavaFileName(javaFile);
    	ClassOrInterfaceDeclaration declaration=compilationUnit.getClassByName(serviceName);
    	
    	if(declaration!=null){
    		//获取实现的接口
    		ClassOrInterfaceDeclaration implementDeclaration=ParseUtils.parserImplementInterface(javaFile, declaration);
    	
    		//解析类的注释
    		parseClassDocs(declaration,implementDeclaration);
 
    		//解析方法的注释 
    		parseMethodDocs(declaration,implementDeclaration);
    		
    		//解析该service调用的mapper
    		parseAutowireMapper(declaration);
    	}
    	
    	
    	
    	return serviceNode;
    }

	

	private void parseAutowireMapper(ClassOrInterfaceDeclaration declaration) {
		List<FieldDeclaration> fieldList=declaration.getFields();
		for (FieldDeclaration fieldDeclaration : fieldList) {
			VariableDeclarator variable=fieldDeclaration.getVariable(0);
			String fieldType=variable.getType().asString();
			if(IsMapperExist(fieldType)) {
				Explain explain=new Explain();
				explain.setType(fieldType);
				explain.setDescription(mapperNodeMap.get(fieldType).getDescription());
				serviceNode.addAutowiredMapper(explain);
			}
				
		}
	}

	private void parseMethodDocs(ClassOrInterfaceDeclaration declaration, ClassOrInterfaceDeclaration implementDeclaration) {
		List<MethodDeclaration> implementMethodDeclarationList=getMethodDeclaration(implementDeclaration);
	    for (MethodDeclaration m : implementMethodDeclarationList) {
	    	InterfaceNode  interfaceNode=new InterfaceNode();
	    	
	    	//方法名
	    	interfaceNode.setMethodName(ParseUtils.parserMethodName(m));
	    	
	    	//参数名以及类型
	    	NodeList<Parameter> paramList=m.getParameters();
	    	for (Parameter parameter : paramList) {
				interfaceNode.addParamNode(ParseUtils.constructParamNode(parameter));
			}
	    	
	    	//返回类型
	    	interfaceNode.setReturnNode(ParseUtils.constructReturnNode(javaFile,m));
	    	
	    	//抛出的异常类型
	    	NodeList<ReferenceType> exceptionList=m.getThrownExceptions();
	    	for (ReferenceType referenceType : exceptionList) {
				Explain explain=new Explain();
			    explain.setType(referenceType.asString());
			    interfaceNode.addThrowsNode(explain);
			}
	    	
	    	//方法注释
	    	Javadoc javadoc=getMethodDoc(m,declaration);
	    	if(javadoc!=null){
	    		ParseUtils.parserMethodNotes(interfaceNode, javadoc);
	    	}
	    	if(StringUtils.isNullOrBlank(interfaceNode.getDescription())){
	    		interfaceNode.setDescription(interfaceNode.getMethodName());
	    	}
	    	serviceNode.addInterfaceNodes(interfaceNode);
		}
	    
	}

	private Javadoc getMethodDoc(MethodDeclaration m, ClassOrInterfaceDeclaration declaration) {
		Javadoc javadoc=null;
		javadoc=m.getJavadoc();
		//接口方法若是没有注释，从方法实现中获取
		if(javadoc==null){
			String methodName=m.getNameAsString();
			List<MethodDeclaration> list=declaration.getMethodsByName(methodName);
			//通过参数类型确定方法（重载）
			String [] paramType=getparamType(m.getParameters());
			for (MethodDeclaration methodDeclaration : list) {
				String [] array=getparamType(methodDeclaration.getParameters());
				if(Arrays.equals(paramType, array)){
					methodDeclaration.getJavadoc();
					break;
				}
			}
		}
		return javadoc;
	}

	private String[] getparamType(NodeList<Parameter> parameters) {
		String [] array=new String[parameters.size()];
		for(int i=0;i<parameters.size();i++){
			Parameter p=parameters.get(i);
			array[i]=p.getType().asString();
		}
		return array;
	}

	private List<MethodDeclaration> getMethodDeclaration(ClassOrInterfaceDeclaration c) {
		
		List<MethodDeclaration> result=new ArrayList<>();
		List<MethodDeclaration> list=c.getChildNodesByType(MethodDeclaration.class);
		
		for (MethodDeclaration m : list) {
			if(m.getModifiers().contains(Modifier.PUBLIC)){
				result.add(m);
			}
		}
		return result;
	}

	private void parseClassDocs(ClassOrInterfaceDeclaration declaration,ClassOrInterfaceDeclaration implementDeclaration) {
		Javadoc javadoc = null;
		if (implementDeclaration != null && implementDeclaration.getJavadoc() != null) {
			javadoc = implementDeclaration.getJavadoc();
		} else {
			javadoc = declaration.getJavadoc();
		}

		if (javadoc != null) {
			String description = javadoc.getDescription().toText();
			serviceNode.setDescription(description);

		/*	List<JavadocBlockTag> blockTags = javadoc.getBlockTags();
			if (blockTags != null) {
				for (JavadocBlockTag blockTag : blockTags) {
					if ("author".equalsIgnoreCase(blockTag.getTagName())) {
						serviceNode.setAuthor(blockTag.getContent().toText());
					}
				}
			}*/
			serviceNode.setAuthor(ParseUtils.parserClassAuthor(javadoc));
		}

		// 若类没有注释，则description为类名
		if (serviceNode.getDescription() == null) {
			serviceNode.setDescription(declaration.getNameAsString());
		}
		
		serviceNode.setServiceName(implementDeclaration.getNameAsString());

	}
	
	private  Boolean IsMapperExist(String mapperName){
    	if(mapperNodeMap.get(mapperName)!=null) return true;
    	else return false;
    }

}
