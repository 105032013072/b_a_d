package com.bosssoft.platform.apidocs.parser.controller;

import com.bosssoft.platform.apidocs.LogUtils;
import com.bosssoft.platform.apidocs.ParseUtils;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.parser.mate.ControllerNode;
import com.bosssoft.platform.apidocs.parser.mate.Explain;
import com.bosssoft.platform.apidocs.parser.mate.FieldNode;
import com.bosssoft.platform.apidocs.parser.mate.ParamNode;
import com.bosssoft.platform.apidocs.parser.mate.RequestNode;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;
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
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * parse Controller Java the common part, get all request nodes
 *
 * @author yeguozhong yedaxia.github.com
 */
public abstract class AbsControllerParser {

    private CompilationUnit compilationUnit;
    private ControllerNode controllerNode;
    private File javaFile;
    private Map<String,ServiceNode> serviceNodeMap=new HashMap<>();

    public ControllerNode parse(File javaFile,Map<String,ServiceNode> serviceNodeMap){
        this.serviceNodeMap=serviceNodeMap;
        this.javaFile = javaFile;
        this.compilationUnit = ParseUtils.compilationUnit(javaFile);
        this.controllerNode = new ControllerNode();

        String controllerName = Utils.getJavaFileName(javaFile);
        /*compilationUnit.getOptionalClassByName(controllerName)
              .ifPresent(c -> {
                    parseClassDoc(c);
                    parseMethodDocs(c);
                    afterHandleController(controllerNode, c);
                }); */ 
        
        //contorlelr所属的模块
        String modelName=ParseUtils.getClassModel(compilationUnit);
        controllerNode.setModelName(modelName);
        
        ClassOrInterfaceDeclaration declaration =compilationUnit.getClassByName(controllerName);
        if(declaration!=null){
        	 parseClassDoc(declaration);
     		 parseAutowireService(declaration);//解析调用的service
             parseMethodDocs(declaration);
             afterHandleController(controllerNode, declaration);
        }
    
        return controllerNode; 
    }

    private void parseAutowireService(ClassOrInterfaceDeclaration declaration) {
    	List<FieldDeclaration> fieldList=declaration.getFields();
		for (FieldDeclaration fieldDeclaration : fieldList) {
			VariableDeclarator variable=fieldDeclaration.getVariable(0);
			String fieldType=variable.getType().asString();
			if(IsServiceExist(fieldType)) {
				Explain explain=new Explain();
				ServiceNode serviceNode=serviceNodeMap.get(fieldType);
				explain.setKey(fieldType);
				explain.setType(serviceNode.getHtmlPath());
				explain.setDescription(serviceNode.getDescription());
				controllerNode.addAutowiredService(explain);
			}
				
		}
		
	}

	protected File getControllerFile(){
        return javaFile;
    }

    //解析类的注释
    private void parseClassDoc(ClassOrInterfaceDeclaration c){
     /*   c.getJavadoc().ifPresent( d -> {
            String description = d.getDescription().toText();
            controllerNode.setDescription(Utils.isNotEmpty(description)? description: c.getNameAsString());
            List<JavadocBlockTag> blockTags = d.getBlockTags();
            if(blockTags != null){
                for(JavadocBlockTag blockTag : blockTags){
                    if("author".equalsIgnoreCase(blockTag.getTagName())){
                        controllerNode.setAuthor(blockTag.getContent().toText());
                    }
                }
            }
        });*/
    	Javadoc javadoc=c.getJavadoc();
    	if(javadoc!=null){
    		//注释
			String description = javadoc.getDescription().toText();
			controllerNode.setDescription(Utils.isNotEmpty(description) ? description : c.getNameAsString());

			List<JavadocBlockTag> blockTags = javadoc.getBlockTags();
			if (blockTags != null) {
				for (JavadocBlockTag blockTag : blockTags) {
					if ("author".equalsIgnoreCase(blockTag.getTagName())) {
						controllerNode.setAuthor(blockTag.getContent().toText());
					}
				}
			}
    	}
    	
    	//若类没有注释，则Description为类名
        if(controllerNode.getDescription() == null){
            controllerNode.setDescription(c.getNameAsString());
        }
        
        controllerNode.setClassName(c.getNameAsString());

    }
    private void parseMethodDocs(ClassOrInterfaceDeclaration c){
   
    	//jdk1.7
    	List<MethodDeclaration> MethodDeclarationList=getMethodDeclaration(c);
    	for (MethodDeclaration m : MethodDeclarationList) {
			if(m.getAnnotationByName("RequestMapping")!=null||m.getAnnotationByName("GetMapping")!=null||m.getAnnotationByName("PostMapping")!=null){
				RequestNode requestNode = new RequestNode();
				requestNode.setMethodName(ParseUtils.parserMethodName(m));
                if( m.getAnnotationByClass(Deprecated.class)!=null) requestNode.setDeprecated(true);
				
                //参数名以及类型
                NodeList<Parameter> paramList= m.getParameters();
                for (Parameter p : paramList) {
                	ParamNode paramNode = new ParamNode();
                    paramNode.setName(p.getName().asString());
                	paramNode.setType(p.getType().asString());
                	requestNode.addParamNode(paramNode);
				}
                
                
                
              //方法上的注释（说明以及param）
                Javadoc javadoc=m.getJavadoc();
                if(javadoc!=null){
                	String description = javadoc.getDescription().toText();
                    requestNode.setDescription(description);
                    List<JavadocBlockTag> tagList=javadoc.getBlockTags();
                    for (JavadocBlockTag javadocBlockTag : tagList) {
						if(javadocBlockTag.getTagName().equals("param")){
							ParamNode paramNode = requestNode.getParamNodeByName(javadocBlockTag.getName());
							if(paramNode!=null){
								paramNode.setDescription(javadocBlockTag.getContent().toText());
							}
						}
					}

                }
				
                afterHandleMethod(requestNode, m);

                //解析返回参数
                ResponseNode responseNode = new ResponseNode();
				if(m.getAnnotationByName("ApiDoc")!=null){
					//解析ApiDoc注解
					AnnotationExpr an=m.getAnnotationByName("ApiDoc");
					parserApiDoc(an,requestNode);
					
				}else{
					//直接获取方法的返回值
					requestNode.setReturnString(m.getType().asString());
					String basictype=ParseUtils.getBasicType(m.getType());
					File resultJavaFile=null;
					try{
						resultJavaFile=ParseUtils.searchJavaFile(javaFile, basictype);
					}catch(Exception e){
						LogUtils.info("", "");
					}
					if(resultJavaFile!=null){
						responseNode.setClassName(Utils.getJavaFileName(resultJavaFile));
						ParseUtils.parseResponseNode(resultJavaFile, responseNode);
						requestNode.setResponseNode(responseNode);
					}
					
				}
				
				if(StringUtils.isNullOrBlank(requestNode.getDescription())){
                	requestNode.setDescription(requestNode.getMethodName());
                }
				 controllerNode.addRequestNode(requestNode);
			}
		}
    	
    }
    
    
    

    //解析方法的注释
   /* private void parseMethodDocs(ClassOrInterfaceDeclaration c){
       
    	//jdk1.7
    	List<MethodDeclaration> MethodDeclarationList=getMethodDeclaration(c);
    	for (MethodDeclaration m : MethodDeclarationList) {
			if(m.getAnnotationByName("ApiDoc")!=null){
				AnnotationExpr an=m.getAnnotationByName("ApiDoc");
				RequestNode requestNode = new RequestNode();
				requestNode.setMethodName(ParseUtils.parserMethodName(m));
                if( m.getAnnotationByClass(Deprecated.class)!=null) requestNode.setDeprecated(true);
                
                //方法上的注释（说明以及param）
                Javadoc javadoc=m.getJavadoc();
                if(javadoc!=null){
                	String description = javadoc.getDescription().toText();
                    requestNode.setDescription(description);
                    List<JavadocBlockTag> tagList=javadoc.getBlockTags();
                    for (JavadocBlockTag javadocBlockTag : tagList) {
						if(javadocBlockTag.getTagName().equals("param")){
							ParamNode paramNode = new ParamNode();
                            paramNode.setName(javadocBlockTag.getName());
                            paramNode.setDescription(javadocBlockTag.getContent().toText());
                            requestNode.addParamNode(paramNode);
						}
					}

                }
                
                //参数名以及类型
                NodeList<Parameter> paramList= m.getParameters();
                for (Parameter p : paramList) {
					String paraName=p.getName().asString();
					ParamNode paramNode = requestNode.getParamNodeByName(paraName);
                    if(paramNode != null){
                        paramNode.setType(ParseUtils.unifyType(p.getType().asString()));
                    }
				}
                afterHandleMethod(requestNode, m);
                com.github.javaparser.ast.type.Type resultClassType = null;
                if(an instanceof SingleMemberAnnotationExpr){
                	 resultClassType = ((ClassExpr) ((SingleMemberAnnotationExpr) an).getMemberValue()).getType();
                }else if(an instanceof NormalAnnotationExpr){
                	MemberValuePair pair=null;
                	NodeList<MemberValuePair> memberValueList =((NormalAnnotationExpr)an).getPairs();
                	for (MemberValuePair memberValuePair : memberValueList) {
						if(memberValuePair.getNameAsString().equals("result")) pair=memberValuePair;
					}
                	if(pair!=null) resultClassType = ((ClassExpr) pair.getValue()).getType();
                	
                }
                
                
                
                if(resultClassType == null){
                    return;
                }

                ResponseNode responseNode = new ResponseNode();
                File resultJavaFile;
                
                if(resultClassType.asString().endsWith("[]")){
                	responseNode.setList(Boolean.TRUE);
                    String type = resultClassType.getElementType().asString();
                    resultJavaFile = ParseUtils.searchJavaFile(javaFile, type);
                }else{
                	responseNode.setList(Boolean.FALSE);
                    resultJavaFile = ParseUtils.searchJavaFile(javaFile, resultClassType.asString());
                }
                responseNode.setClassName(Utils.getJavaFileName(resultJavaFile));
                ParseUtils.parseResponseNode(resultJavaFile, responseNode);
                requestNode.setResponseNode(responseNode);

                if(StringUtils.isNullOrBlank(requestNode.getDescription())){
                	requestNode.setDescription(requestNode.getMethodName());
                }
                controllerNode.addRequestNode(requestNode);
			}
		}
    	
    }*/

    private void parserApiDoc(AnnotationExpr an, RequestNode requestNode) {
    	com.github.javaparser.ast.type.Type resultClassType = null;
    	if(an instanceof SingleMemberAnnotationExpr){
       	    resultClassType = ((ClassExpr) ((SingleMemberAnnotationExpr) an).getMemberValue()).getType();
       	    ResponseNode responseNode = new ResponseNode();
       	 responseNode= buildResponseNode(resultClassType);
       	requestNode.setResponseNode(responseNode);
       //	requestNode.setReturnString(responseNode.getClassName());
       	    
    	}else if(an instanceof NormalAnnotationExpr){
    		NodeList<Expression> keyList=new NodeList<>();
    		NodeList<Expression> valueList=new NodeList<>();
    		
    		NodeList<MemberValuePair> memberValueList =((NormalAnnotationExpr)an).getPairs();
        	for (MemberValuePair memberValuePair : memberValueList) {
				if("key".equals(memberValuePair.getNameAsString())){
					keyList=((ArrayInitializerExpr)memberValuePair.getValue()).getValues();
				}else if("value".equals(memberValuePair.getNameAsString())){
					valueList=((ArrayInitializerExpr)memberValuePair.getValue()).getValues();
				}
			}
        	
        	 ResponseNode resultresponseNode = new ResponseNode();
        	 resultresponseNode.setClassName(requestNode.getMethodName()+"ReturnVo");
        	 resultresponseNode.setList(Boolean.FALSE);
        	for(int i=0;i<valueList.size();i++){
        		resultClassType=((ClassExpr)valueList.get(i)).getType();
        		 ResponseNode responseNode= buildResponseNode(resultClassType);
        		 FieldNode fieldNdoe=new FieldNode();
        		
        		 fieldNdoe.setName(((StringLiteralExpr)keyList.get(i)).getValue());
        		 fieldNdoe.setType(responseNode.getClassName());
        		 
        		 if(responseNode.getChildNodes().size()>0){
               		fieldNdoe.setChildResponseNode(responseNode);
        		 } 
        		 resultresponseNode.addChildNode(fieldNdoe);
        	}
        	requestNode.setResponseNode(resultresponseNode);       	
    	}
		
	}
    
    
    

	private ResponseNode buildResponseNode(Type resultClassType) {
		ResponseNode responseNode = new ResponseNode();
		File resultJavaFile;
		/*if(resultClassType.asString().endsWith("[]")){
        	responseNode.setList(Boolean.TRUE);
            String type = resultClassType.getElementType().asString();
            resultJavaFile = ParseUtils.searchJavaFile(javaFile, type);
        }else{
        	responseNode.setList(Boolean.FALSE);
            resultJavaFile = ParseUtils.searchJavaFile(javaFile, resultClassType.asString());
        }
        responseNode.setClassName(Utils.getJavaFileName(resultJavaFile));
        ParseUtils.parseResponseNode(resultJavaFile, responseNode);*/
        
		String type="";
        if(resultClassType.asString().endsWith("[]")){
        	responseNode.setList(Boolean.TRUE);
        	type=resultClassType.getElementType().asString();
        }else{
        	responseNode.setList(Boolean.FALSE);
        	type=resultClassType.asString();
        }
        if(ParseUtils.TYPE_MODEL.equals(ParseUtils.unifyType(type))){
        	 resultJavaFile = ParseUtils.searchJavaFile(javaFile, type);
        	 responseNode.setClassName(Utils.getJavaFileName(resultJavaFile));
             ParseUtils.parseResponseNode(resultJavaFile, responseNode);
        }else{//基本数据类型
        	responseNode.setClassName(type);
        }

        if(responseNode.isList()){
        	responseNode.setClassName(responseNode.getClassName()+"[]");
        }
        return responseNode;
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
    
    private  Boolean IsServiceExist(String serviceName){
    	if(serviceNodeMap.get(serviceName)!=null) return true;
    	else return false;
    }
   

	/**
     * called after controller node has handled
     * @param clazz
     */
    protected void afterHandleController(ControllerNode controllerNode, ClassOrInterfaceDeclaration clazz){}

    /**
     * called after request method node has handled
     */
    protected void afterHandleMethod(RequestNode requestNode, MethodDeclaration md){}
}
