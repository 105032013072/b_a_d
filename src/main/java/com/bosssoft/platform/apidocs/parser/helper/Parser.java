package com.bosssoft.platform.apidocs.parser.helper;

import java.io.File;
import java.util.List;

import com.bosssoft.platform.apidocs.ParseUtils;
import com.bosssoft.platform.apidocs.parser.mate.Explain;
import com.bosssoft.platform.apidocs.parser.mate.ParamNode;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

public class Parser {


	/**
	 * 获取类的实现接口
	 * @param javaFile
	 * @param cod
	 * @return
	 */
  public static ClassOrInterfaceDeclaration parserImplementInterface(File javaFile,ClassOrInterfaceDeclaration cod){
	  String  implementClassName=cod.getImplementedTypes(0).getNameAsString();
		File modelJavaFile=ParseUtils.searchJavaFile(javaFile, implementClassName);
		ClassOrInterfaceDeclaration cid = null;
		List<ClassOrInterfaceDeclaration> list = ParseUtils.compilationUnit(modelJavaFile).getChildNodesByType(ClassOrInterfaceDeclaration.class);
		for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : list) {
			if (implementClassName.endsWith(classOrInterfaceDeclaration.getNameAsString())) {
				cid = classOrInterfaceDeclaration; 
				break; 
			}
		}
		return cid;
  }
  
  /**
   * 获取类的作者
   * @param javadoc
   * @return
   */
  public static String parserClassAuthor(Javadoc javadoc){
	  List<JavadocBlockTag> blockTags = javadoc.getBlockTags();
		if (blockTags != null) {
			for (JavadocBlockTag blockTag : blockTags) {
				if ("author".equalsIgnoreCase(blockTag.getTagName())) {
					return blockTag.getContent().toText();
				}
			}
		}
		return "";
  }
  
  /**
   * 获取方法名
   * @param m
   * @return
   */
  public static String parserMethodName(MethodDeclaration m){
	  return m.getNameAsString();
  }
  
  /**
   * 构造方法参数节点
   * @param parameter
   * @return
   */
  public static ParamNode constructParamNode(Parameter parameter){
	    String paramName=parameter.getName().asString();
		ParamNode paramNode=new ParamNode();
		paramNode.setName(paramName);
		paramNode.setType(parameter.getType().asString());
		return paramNode;
  }
  
  public static Explain constructReturnNode(MethodDeclaration m){
	  Explain e=new Explain();
	  e.setType(m.getType().asString());
	  return e;
  }
  
  
  
}
