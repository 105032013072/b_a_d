package com.bosssoft.platform.apidocs;

import com.bosssoft.platform.apidocs.parser.*;
import com.bosssoft.platform.apidocs.parser.controller.AbsControllerParser;
import com.bosssoft.platform.apidocs.parser.controller.GenericControllerParser;
import com.bosssoft.platform.apidocs.parser.controller.JFinalControllerParser;
import com.bosssoft.platform.apidocs.parser.controller.JFinalRoutesParser;
import com.bosssoft.platform.apidocs.parser.controller.PlayControllerParser;
import com.bosssoft.platform.apidocs.parser.controller.PlayRoutesParser;
import com.bosssoft.platform.apidocs.parser.controller.SpringControllerParser;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;
import com.bosssoft.platform.apidocs.parser.service.AbsServiceParser;
import com.bosssoft.platform.apidocs.parser.service.SpringServiceParser;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
 * to judge project which framework is using and make some initialization
 *
 * @author yeguozhong yedaxia.github.com
 */
public class DocContext {

    private static String projectPath;
    private static String docPath;
    private static String javaSrcPath;
    private static AbsControllerParser controllerParser;
    private static AbsServiceParser serviceParser;
    private static List<File> controllerFiles;
    private static List<File> serviceFiles;
    private static IResponseWrapper responseWrapper;
    private static Docs.DocsConfig config;
    
    public static void init(Docs.DocsConfig config){
        DocContext.config = config;
        setProjectPath(config.projectPath);
        setDocPath(config.docsPath);
        Resources.setUserCodeTplPath(config.codeTplPath);

        File logFile = getLogFile();
        if(logFile.exists()){
            logFile.delete();
        }

        //try to find javaSrcPath
        final File projectDir = new File(projectPath);
        List<File> result = new ArrayList<>();
        Utils.wideSearchFile(projectDir, new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                if(name.endsWith(".java")){
                	PackageDeclaration packageDeclaration=ParseUtils.compilationUnit(file).getPackageDeclaration();
                	//Optional<PackageDeclaration> opPackageDeclaration = ParseUtils.compilationUnit(file).getPackageDeclarationOptional();
                    if(packageDeclaration!=null){
                        String packageName = packageDeclaration.getNameAsString();
                        if(Utils.hasDirInFile(file, projectDir, "test") && !packageName.contains("test")){
                            return false;
                        }else{
                            return true;
                        }
                    }
                    return !Utils.hasDirInFile(file, projectDir, "test");
                }
                return false;
            }
        }, result, true);

        if(result.isEmpty()){
            throw new RuntimeException("cannot find any java file in this project : " + projectPath);
        }

        File oneJavaFile = result.get(0);
      // Optional<PackageDeclaration> opPackageDeclaration = ParseUtils.compilationUnit(oneJavaFile).getPackageDeclarationOptional();
        PackageDeclaration packageDeclaration=ParseUtils.compilationUnit(oneJavaFile).getPackageDeclaration();
        String parentPath = oneJavaFile.getParentFile().getAbsolutePath();
        if(packageDeclaration!=null){
            DocContext.javaSrcPath = parentPath.substring(0, parentPath.length() - packageDeclaration.getNameAsString().length());
        }else{
            DocContext.javaSrcPath = parentPath + "/";
        }

        result.clear();

        LogUtils.info("find java src path : %s", javaSrcPath);

        File javaSrcDir = new File(javaSrcPath);

        //which mvc framework
        ProjectType projectType = null;
        if(config.isSpringMvcProject()){
            projectType = ProjectType.SPRING;
        }else if(config.isJfinalProject()){
            projectType = ProjectType.JFINAL;
        }else if(config.isPlayProject()){
            projectType = ProjectType.PLAY;
        }else if(config.isGeneric()){
            projectType = ProjectType.GENERIC;
        }

        if(projectType == null){
            if(Utils.isPlayFramework(projectDir)){
                projectType = ProjectType.PLAY;
            }else if(Utils.isJFinalFramework(javaSrcDir)){
                projectType = ProjectType.JFINAL;
            }else if(Utils.isSpringFramework(javaSrcDir)){
                projectType = ProjectType.SPRING;
            }else{
                projectType = ProjectType.GENERIC;
            }
        }

        //加载controller类文件
        controllerFiles = new ArrayList<>();
        Set<String> controllerFileNames;

        switch (projectType){
            case PLAY:
                controllerParser = new PlayControllerParser();
                controllerFileNames = new LinkedHashSet<>();
                List<PlayRoutesParser.RouteNode> routeNodeList = PlayRoutesParser.INSTANCE.getRouteNodeList();

                for(PlayRoutesParser.RouteNode node : routeNodeList){
                    controllerFileNames.add(node.controllerFile);
                }

                for(String controllerFileName : controllerFileNames){
                    controllerFiles.add(new File(controllerFileName));
                }

                LogUtils.info("found it a play framework project, tell us i f we are wrong.");
                break;
            case JFINAL:
                controllerParser = new JFinalControllerParser();
                controllerFileNames = new LinkedHashSet<>();
                List<JFinalRoutesParser.RouteNode> jFinalRouteNodeList = JFinalRoutesParser.INSTANCE.getRouteNodeList();

                for(JFinalRoutesParser.RouteNode node : jFinalRouteNodeList){
                    controllerFileNames.add(node.controllerFile);
                }

                for(String controllerFileName : controllerFileNames){
                    controllerFiles.add(new File(controllerFileName));
                }
                LogUtils.info("found it a jfinal project, tell us if we are wrong.");
                break;
            case SPRING:
                controllerParser = new SpringControllerParser();
                Utils.wideSearchFile(javaSrcDir, new FilenameFilter() {
                    @Override
                    public boolean accept(File f, String name) {
                       /* return f.getName().endsWith(".java") && ParseUtils.compilationUnit(f)
                                .getChildNodesByType(ClassOrInterfaceDeclaration.class)
                                .stream()
                                .anyMatch(cd -> cd.getAnnotationByName("Controller").isPresent() || cd.getAnnotationByName("RestController").isPresent());*/
                
                    	if(!(f.getName().endsWith(".java"))) return false;
                    	List<ClassOrInterfaceDeclaration> list= ParseUtils.compilationUnit(f).getChildNodesByType(ClassOrInterfaceDeclaration.class);
                    	for (ClassOrInterfaceDeclaration cd : list) {
                    		if((cd.isAnnotationPresent("Controller")||cd.isAnnotationPresent("RestController"))) return true;
						}
                    	
                    	return false;
                    }
                }, result, false);
                controllerFiles.addAll(result);
                LogUtils.info("found it a spring mvc project, tell us if we are wrong.");
                break;
            default:
                controllerParser = new GenericControllerParser();
                Utils.wideSearchFile(javaSrcDir, new FilenameFilter() {
                    @Override
                    public boolean accept(File f, String name) {
                       /* return f.getName().endsWith(".java") && ParseUtils.compilationUnit(f)
                                .getChildNodesByType(ClassOrInterfaceDeclaration.class)
                                .stream()
                                .anyMatch(cd -> {
                                    return cd.getChildNodesByType(MethodDeclaration.class)
                                            .stream()
                                            .anyMatch(md -> md.getAnnotationByName("ApiDoc").isPresent());
                                });*/
                    	
                    	if(!(f.getName().endsWith(".java"))) return false;
                    	List<ClassOrInterfaceDeclaration> list=ParseUtils.compilationUnit(f).getChildNodesByType(ClassOrInterfaceDeclaration.class);
                    	for (ClassOrInterfaceDeclaration cd : list) {
                    		if(hashApiDocAnnotation(cd)) return true;
						}
                    	return false;
                    }

					private boolean hashApiDocAnnotation(ClassOrInterfaceDeclaration cd) {
						 List<MethodDeclaration> methodDeclarationList= cd.getChildNodesByType(MethodDeclaration.class);
						 for (MethodDeclaration md : methodDeclarationList) {
							if(md.isAnnotationPresent("ApiDoc")) return true;
						}
						 
						return false;
					}
                }, result, false);
                controllerFiles.addAll(result);
                LogUtils.info("it's a generic project.");
                break;
        }
        
        //加载service的类文件()
		serviceFiles = new ArrayList<>();
		result.clear();
		switch (projectType) {
		 case SPRING:
			serviceParser = new SpringServiceParser();
			Utils.wideSearchFile(javaSrcDir, new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {

                	if(!(f.getName().endsWith(".java"))) return false;
                	List<ClassOrInterfaceDeclaration> list= ParseUtils.compilationUnit(f).getChildNodesByType(ClassOrInterfaceDeclaration.class);
                	for (ClassOrInterfaceDeclaration cd : list) {
                		if((cd.isAnnotationPresent("Service"))) return true;
					}
                	
                	return false;
                }
            }, result, false);
            serviceFiles.addAll(result);
			break;
        }
    }

    /**
     * get log file path
     * @return
     */
    public static File getLogFile(){
        return new File(DocContext.getDocPath() , "apidoc.log");
    }

    /**
     * get project path
     */
    public static String getProjectPath() {
        return projectPath;
    }

    private static void setProjectPath(String projectPath) {
        DocContext.projectPath = new File(projectPath).getAbsolutePath() + "/";
    }

    /**
     * api docs output path
     * @return
     */
    public static String getDocPath() {
        return docPath;
    }

    private static void setDocPath(String docPath) {

        if(docPath == null || docPath.isEmpty()){
            docPath = projectPath + "apidocs";
        }

        File docDir = new File(docPath);
        if(!docDir.exists()){
            docDir.mkdirs();
        }
        DocContext.docPath = docPath;
    }

    /**
     * get java src path
     * @return
     */
    public static String getJavaSrcPath(){
        return javaSrcPath;
    }

    /**
     * get all controllers in this project
     * @return
     */
    public static File[] getControllerFiles(){
        return controllerFiles.toArray(new File[controllerFiles.size()]);
    }

    /**
     * get controller parser, it will return different parser by different framework you are using.
     * @return
     */
    public static AbsControllerParser controllerParser(){
        return controllerParser;
    }

    public static IResponseWrapper getResponseWrapper() {
        if(responseWrapper == null){
            responseWrapper = new IResponseWrapper() {
                @Override
                public Map<String,Object> wrapResponse(ResponseNode responseNode) {
                    Map<String,Object> resultMap = new HashMap<>();
                    resultMap.put("code", 0);
                    resultMap.put("data", responseNode);
                    resultMap.put("msg","success");
                    return resultMap;
                }
            };
        }
        return responseWrapper;
    }

    public static Docs.DocsConfig getDocsConfig(){
        return DocContext.config;
    }

    static void setResponseWrapper(IResponseWrapper responseWrapper) {
        DocContext.responseWrapper = responseWrapper;
    }

	public static AbsServiceParser getServiceParser() {
		return serviceParser;
	}

	public static File[] getServiceFiles() {
	  return serviceFiles.toArray(new File[serviceFiles.size()]);
	}
    
    
}
