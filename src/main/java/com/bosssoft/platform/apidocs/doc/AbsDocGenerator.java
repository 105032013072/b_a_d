package com.bosssoft.platform.apidocs.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bosssoft.platform.apidocs.DocContext;
import com.bosssoft.platform.apidocs.HtmlType;
import com.bosssoft.platform.apidocs.LogUtils;
import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.codegenerator.ios.ModelCodeGenerator;
import com.bosssoft.platform.apidocs.codegenerator.java.JavaCodeGenerator;
import com.bosssoft.platform.apidocs.parser.controller.AbsControllerParser;
import com.bosssoft.platform.apidocs.parser.entity.EntityParser;
import com.bosssoft.platform.apidocs.parser.mapper.AbsMapperParser;
import com.bosssoft.platform.apidocs.parser.mate.ControllerNode;
import com.bosssoft.platform.apidocs.parser.mate.EntityNode;
import com.bosssoft.platform.apidocs.parser.mate.MapperNode;
import com.bosssoft.platform.apidocs.parser.mate.RequestNode;
import com.bosssoft.platform.apidocs.parser.mate.ServiceNode;
import com.bosssoft.platform.apidocs.parser.service.AbsServiceParser;
import com.bosssoft.platform.common.utils.FileUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public abstract class AbsDocGenerator{

    private AbsControllerParser controllerParser;
    private AbsServiceParser serviceParser;
    private AbsMapperParser mapperParser;
    private EntityParser entityParser;
    
    private IControllerDocBuilder controllerDocBuilder;
    private Map<String,String> controlelrHtmlMap=new HashMap<>();
    private Map<String,String> serviceHtmlMap=new HashMap<>();
    private Map<String,String> mapperHtmlMap=new HashMap<>();
    private Map<String,String> entityHtmlMap=new HashMap<>();
    
    private List<ControllerNode> controllerNodeList = new ArrayList<>();
    private Map<String,ServiceNode> serviceNodeMap=new HashMap<>();
    private Map<String,MapperNode> mapperNodeMap=new HashMap<>();
    private Map<String,EntityNode> entityNodeMap=new HashMap<>();
    
    
    public AbsDocGenerator(AbsControllerParser controllerParser,AbsServiceParser serviceParser,AbsMapperParser mapperParser,EntityParser entityParser, IControllerDocBuilder controllerDocBuilder) {
        this.controllerParser = controllerParser;
        this.serviceParser=serviceParser;
        this.mapperParser=mapperParser;
        this.controllerDocBuilder = controllerDocBuilder;
        this.entityParser=entityParser;
    }

    /**
     * generate api Docs
     */
    public void generateDocs(){
        LogUtils.info("generate api docs start...");
        
        LogUtils.info("generate api docs for entity...");
        generateEntitysDocs();
        
        LogUtils.info("generate api docs for mapper...");
        generateMapperssDocs();
        
        LogUtils.info("generate api docs for service...");
        generateServicessDocs();
        
        LogUtils.info("generate api docs for controller...");
        generateControllersDocs();
      
        generateIndexDocs();
        
        //generateIndex(docFileNameList);
        LogUtils.info("generate api docs done !!!");
    }


	private void generateIndexDocs() {
		//复制资源到目标目录
		try{
		String orign=Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath()+File.separator+"style.css";
		FileUtils.copy(new File(orign), new File(DocContext.getDocPath()), true);
		
		
		Map<String,Object> dataModel=new HashMap<>();
		
			//controlelr 主页
			dataModel.put("type", "Controller");
			dataModel.put("nodes", controlelrHtmlMap);
			String controlelrhtmlPath=DocContext.getDocPath()+File.separator+"controller_index.html";
			HtmlDocBuilder.buildHtml(dataModel, controlelrhtmlPath, HtmlType.CLASSINDEX);
			
			//service 主页
			dataModel.clear();
			dataModel.put("type", "Service");
			dataModel.put("nodes", serviceHtmlMap);
			String servicehtmlPath=DocContext.getDocPath()+File.separator+"sesrvice_index.html";
			HtmlDocBuilder.buildHtml(dataModel, servicehtmlPath, HtmlType.CLASSINDEX);
			
			//mapper 主页
			dataModel.clear();
			dataModel.put("type", "Mapper");
			dataModel.put("nodes", mapperHtmlMap);
			String mapperhtmlPath=DocContext.getDocPath()+File.separator+"mapper_index.html";
			HtmlDocBuilder.buildHtml(dataModel, mapperhtmlPath, HtmlType.CLASSINDEX);
			
			//entity 主页
			dataModel.clear();
			dataModel.put("type", "Entity");
			dataModel.put("nodes", entityHtmlMap);
			String entityhtmlPath=DocContext.getDocPath()+File.separator+"entity_index.html";
			HtmlDocBuilder.buildHtml(dataModel, entityhtmlPath, HtmlType.CLASSINDEX);
			
		   Map<String,String> nodes=new LinkedHashMap<>();
		 
		   nodes.put(controlelrhtmlPath, "Controller");
		   nodes.put(servicehtmlPath, "Service");
		   nodes.put(mapperhtmlPath, "Mapper");
		   nodes.put(entityhtmlPath, "Entity");
		  
		   dataModel.clear();
		   dataModel.put("type", "API");
		   dataModel.put("nodes", nodes);
		   String path=DocContext.getDocPath()+File.separator+"index.html";
		   HtmlDocBuilder.buildHtml(dataModel, path, HtmlType.CLASSINDEX);
		   
		}catch(Exception e){
			LogUtils.error("generate docs for index file faild: ", e);
		}
		
		
	}

	private void generateEntitysDocs() {
    	File[] entityFiles=DocContext.getEntityFiles();
		for (File entityFile : entityFiles) {
			try{
				EntityNode eNode=entityParser.parse(entityFile);
				entityNodeMap.put(eNode.getClassName(), eNode);
				LogUtils.info("start to generate docs for entity file : %s", entityFile.getName());
				String htmlPath=DocContext.getDocPath()+File.separator+"entity"+File.separator+eNode.getClassName()+".html";
				 HtmlDocBuilder.buildHtml(eNode, htmlPath, HtmlType.CLASSENTITY);
				 entityHtmlMap.put(htmlPath, eNode.getDescription());
			}catch(Exception e){
				LogUtils.error("generate docs for entity file : "+entityFile.getName(), e);
			}
			
		}
	}

	private void generateMapperssDocs() {
		File[] mapperFiles=DocContext.getMapperFile();
		for (File mapperFile : mapperFiles) {
			try{
				MapperNode mNode=mapperParser.parse(mapperFile,entityNodeMap);
				mapperNodeMap.put(mNode.getMapperName(),mNode);
				
				LogUtils.info("start to generate docs for mapper file : %s", mapperFile.getName());
				 String htmlPath=DocContext.getDocPath()+File.separator+"mapper"+File.separator+mNode.getMapperName()+".html";
				 HtmlDocBuilder.buildHtml(mNode, htmlPath, HtmlType.CLASSMAPPER);
				 mapperHtmlMap.put(htmlPath, mNode.getDescription());
			}catch(Exception e){
				LogUtils.error("generate docs for mapper file : "+mapperFile.getName(), e);
			}
			
		}
	}

	private void generateServicessDocs() {
		File[] serviceFiles=DocContext.getServiceFiles();
		for (File serviceFile : serviceFiles) {
			try{
				 LogUtils.info("start to parse service file : %s", serviceFile.getName());
				 ServiceNode sNode=serviceParser.parse(serviceFile,mapperNodeMap);
				 serviceNodeMap.put(sNode.getServiceName(),sNode);
				 
				 LogUtils.info("start to generate docs for service file : %s", serviceFile.getName());
				 String htmlPath=DocContext.getDocPath()+File.separator+"service"+File.separator+sNode.getServiceName()+".html";
				 HtmlDocBuilder.buildHtml(sNode, htmlPath, HtmlType.CLASSSERVICE);
				 serviceHtmlMap.put(htmlPath, sNode.getDescription());
			}catch(Exception e){
				LogUtils.error("generate docs for service file : "+serviceFile.getName(), e);
			}
		}
		
	}

	private void generateControllersDocs(){
        File[] controllerFiles = DocContext.getControllerFiles();
        for (File controllerFile : controllerFiles) {
            try {
                LogUtils.info("start to parse controller file : %s", controllerFile.getName());
                ControllerNode controllerNode = controllerParser.parse(controllerFile,serviceNodeMap);
                if(controllerNode.getRequestNodes().isEmpty()){
                    continue;
                }
                controllerNodeList.add(controllerNode);
                LogUtils.info("start to generate docs for controller file : %s", controllerFile.getName());
               /* String controllerDocs = controllerDocBuilder.buildDoc(controllerNode);
                String docName = controllerNode.getDescription();
                docFileNameList.add(docName);
                Utils.writeToDisk(new File(DocContext.getDocPath(), docName+".html"),controllerDocs);*/
                
                //构造json、Android Code、IOS Code值以及页面
                List<RequestNode> requestNodeList= controllerNode.getRequestNodes();
                for (RequestNode requestNode : requestNodeList) {
                	 JavaCodeGenerator javaCodeGenerator = new JavaCodeGenerator(requestNode.getResponseNode());
                     String javaurl = javaCodeGenerator.generateCode();
                     requestNode.setAndroidCodeUrl(javaurl);
                     
                     ModelCodeGenerator iosCodeGenerator = new ModelCodeGenerator(requestNode.getResponseNode());
                     String iosUrl = iosCodeGenerator.generateCode();
                     requestNode.setiOSCodeUrl(iosUrl);
                     
                     requestNode.setResponseJson(requestNode.getResponseNode().toJsonApi());
				}
                
                String htmlPath=DocContext.getDocPath()+File.separator+"controller"+File.separator+controllerNode.getControlelrName()+".html";
				 HtmlDocBuilder.buildHtml(controllerNode, htmlPath, HtmlType.CLASSCONTROLLER);
                LogUtils.info("success to generate docs for controller file : %s", controllerFile.getName());
                controlelrHtmlMap.put(htmlPath, controllerNode.getDescription());
            } catch (Exception e) {
                LogUtils.error("generate docs for controller file : "+controllerFile.getName()+" fail", e);
            }
        }

    }

    public List<ControllerNode> getControllerNodeList(){
        return controllerNodeList;
    }
    
    
    
    
	abstract void generateIndex(List<String> docFileNameList);
}
