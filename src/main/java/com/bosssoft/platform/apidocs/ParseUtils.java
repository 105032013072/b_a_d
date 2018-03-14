package com.bosssoft.platform.apidocs;

import com.bosssoft.platform.apidocs.parser.mate.ClassNode;
import com.bosssoft.platform.apidocs.parser.mate.Explain;
import com.bosssoft.platform.apidocs.parser.mate.FieldNode;
import com.bosssoft.platform.apidocs.parser.mate.InterfaceNode;
import com.bosssoft.platform.apidocs.parser.mate.MockNode;
import com.bosssoft.platform.apidocs.parser.mate.ParamNode;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;
import com.bosssoft.platform.apidocs.parser.mate.ReturnNode;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.*;

/**
 * some util methods during parse
 *
 * @author yeguozhong yedaxia.github.com
 */
public class ParseUtils {

    /**
     * means a model class type
     */
    public static final String TYPE_MODEL = "unkown";

    /**
     * search File of className in the java file
     *
     * @param inJavaFile
     * @param className
     * @return
     */
    public static File searchJavaFile(File inJavaFile, String className){
       File file = searchJavaFileInner(inJavaFile, className);
       if(file == null){
    	   LogUtils.error("cannot find java file , in java file : " + inJavaFile.getAbsolutePath() + ", className : " +className);
           //throw new RuntimeException();
       }
       return file;
    }

    private static File searchJavaFileInner(File inJavaFile, String className){
        CompilationUnit compilationUnit = compilationUnit(inJavaFile);

        String[] cPaths;

       /*Optional<ImportDeclaration> idOp = compilationUnit.getImports()
                .stream()
                .filter(im -> im.getNameAsString().endsWith(className))
                .findFirst();*/
        
        ImportDeclaration idop=getImportDeclaration(compilationUnit,className);

        //found in import
        if(idop!=null){
            cPaths = idop.getNameAsString().split("\\.");
            return backTraceJavaFileByName(cPaths);
        }

        //inner class
        if(getInnerClassNode(compilationUnit, className)!=null){
            return inJavaFile;
        }

        cPaths = className.split("\\.");

        //current directory
        if(cPaths.length == 1){
            final String finalclassName=className;
            File[] javaFiles = inJavaFile.getParentFile().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.equals(finalclassName + ".java");
                }
            });

            if(javaFiles != null && javaFiles.length == 1){
                return javaFiles[0];
            }

        }else{

            final String firstPath = cPaths[0];
            //same package inner class
            File[] javaFiles = inJavaFile.getParentFile().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    int i = name.lastIndexOf(".java");
                    if(i == -1){
                        return false;
                    }
                    return name.substring(0, i).equals(firstPath);
                }
            });

            if(javaFiles != null && javaFiles.length > 0){
                File javaFile = javaFiles[0];
                if(getInnerClassNode(compilationUnit(javaFile), className)!=null){
                    return javaFile;
                }
            }
        }

        //maybe a complete class name
        File javaFile = backTraceJavaFileByName(cPaths);
        if(javaFile != null){
            return javaFile;
        }

        //.* at import
        NodeList<ImportDeclaration> importDeclarations = compilationUnit.getImports();
        if(importDeclarations.isNonEmpty()){
            for(ImportDeclaration importDeclaration : importDeclarations){
                if(importDeclaration.toString().contains(".*")){
                    String packageName = importDeclaration.getNameAsString();
                    cPaths = (packageName + "." + className).split("\\.");
                    javaFile = backTraceJavaFileByName(cPaths);
                    if(javaFile != null){
                        break;
                    }
                }
            }
        }

        return javaFile;
    }

    private static ImportDeclaration getImportDeclaration(CompilationUnit compilationUnit, String className) {
 
    	
    	NodeList<ImportDeclaration> list=compilationUnit.getImports();
    	NodeList<ImportDeclaration> result=new NodeList<>();
    	for (ImportDeclaration importDeclaration : list) {
			if(importDeclaration.getNameAsString().endsWith(className)){
				result.add(importDeclaration);
			}
		}
        
    	if(result.size()!=0) return result.get(0);
    	else return null;
	}

	/**
     * get inner class node
     *
     * @param compilationUnit
     * @param className
     * @return
     */
    private static TypeDeclaration getInnerClassNode(CompilationUnit compilationUnit , String className){
        /*return compilationUnit.getChildNodesByType(TypeDeclaration.class)
                .stream()
                .filter( c -> c instanceof ClassOrInterfaceDeclaration ||  c instanceof EnumDeclaration)
                .filter( c -> className.endsWith(c.getNameAsString()))
                .findFirst();*/
    	
    	List<TypeDeclaration> result=new ArrayList<>();
		List<TypeDeclaration> list = compilationUnit.getChildNodesByType(TypeDeclaration.class);
    	
		for (TypeDeclaration typeDeclaration : list) {
			if(typeDeclaration instanceof ClassOrInterfaceDeclaration|| typeDeclaration instanceof EnumDeclaration){
				if(className.endsWith(typeDeclaration.getNameAsString())) result.add(typeDeclaration);
			}
		}
		
		if(result.size()!=0) return result.get(0);
		else return null; 
    }

    private static File backTraceJavaFileByName(String[] cPaths){
        if(cPaths.length == 0){
            return null;
        }
        List<String> javaSrcPathList= DocContext.getJavaSrcPathList();
        for (String srcPath : javaSrcPathList) {
        	String javaFilePath = srcPath + Utils.joinArrayString(cPaths, "/") +".java";
        	 File javaFile = new File(javaFilePath);
        	 if(javaFile.exists() && javaFile.isFile()){
        		 return javaFile;
        	 }
		}
        return backTraceJavaFileByName(Arrays.copyOf(cPaths, cPaths.length - 1));
    }

    /**
     * get java file parser object
     *
     * @param javaFile
     * @return
     */
    public static CompilationUnit compilationUnit(File javaFile){
        try{
            return JavaParser.parse(javaFile);
        }catch (FileNotFoundException e){
            throw new RuntimeException("java file not exits , file path : " + javaFile.getAbsolutePath());
        }
    }

    /**
     * parse response of model java file
     *
     * @param modelJavaFile
     * @param responseNode
     */
    public static void parseResponseNode(File modelJavaFile, ClassNode responseNode){
        String resultClassName = responseNode.getClassName();

        /*ParseUtils.compilationUnit(modelJavaFile).
                getChildNodesByType(ClassOrInterfaceDeclaration.class).
                stream().filter(f -> resultClassName.endsWith(f.getNameAsString())).findFirst().ifPresent(cl -> {

                    NodeList<ClassOrInterfaceType> exClassTypeList =  cl.getExtendedTypes();
                    if(!exClassTypeList.isEmpty()){
                        String extendClassName = exClassTypeList.get(0).getNameAsString();
                        responseNode.setClassName(extendClassName);
                        parseResponseNode(ParseUtils.searchJavaFile(modelJavaFile, extendClassName), responseNode);
                    }

                    //类的字段
                    cl.getChildNodesByType(FieldDeclaration.class)
                    .stream().filter(fd -> !fd.getModifiers().contains(Modifier.STATIC))
                    .forEach(fd -> {

                        //内部类字段也会读取到，这里特殊处理
                        ClassOrInterfaceDeclaration cClDeclaration = (ClassOrInterfaceDeclaration)fd.getParentNode().get();
                        if(!resultClassName.endsWith(cClDeclaration.getNameAsString())){
                            return;
                        }

                        fd.getVariables().forEach(v -> {
                            FieldNode fieldNode = new FieldNode();
                            responseNode.addChildNode(fieldNode);
                            fd.getComment().ifPresent(c -> fieldNode.setDescription(Utils.cleanCommentContent(c.getContent())));
                            if(!Utils.isNotEmpty(fieldNode.getDescription())){
                                v.getComment().ifPresent(c -> fieldNode.setDescription(Utils.cleanCommentContent(c.getContent())));
                            }
                            fd.getAnnotationByName("RapMock").ifPresent(an -> {
                                if(an instanceof NormalAnnotationExpr){
                                    NormalAnnotationExpr normalAnExpr = (NormalAnnotationExpr)an;
                                    MockNode mockNode = new MockNode();
                                    for(MemberValuePair mvPair : normalAnExpr.getPairs()){
                                        String name = mvPair.getName().asString();
                                        if("limit".equalsIgnoreCase(name)){
                                            mockNode.setLimit(Utils.removeQuotations(mvPair.getValue().toString()));
                                        }else if("value".equalsIgnoreCase(name)){
                                            mockNode.setValue(Utils.removeQuotations(mvPair.getValue().toString()));
                                        }
                                    }
                                    fieldNode.setMockNode(mockNode);
                                }else if(an instanceof SingleMemberAnnotationExpr){
                                    SingleMemberAnnotationExpr singleAnExpr = (SingleMemberAnnotationExpr)an;
                                    MockNode mockNode = new MockNode();
                                    mockNode.setValue(Utils.removeQuotations(singleAnExpr.getMemberValue().toString()));
                                    fieldNode.setMockNode(mockNode);
                                }
                            });
                            fieldNode.setName(v.getNameAsString());
                            Type elType = fd.getElementType();
                            String type = elType.asString();
                            if(elType.getParentNode().get() instanceof ArrayType){
                                parseChildResponseNode(fieldNode, modelJavaFile, type, Boolean.TRUE);
                            }else{
                                if(isCollectionType(type)){
                                    elType.getChildNodesByType(ClassOrInterfaceType.class)
                                            .stream()
                                            .findFirst()
                                            .ifPresent(t ->{
                                                String genericType = t.getNameAsString();
                                                parseChildResponseNode(fieldNode, modelJavaFile, genericType, Boolean.TRUE);
                                            });
                                }else{
                                    parseChildResponseNode(fieldNode, modelJavaFile, type, Boolean.FALSE);
                                }
                            }
                        });
                    });
        });
*/
        //jdk1.7
		ClassOrInterfaceDeclaration cid = null;
		List<ClassOrInterfaceDeclaration> list = ParseUtils.compilationUnit(modelJavaFile).getChildNodesByType(ClassOrInterfaceDeclaration.class);
		for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : list) {
			if (resultClassName.endsWith(classOrInterfaceDeclaration.getNameAsString())) {
				cid = classOrInterfaceDeclaration; 
				break;
			}
		}
        
		if (cid != null) {
			NodeList<ClassOrInterfaceType> exClassTypeList = cid.getExtendedTypes();
			if (!exClassTypeList.isEmpty()) {
				String extendClassName = exClassTypeList.get(0).getNameAsString();
				responseNode.setClassName(extendClassName);
				parseResponseNode(ParseUtils.searchJavaFile(modelJavaFile, extendClassName), responseNode);
			}

			// 类的字段
			List<FieldDeclaration> fieldDeclarationList = cid.getChildNodesByType(FieldDeclaration.class);
			for (FieldDeclaration fd : fieldDeclarationList) {
                if(!(fd.getModifiers().contains(Modifier.STATIC))){
                	//内部类字段也会读取到，这里特殊处理
                    ClassOrInterfaceDeclaration cClDeclaration = (ClassOrInterfaceDeclaration)fd.getParentNode();
                    if(!resultClassName.endsWith(cClDeclaration.getNameAsString())){
                    	continue;
                    }
                    
                    NodeList<VariableDeclarator> variableList=fd.getVariables();
                    for (VariableDeclarator v : variableList) {
               
                    	FieldNode fieldNode = new FieldNode();
                         responseNode.addChildNode(fieldNode);
                         /*if(fd.getComment()!=null){
                        	 fieldNode.setDescription(fd.getComment().getContent());
                         }
                         if(!Utils.isNotEmpty(fieldNode.getDescription())){
                              if(v.getComment()!=null) fieldNode.setDescription(Utils.cleanCommentContent(v.getComment().getContent()));
                         }*/
                         fieldNode.setDescription(ParseUtils.getFeildDoc(fd));
                         
                        // fd.getAnnotationByName("RapMock")
                        AnnotationExpr  an =fd.getAnnotationByName("RapMock");
                        if(an!=null){
                        	if(an instanceof NormalAnnotationExpr){
                        		NormalAnnotationExpr normalAnExpr = (NormalAnnotationExpr)an;
                                MockNode mockNode = new MockNode();
                                for(MemberValuePair mvPair : normalAnExpr.getPairs()){
                                    String name = mvPair.getName().asString();
                                    if("limit".equalsIgnoreCase(name)){
                                        mockNode.setLimit(Utils.removeQuotations(mvPair.getValue().toString()));
                                    }else if("value".equalsIgnoreCase(name)){
                                        mockNode.setValue(Utils.removeQuotations(mvPair.getValue().toString()));
                                    }
                                }
                                fieldNode.setMockNode(mockNode);
                        	}else if(an instanceof SingleMemberAnnotationExpr){
                        		SingleMemberAnnotationExpr singleAnExpr = (SingleMemberAnnotationExpr)an;
                                MockNode mockNode = new MockNode();
                                mockNode.setValue(Utils.removeQuotations(singleAnExpr.getMemberValue().toString()));
                                fieldNode.setMockNode(mockNode);
                        	}
                        }
                        fieldNode.setName(v.getNameAsString());
                        Type elType = fd.getElementType();
                        String type = elType.asString();
                        if(elType.getParentNode() instanceof ArrayType){
                        	parseChildResponseNode(fieldNode, modelJavaFile, type, Boolean.TRUE);
                        }else {
                        	 if(isCollectionType(type)){
                        		List<ClassOrInterfaceType> ciList= elType.getChildNodesByType(ClassOrInterfaceType.class);
                        		if(ciList.size()!=0){
                        			ClassOrInterfaceType classOrInterfaceType=ciList.get(0);
                        			String genericType = classOrInterfaceType.getNameAsString();
                                    parseChildResponseNode(fieldNode, modelJavaFile, genericType, Boolean.TRUE);
                        		}
                        	 }else if(isMapType(type)){
                        		 fieldNode.setType(type);
                        	 }else{
                        		 parseChildResponseNode(fieldNode, modelJavaFile, type, Boolean.FALSE); 
                        	 }
                        }
                        
					}
                    
                }
			}

		}
      
        //恢复原来的名称
        responseNode.setClassName(resultClassName);
    }

    private static void parseChildResponseNode(FieldNode parentNode, File inJavaFile, String type, Boolean isList){
        String unifyType = unifyType(type);
        if(TYPE_MODEL.equals(unifyType)){
            File childJavaFile = searchJavaFile(inJavaFile, type);
            /*Optional<EnumDeclaration> ed = compilationUnit(childJavaFile)
                    .getChildNodesByType(EnumDeclaration.class)
                    .stream()
                    .filter( em -> type.endsWith(em.getNameAsString()))
                    .findFirst();*/
            if(childJavaFile==null){
            	 parentNode.setType(isList ? unifyType + "[]" : unifyType);
            	 return ;
            }
            
            EnumDeclaration ed=null;
            List<EnumDeclaration> list= compilationUnit(childJavaFile).getChildNodesByType(EnumDeclaration.class);
            for (EnumDeclaration enumDeclaration : list) {
				if(type.endsWith(enumDeclaration.getNameAsString())){
					ed=enumDeclaration;
					break;
				}
			}
            
            
            
            if(ed!=null){
                parentNode.setType("string");
                List<EnumConstantDeclaration> constants = ed.getChildNodesByType(EnumConstantDeclaration.class);
                StringBuilder sb = new StringBuilder(parentNode.getDescription() == null ? "" : parentNode.getDescription());
                sb.append(" [");
                for(int i = 0 , size = constants.size(); i != size ; i++){
                    sb.append(constants.get(i).getNameAsString());
                    if(i != size -1){
                        sb.append(",");
                    }
                }
                sb.append("]");
                parentNode.setDescription(sb.toString());
            }else{
                ResponseNode childResponse = new ResponseNode();
                parentNode.setChildResponseNode(childResponse);
                childResponse.setList(isList);
                parentNode.setType(isList ? type + "[]" : type);
                childResponse.setClassName(type);
                parseResponseNode(searchJavaFile(inJavaFile, type), childResponse);
            }
        } else {
            parentNode.setType(isList ? unifyType + "[]" : unifyType);
        }
    }

    /**
     * is model type or not
     * @param className
     * @return
     */
    public static boolean isModelType(String className){
        return TYPE_MODEL.equals(unifyType(className));
    }

    /**
     * unify the type show in docs
     *
     * @param className
     * @return
     */
    public static String unifyType(String className){
        String[] cPaths = className.split("\\.");
        String rawType = cPaths[cPaths.length - 1];
        if("byte".equalsIgnoreCase(rawType)){
            return "byte";
        } else if("short".equalsIgnoreCase(rawType)){
            return "short";
        } else if("int".equalsIgnoreCase(rawType)
                || "Integer".equalsIgnoreCase(rawType)
                || "BigInteger".equalsIgnoreCase(rawType)){
            return "int";
        } else if("long".equalsIgnoreCase(rawType)){
            return "long";
        } else if("float".equalsIgnoreCase(rawType)){
            return "float";
        } else if("double".equalsIgnoreCase(rawType)
                ||"BigDecimal".equalsIgnoreCase(rawType)){
            return "double";
        } else if("boolean".equalsIgnoreCase(rawType)){
            return "boolean";
        } else if("char".equalsIgnoreCase(rawType)
                || "Character".equalsIgnoreCase(rawType)){
            return "char";
        }else if("String".equalsIgnoreCase(rawType)){
            return "string";
        } else if("date".equalsIgnoreCase(rawType)
                || "ZonedDateTime".equalsIgnoreCase(rawType)){
            return "date";
        } else if("file".equalsIgnoreCase(rawType)){
            return "file";
        } else{
            return TYPE_MODEL;
        }
    }

    /**
     *  is implements from Collection or not
     *
     * @param className
     * @return
     */
    public static boolean isCollectionType(String className){
        String[] cPaths = className.split("\\.");
        String genericType = cPaths[cPaths.length - 1];
        int genericLeftIndex = genericType.indexOf("<");
        String rawType = genericLeftIndex != -1 ? genericType.substring(0, genericLeftIndex) : genericType;
        String collectionClassName = "java.util."+rawType;
        try{
            Class collectionClass = Class.forName(collectionClassName);
            return Collection.class.isAssignableFrom(collectionClass);
        }catch (ClassNotFoundException e){
            return false;
        }
    }
    
    public static boolean isMapType(String className){
        String[] cPaths = className.split("\\.");
        String genericType = cPaths[cPaths.length - 1];
        int genericLeftIndex = genericType.indexOf("<");
        String rawType = genericLeftIndex != -1 ? genericType.substring(0, genericLeftIndex) : genericType;
        String collectionClassName = "java.util."+rawType;
        try{
            Class collectionClass = Class.forName(collectionClassName);
            return Map.class.isAssignableFrom(collectionClass);
        }catch (ClassNotFoundException e){
            return false;
        }
    }
    
    
    /**
	 * 获取类的实现接口
	 * @param javaFile
	 * @param cod
	 * @return
	 */
  public static ClassOrInterfaceDeclaration parserImplementInterface(File javaFile,ClassOrInterfaceDeclaration cod){
	  ClassOrInterfaceDeclaration cid = null;
	  String  implementClassName=cod.getImplementedTypes(0).getNameAsString();
		File modelJavaFile=ParseUtils.searchJavaFile(javaFile, implementClassName);
		if(modelJavaFile==null)  return cid;
		List<ClassOrInterfaceDeclaration> list = ParseUtils.compilationUnit(modelJavaFile).getChildNodesByType(ClassOrInterfaceDeclaration.class);
		for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : list) {
			if (implementClassName.endsWith(classOrInterfaceDeclaration.getNameAsString())) {
				cid = classOrInterfaceDeclaration; 
				break; 
			}
		}
		return cid;
  }
  
  
 /* public static void parserExtendsClass(File javaFile,ClassOrInterfaceDeclaration cod){
	 Javadoc javadoc=cod.getJavadoc();
	  NodeList<ClassOrInterfaceType> list= cod.getExtendedTypes();
	  if(list.size()>0){
		  String name=list.get(0).getNameAsString();
		//  File modelJavaFile=ParseUtils.searchJavaFile(javaFile, name);
		 
		 //获取类的所有导入
		  CompilationUnit compilationUnit= ParseUtils.compilationUnit(javaFile);
		  getImportDeclaration(compilationUnit, name);
	  }
	  
  }*/
  
  
  
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
  
  public static ReturnNode constructReturnNode(File javaFile,MethodDeclaration methodDeclaration){
		ReturnNode returnNode = new ReturnNode();
		returnNode.setType(methodDeclaration.getType().asString());
		String baisctype = getBasicType(methodDeclaration.getType());
		File f = null;

		f = ParseUtils.searchJavaFile(javaFile, baisctype);

		if (f != null) {
			ResponseNode responseNode = new ResponseNode();
			responseNode.setClassName(Utils.getJavaFileName(f));
			ParseUtils.parseResponseNode(f, responseNode);
			returnNode.setJsonString((responseNode.toJsonApi()));
		}
		return returnNode;
  }
  
  public static String getBasicType(Type type) {
	  
  	String typeStr=type.asString();
  	if(ParseUtils.TYPE_MODEL.equals(typeStr)||"Void".equalsIgnoreCase(typeStr)){
  		return type.asString();
  	}else if(type instanceof ClassOrInterfaceType) {
  		ClassOrInterfaceType cot=(ClassOrInterfaceType) type;
  		String name=cot.getNameAsString();
  		if(name.equals("Map")||name.equals("HashMap")||name.equals("TreeMap")||name.equals("Hashtable")||name.equals("LinkedHashMap")){
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

/**
   * 解析方法上的注释
   * @param interfaceNode
   * @param javadoc
   */
  public static void parserMethodNotes(InterfaceNode interfaceNode,Javadoc javadoc){

		String description=javadoc.getDescription().toText();
	    interfaceNode.setDescription(description);
	    List<JavadocBlockTag> tagList=javadoc.getBlockTags();
	    for (JavadocBlockTag javadocBlockTag : tagList) {
			String tagName=javadocBlockTag.getTagName();
			
			if ("param".equals(tagName)) {
				ParamNode paramNode = interfaceNode.getParamNodeByName(javadocBlockTag.getName());
				if(paramNode==null){
					paramNode=new ParamNode();
					paramNode.setDescription(javadocBlockTag.getContent().toText());
					interfaceNode.addParamNode(paramNode);
				}else{
					paramNode.setDescription(javadocBlockTag.getContent().toText());
				}
			}else if("return".equals(tagName)){
				String content=javadocBlockTag.getContent().toText();
				ReturnNode returnNode=interfaceNode.getReturnNode();
				if(returnNode==null){
					returnNode=new ReturnNode();
				   interfaceNode.setReturnNode(returnNode);
				}
				interfaceNode.getReturnNode().setDescription(javadocBlockTag.getContent().toText());
	    	}else if("throws".equals(tagName)){
	    	   String excceptonName=javadocBlockTag.getName();
	    	   String desc="";
	    	   if(excceptonName==null){
	    		   String[] context=javadocBlockTag.getContent().toText().split(" ");
	    		   excceptonName=context[0];
	    		   if(context.length>=2) desc=context[1];
	    	  }
	    	   
	    	   Explain exp=interfaceNode.getThrowsNodeByName(excceptonName);
    		   if(exp==null){
    			   exp=new Explain();
    			   exp.setDescription(desc);
    			   interfaceNode.addThrowsNode(exp);
    		   }else {
    			   exp.setDescription(desc);
    		   }
	    	   
	    	  /* if(excceptonName!=null){
	    		   Explain exp=interfaceNode.getThrowsNodeByName(excceptonName);
	    		   if(exp==null){
	    			   exp=new Explain();
	    			   exp.setDescription(javadocBlockTag.getContent().toText());
	    			   interfaceNode.addThrowsNode(exp);
	    		   }else {
	    			   exp.setDescription(javadocBlockTag.getContent().toText());
	    		   }
	    	   }*/
	    	}
		}
	    
	   
	    
	
  }
  
  /**
   * 获取接口继承的类（全名）
   * @param javaFile ：接口文件
   * @return
   */
  public static List<String> getExtendsClass(File javaFile){
	  List<String> result=new ArrayList<String>();
	  CompilationUnit compilationUnit=ParseUtils.compilationUnit(javaFile);
	  String interfaceName=Utils.getJavaFileName(javaFile);
	  //获取该接口实现的类
	  ClassOrInterfaceDeclaration cod=compilationUnit.getInterfaceByName(interfaceName);
	  if(cod!=null){
		  NodeList<ClassOrInterfaceType> extendList=cod.getExtendedTypes();
		  for (ClassOrInterfaceType classOrInterfaceType : extendList) {
			String name=classOrInterfaceType.getNameAsString();
			ImportDeclaration importde=getImportDeclaration(compilationUnit, name);
			if(importde!=null) result.add(importde.getNameAsString());
		}
	  }
	  return result;
  }
    
  public static String getFeildDoc(FieldDeclaration fieldDeclaration){
	  String doc=null;
	  Javadoc javadoc=fieldDeclaration.getJavadoc();
	  if(javadoc!=null) {
		 doc=javadoc.toText().trim();
	  }else if(fieldDeclaration.getComment()!=null){
		  doc=fieldDeclaration.getComment().getContent();
	  }else{
		  NodeList<VariableDeclarator> list= fieldDeclaration.getVariables();
		  for (VariableDeclarator v : list) {          
			  if(v.getComment()!=null) doc=Utils.cleanCommentContent(v.getComment().getContent());
		}
	  }
	  return doc;
  }  
  
  /**
   * 获取类所属的模块名
   * @param packageName
   * @return
   */
  public static String getClassModel(CompilationUnit compilationUnit){
	  String packageName=compilationUnit.getPackageDeclaration().getNameAsString();
	  Set<String> packageSet=DocContext.getModelPackageMapKeySet();
	  for (String name : packageSet) {
		if(packageName.startsWith(name)) return DocContext.getModelPackageMap().get(name);
	}
	  return "";
  } 
  
  
}
