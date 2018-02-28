package com.bosssoft;
import java.io.File;
import java.net.StandardSocketOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.bosssoft.platform.apidocs.ParseUtils;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class MyTest {
   
	@Test
	public void test1(){
		File javaFile=new File("D:/github/bosssoft-apidocs/src/test/java/com/bosssoft/UserService.java");
		String serviceName=Utils.getJavaFileName(javaFile);
    	ClassOrInterfaceDeclaration declaration=ParseUtils.compilationUnit(javaFile).getClassByName(serviceName);
	
		List<MethodDeclaration> implementMethodDeclarationList=getMethodDeclaration(declaration);
		for (MethodDeclaration methodDeclaration : implementMethodDeclarationList) {
	              String type=getBasicType(methodDeclaration.getType());	
	              System.out.println(type);
	              
	              File f=null;
	              try{
	            	   f=  ParseUtils.searchJavaFile(javaFile, type);
	              }catch (Exception e) {
				    System.out.println(type+" can not to file java file");
				  }
	             if(f!=null){
	            	 ResponseNode responseNode = new ResponseNode();
		             ParseUtils.parseResponseNode(f, responseNode);
		             System.out.println(responseNode.toJsonApi()); 
	             }
	           
	             
	              
		}
			
			
	}
	
    private String getBasicType(Type type) {
    	String typeStr=type.asString();
    	if(ParseUtils.TYPE_MODEL.equals(typeStr)||"Void".equalsIgnoreCase(typeStr)){
    		return type.asString();
    	}else if(type instanceof ClassOrInterfaceType) {
    		ClassOrInterfaceType cot=(ClassOrInterfaceType) type;
    		String name=cot.getNameAsString();
    		if(name.equals("Map")||name.equals("HashMap")||name.equals("TreeMap")||name.equals("Hashtable")){
    			NodeList<Type> list=cot.getTypeArguments();
    			if(list.size()>0){
    				return getBasicType(list.get(1));
    			}
    		}else if(name.equals("List")||name.equals("LinkedList")||name.equals("ArrayList")||name.equals("Vector")||name.equals("Set")){
    			NodeList<Type> list=cot.getTypeArguments();
    			if(list.size()>0){
    				return getBasicType(list.get(0));
    			}
    		}
    	}
   
    	return type.asString();
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
}
