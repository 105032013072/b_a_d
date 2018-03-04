package com.bosssoft.platform.apidocs.parser.mapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosssoft.platform.apidocs.DocContext;
import com.bosssoft.platform.apidocs.ParseUtils;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.parser.mate.EntityNode;
import com.bosssoft.platform.apidocs.parser.mate.Explain;
import com.bosssoft.platform.apidocs.parser.mate.InterfaceNode;
import com.bosssoft.platform.apidocs.parser.mate.MapperNode;
import com.bosssoft.platform.apidocs.parser.mate.ServiceNode;
import com.bosssoft.platform.common.utils.StringUtils;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.javadoc.Javadoc;

public abstract class AbsMapperParser {
	private CompilationUnit compilationUnit;
    private MapperNode mapperNode;
    private File javaFile;
    private  Map<String,EntityNode> entityNodeMap=new HashMap<>();
    
    public MapperNode parse(File javaFile,Map<String,EntityNode> entityNodeMap){
    	this.entityNodeMap=entityNodeMap;
    	mapperNode=new MapperNode();
    	this.javaFile=javaFile;
    	this.compilationUnit= ParseUtils.compilationUnit(javaFile);
    	
    	 String mapperName = Utils.getJavaFileName(javaFile);
    	 ClassOrInterfaceDeclaration declaration =compilationUnit.getInterfaceByName(mapperName);
    	if(declaration!=null){
    		//解析类的注释
    		parseClassDocs(declaration);
 
    		//解析方法的注释 
    		parseMethodDocs(declaration);
    	}
    	return mapperNode;
    }


	private void parseMethodDocs(ClassOrInterfaceDeclaration declaration) {

		List<MethodDeclaration> implementMethodDeclarationList=getMethodDeclaration(declaration);
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
	    	Javadoc javadoc=m.getJavadoc();
	    	if(javadoc!=null){
	    		ParseUtils.parserMethodNotes(interfaceNode, javadoc);
	    	}
	    	if(StringUtils.isNullOrBlank(interfaceNode.getDescription())){
	    		interfaceNode.setDescription(interfaceNode.getMethodName());
	    	}
	    	mapperNode.addInterfaceNodes(interfaceNode);
		}
	    
	
		
	}


	private List<MethodDeclaration> getMethodDeclaration(ClassOrInterfaceDeclaration declaration) {
		List<MethodDeclaration> result=new ArrayList<>();
		List<MethodDeclaration> list=declaration.getChildNodesByType(MethodDeclaration.class);
		
		for (MethodDeclaration m : list) {
			if(m.getModifiers().contains(Modifier.PUBLIC)){
				result.add(m);
			}
		}
		return result;
	}


	private void parseClassDocs(ClassOrInterfaceDeclaration declaration) {
		
		Javadoc javadoc = declaration.getJavadoc();
		
		ClassOrInterfaceType implement= declaration.getExtendedTypes(0);
		String type= implement.getTypeArguments().get(0).asString();
		if(IsEntityExist(type)){
			Explain explain=new Explain();
			explain.setType(type);
			EntityNode entityNode=entityNodeMap.get(type);
			explain.setDescription(entityNode.getDescription());
			explain.setType(entityNode.getHtmlPath());
			 mapperNode.setRelationEntity(explain);
		}
		
		if (javadoc != null) {
			String description = javadoc.getDescription().toText();
			mapperNode.setDescription(description);
			mapperNode.setAuthor(ParseUtils.parserClassAuthor(javadoc));
		}

		// 若类没有注释，则description为类名
		if (mapperNode.getDescription() == null) {
			mapperNode.setDescription(declaration.getNameAsString());
		}

		mapperNode.setClassName(declaration.getNameAsString());

	}
	
	public Boolean IsEntityExist(String entityName){
    	if(entityNodeMap.get(entityName)!=null) return true;
    	else return false;
    }
}
