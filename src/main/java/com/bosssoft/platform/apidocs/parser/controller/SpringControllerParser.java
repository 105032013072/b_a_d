package com.bosssoft.platform.apidocs.parser.controller;

import com.bosssoft.platform.apidocs.ParseUtils;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.parser.mate.ClassNode;
import com.bosssoft.platform.apidocs.parser.mate.ControllerNode;
import com.bosssoft.platform.apidocs.parser.mate.FieldNode;
import com.bosssoft.platform.apidocs.parser.mate.HeaderNode;
import com.bosssoft.platform.apidocs.parser.mate.ParamNode;
import com.bosssoft.platform.apidocs.parser.mate.RequestNode;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * use for spring mvc
 *
 * @author yeguozhong yedaxia.github.com
 */
public class SpringControllerParser extends AbsControllerParser {

    private final static String[] MAPPING_ANNOTATIONS = {
            "GetMapping", "PostMapping", "PutMapping",
            "PatchMapping", "DeleteMapping", "RequestMapping"
    };

    @Override
    protected void afterHandleController(ControllerNode controllerNode, ClassOrInterfaceDeclaration clazz) {
      /*  clazz.getOptionalAnnotationByName("RequestMapping").ifPresent(a -> {
            if (a instanceof SingleMemberAnnotationExpr) {
                String baseUrl = ((SingleMemberAnnotationExpr) a).getMemberValue().toString();
                controllerNode.setBaseUrl(Utils.removeQuotations(baseUrl));
                return;
            }
            if (a instanceof NormalAnnotationExpr) {
                ((NormalAnnotationExpr) a).getPairs().stream()
                        .filter(v -> isUrlPathKey(v.getNameAsString()))
                        .findFirst()
                        .ifPresent(p -> {
                            controllerNode.setBaseUrl(Utils.removeQuotations(p.getValue().toString()));
                        });
            }
        });*/
    	
    	//jdk1.7
    	AnnotationExpr annotationExpr= clazz.getAnnotationByName("RequestMapping");
    	if(annotationExpr!=null){
    		if(annotationExpr instanceof SingleMemberAnnotationExpr){
    			 String baseUrl = ((SingleMemberAnnotationExpr) annotationExpr).getMemberValue().toString();
                 controllerNode.setBaseUrl(Utils.removeQuotations(baseUrl));
                 return;
    		}
    		if (annotationExpr instanceof NormalAnnotationExpr){
    			NodeList<MemberValuePair> pairList= ((NormalAnnotationExpr) annotationExpr).getPairs();
    			for (MemberValuePair memberValuePair : pairList) {
					if(isUrlPathKey(memberValuePair.getNameAsString())) {
						controllerNode.setBaseUrl(Utils.removeQuotations(memberValuePair.getValue().toString()));
						break;
					}
				}

    		}
    		
    	}

    }

    @Override
    protected void afterHandleMethod(RequestNode requestNode, MethodDeclaration md) {
       //解析方法的注解
    	parserMethodAnnotations(requestNode,md);
    	
    	/*md.getAnnotations().forEach(an -> {
            String name = an.getNameAsString();
            if (Arrays.asList(MAPPING_ANNOTATIONS).contains(name)) {
                String method = Utils.getClassName(name).toUpperCase().replace("MAPPING", "");
                if (!"REQUEST".equals(method)) {
                    requestNode.addMethod(RequestMethod.valueOf(method).name());
                }

                if (an instanceof NormalAnnotationExpr) {
                    ((NormalAnnotationExpr) an).getPairs().forEach(p -> {
                        String key = p.getNameAsString();
                        if (isUrlPathKey(key)) {
                            requestNode.setUrl(Utils.removeQuotations(p.getValue().toString()));
                        }

                        if ("headers".equals(key)) {
                            Expression methodAttr = p.getValue();
                            if (methodAttr instanceof ArrayInitializerExpr) {
                                NodeList<Expression> values = ((ArrayInitializerExpr) methodAttr).getValues();
                                for (Node n : values) {
                                    String[] h = n.toString().split("=");
                                    requestNode.addHeaderNode(new HeaderNode(h[0], h[1]));
                                }
                            } else {
                                String[] h = p.getValue().toString().split("=");
                                requestNode.addHeaderNode(new HeaderNode(h[0], h[1]));
                            }
                        }

                        if ("method".equals(key)) {
                            Expression methodAttr = p.getValue();
                            if (methodAttr instanceof ArrayInitializerExpr) {
                                NodeList<Expression> values = ((ArrayInitializerExpr) methodAttr).getValues();
                                for (Node n : values) {
                                    requestNode.addMethod(RequestMethod.valueOf(Utils.getClassName(n.toString())).name());
                                }
                            } else {
                                requestNode.addMethod(RequestMethod.valueOf(Utils.getClassName(p.getValue().toString())).name());
                            }
                        }
                    });
                }
                if (an instanceof SingleMemberAnnotationExpr) {
                    String url = ((SingleMemberAnnotationExpr) an).getMemberValue().toString();
                    requestNode.setUrl(Utils.removeQuotations(url));
                    return;
                }

            }
        });*/
    	
    	  
    	

        //解析参数的注解
    	parserParameterAnnotations(requestNode,md);
    	
        /*md.getParameters().forEach(p -> {
            String paraName = p.getName().asString();
            ParamNode paramNode = requestNode.getParamNodeByName(paraName);
            if (paramNode != null) {

                p.getAnnotations().forEach(an -> {
                    String name = an.getNameAsString();
                    if (!"RequestParam".equals(name) && !"RequestBody".equals(name)) {
                        return;
                    }

                    if ("RequestBody".equals(name)) {
                        String type = p.getType().asString();
                        setRequestBody(paramNode, type);
                    }

                    if (an instanceof MarkerAnnotationExpr) {
                        paramNode.setRequired(true);
                        return;
                    }

                    if (an instanceof NormalAnnotationExpr) {
                        ((NormalAnnotationExpr) an).getPairs().stream()
                                .filter(n -> n.getNameAsString().equals("required"))
                                .findFirst()
                                .ifPresent(v -> {
                                    paramNode.setRequired(Boolean.valueOf(v.getValue().toString()));
                                });
                    }

                });
            }
        });*/
    }

    private void parserParameterAnnotations(RequestNode requestNode, MethodDeclaration md) {

    	NodeList<Parameter> parameterList= md.getParameters();
    	for (Parameter p : parameterList) {
    		String paraName = p.getName().asString();
            ParamNode paramNode = requestNode.getParamNodeByName(paraName);
          //  String orignType=paramNode.getType();
            if(paramNode!=null){
            	NodeList<AnnotationExpr> annotationExprList= p.getAnnotations();
            	for (AnnotationExpr an : annotationExprList) {
            		String name = an.getNameAsString();
            		if (!"RequestParam".equals(name) && !"RequestBody".equals(name)) {
                        continue;
                    }
            		
            		 if ("RequestBody".equals(name)) {
                         
                         setRequestBody(paramNode, p.getType(),md.getJavadoc());
                        
                     }
            		 
            		 if (an instanceof MarkerAnnotationExpr) {
                         paramNode.setRequired(true);
                         continue;
                     }
            		 if (an instanceof NormalAnnotationExpr){
            			 NodeList<MemberValuePair> memberList=((NormalAnnotationExpr) an).getPairs();
            			 for (MemberValuePair memberValuePair : memberList) {
            				 if(memberValuePair.getNameAsString().equals("required")){
            					 paramNode.setRequired(Boolean.valueOf(memberValuePair.getValue().toString()));
            					 break;
            				 }
						}
            		 }
            		 
				}
            }
            
           // paramNode.setType(orignType);
		}
	}

	

	

	private void parserMethodAnnotations(RequestNode requestNode, MethodDeclaration md) {
    	NodeList<AnnotationExpr> annotationList= md.getAnnotations();
    	for (AnnotationExpr annotationExpr : annotationList) {
    		String name = annotationExpr.getNameAsString();
    		if(Arrays.asList(MAPPING_ANNOTATIONS).contains(name)){
    			 String method = Utils.getClassName(name).toUpperCase().replace("MAPPING", "");
    			 if (!"REQUEST".equals(method)) {
                     requestNode.addMethod(RequestMethod.valueOf(method).name());
                 }
    			 if (annotationExpr instanceof NormalAnnotationExpr){
    				 NodeList<MemberValuePair> memberList= ((NormalAnnotationExpr) annotationExpr).getPairs();
    				 for (MemberValuePair p : memberList) {
    					 String key = p.getNameAsString();
    					 if (isUrlPathKey(key)) {
                             requestNode.setUrl(Utils.removeQuotations(p.getValue().toString()));
                         }
    					 if ("headers".equals(key)){
    						 Expression methodAttr = p.getValue();
    						 if (methodAttr instanceof ArrayInitializerExpr) {
                                 NodeList<Expression> values = ((ArrayInitializerExpr) methodAttr).getValues();
                                 for (Node n : values) {
                                     String[] h = n.toString().split("=");
                                     requestNode.addHeaderNode(new HeaderNode(h[0], h[1]));
                                 }
                             } else {
                                 String[] h = p.getValue().toString().split("=");
                                 requestNode.addHeaderNode(new HeaderNode(h[0], h[1]));
                             }
    					 }
    					 
    					 if ("method".equals(key)) {
                             Expression methodAttr = p.getValue();
                             if (methodAttr instanceof ArrayInitializerExpr) {
                                 NodeList<Expression> values = ((ArrayInitializerExpr) methodAttr).getValues();
                                 for (Node n : values) {
                                     requestNode.addMethod(RequestMethod.valueOf(Utils.getClassName(n.toString())).name());
                                 }
                             } else {
                                 requestNode.addMethod(RequestMethod.valueOf(Utils.getClassName(p.getValue().toString())).name());
                             }
                         }
					}
 
    			 }
    			 if (annotationExpr instanceof SingleMemberAnnotationExpr){
    				 String url = ((SingleMemberAnnotationExpr) annotationExpr).getMemberValue().toString();
                     requestNode.setUrl(Utils.removeQuotations(url));
    			 }
    			 
    		}
		}
		
	}

    private void setRequestBody(ParamNode paramNode, Type type, Javadoc javadoc) {
		Boolean isMap=false;
    	if(type instanceof ClassOrInterfaceType){
			ClassOrInterfaceType cot=(ClassOrInterfaceType) type;
			String name=cot.getNameAsString();
			if(name.equals("Map")||name.equals("HashMap")||name.equals("TreeMap")||name.equals("Hashtable")||name.equals("LinkedHashMap")){
				isMap=true;
				if(javadoc!=null){
					List<JavadocBlockTag> tagList=javadoc.getBlockTags();
					ClassNode classNode = new ClassNode();
					classNode.setClassName(type.asString());
					for (JavadocBlockTag javadocBlockTag : tagList) {
						if(javadocBlockTag.getTagName().equals("param")){
							Boolean isList=false;
							String mapdes=javadocBlockTag.getContent().toText().replaceAll("[ ]+", " ");
							String array[]=mapdes.split(" ");
							String maptype=array[0];
							FieldNode fieldNode=new FieldNode();
							fieldNode.setType(maptype);
							fieldNode.setName(javadocBlockTag.getName());
							if(array.length==2) fieldNode.setDescription(array[1]);
						    if(maptype.endsWith("[]")) isList=true;
							
							
							File f=null;
						    try{
						    	f=ParseUtils.searchJavaFile(getControllerFile(), maptype);
						    }catch (Exception e) {
								System.out.println(maptype+" can not to find java file");
							}
						    
						    if(f!=null){
						    	ResponseNode cn=new ResponseNode();
						    	cn.setClassName(Utils.getJavaFileName(f));
						    	cn.setList(isList);
						    	ParseUtils.parseResponseNode(f, cn);
						    	fieldNode.setChildResponseNode(cn);
						    }
						    
						    classNode.addChildNode(fieldNode);
						}
					}
					paramNode.setNeedjson(true);
					paramNode.setJsonBody(classNode.toJsonApi());
					
				}
			}
		}
		
		if(!isMap){
			setRequestBody(paramNode,type.asString());
		}
		
	}
	
	private void setRequestBody(ParamNode paramNode, String rawType) {
        String modelType;
        boolean isList;
        if (rawType.endsWith("[]")) {
            isList = true;
            modelType = rawType.replace("[]", "");
        } else if (ParseUtils.isCollectionType(rawType)) {
            isList = true;
            modelType = rawType.substring(rawType.indexOf("<") + 1, rawType.length() - 1);
        } else {
            isList = false;
            modelType = rawType;
        }

        if (ParseUtils.isModelType(modelType)) {
            ClassNode classNode = new ClassNode();
            classNode.setClassName(modelType);
            classNode.setList(isList);
            
            File javaFile=ParseUtils.searchJavaFile(getControllerFile(), modelType);
            if(javaFile!=null){
            	ParseUtils.parseResponseNode(javaFile, classNode);
                paramNode.setNeedjson(true);
                paramNode.setJsonBody(classNode.toJsonApi());
            }
        }
    }

    private boolean isUrlPathKey(String name) {
        return name.equals("path") || name.equals("value");
    }
}
