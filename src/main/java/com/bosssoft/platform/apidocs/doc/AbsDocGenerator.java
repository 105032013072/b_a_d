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
import com.bosssoft.platform.apidocs.Docs.DocsConfig;
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
import com.bosssoft.platform.apidocs.parser.mate.Link;
import com.bosssoft.platform.apidocs.parser.mate.MapperNode;
import com.bosssoft.platform.apidocs.parser.mate.Model;
import com.bosssoft.platform.apidocs.parser.mate.ParentNode;
import com.bosssoft.platform.apidocs.parser.mate.RequestNode;
import com.bosssoft.platform.apidocs.parser.mate.ServiceNode;
import com.bosssoft.platform.apidocs.parser.service.AbsServiceParser;
import com.bosssoft.platform.common.utils.FileUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public abstract class AbsDocGenerator{

	private String cssPath;
    private AbsControllerParser controllerParser;
    private AbsServiceParser serviceParser;
    private AbsMapperParser mapperParser;
    private EntityParser entityParser;
    
    private IControllerDocBuilder controllerDocBuilder;
   /* private Map<String,String> controlelrHtmlMap=new HashMap<>();
    private Map<String,String> serviceHtmlMap=new HashMap<>();
    private Map<String,String> mapperHtmlMap=new HashMap<>();*/
    private List<EntityNode> entityHtmlList=new ArrayList<>();
    
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
    	
    	LogUtils.info("create html resource...");
        createhtmlResource();
    	
    	
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
        
        
        //生成word
		WordDocBuilder wordDocBuilder = new WordDocBuilder(new ArrayList<EntityNode>(entityNodeMap.values()),DocContext.getModelMap());
		wordDocBuilder.buidWordDoc();
    }


	private void createhtmlResource() {
		//复制资源到目标目录
		try{
			String orign = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath()+ File.separator + "style.css";
			FileUtils.copy(new File(orign), new File(DocContext.getDocPath()), true);
		    cssPath=DocContext.getDocPath()+File.separator + "style.css";
		}catch(Exception e){
			LogUtils.error(" faild to  create html resource",e);
		}
		
		
	}

	private void generateIndexDocs() {
		
		try{
			
			Map<String,Object> dataModel=new HashMap<>();
			List<ParentNode> modelList=new ArrayList<>();
			
			//创建各个模块的首页
			for (Entry<String, Model> entity : DocContext.getModelMap().entrySet()) {
				//controller
				dataModel.put("type", "页面控制器");
				dataModel.put("nodes", entity.getValue().getControllerNodeList());
			    dataModel.put("cssPath", cssPath);
				String controlelrhtmlPath=DocContext.getDocPath()+File.separator+entity.getKey()+File.separator+"controller_index.html";
				HtmlDocBuilder.buildHtml(dataModel, controlelrhtmlPath, HtmlType.CLASSINDEX);
				
				//service
				dataModel.clear();
				dataModel.put("type", "服务接口");
				dataModel.put("nodes", entity.getValue().getServiceNodeList());
				 dataModel.put("cssPath", cssPath);
				String servicehtmlPath=DocContext.getDocPath()+File.separator+entity.getKey()+File.separator+"service_index.html";
				HtmlDocBuilder.buildHtml(dataModel, servicehtmlPath, HtmlType.CLASSINDEX);
				
				//mapper
				dataModel.clear();
				dataModel.put("type", "数据访问接口");
				dataModel.put("nodes", entity.getValue().getMapperNodeList());
				 dataModel.put("cssPath", cssPath);
				String mapperhtmlPath=DocContext.getDocPath()+File.separator+entity.getKey()+File.separator+"mapper_index.html";
				HtmlDocBuilder.buildHtml(dataModel, mapperhtmlPath, HtmlType.CLASSINDEX);
				
				//模块首页
				dataModel.clear();
				List<ParentNode> list=new ArrayList<>();
				/*Link controlelrLink=new Link("页面控制器", controlelrhtmlPath);
				Link serviceLink=new Link("服务接口",servicehtmlPath);
				Link mapperLink=new Link("数据访问接口",mapperhtmlPath);*/
				ParentNode controlelrPNode=new ParentNode();
				controlelrPNode.setDescription("页面控制器");
				controlelrPNode.setHtmlPath(controlelrhtmlPath);
				list.add(controlelrPNode);
				
				ParentNode servicePNode=new ParentNode();
				servicePNode.setDescription("服务接口");
				servicePNode.setHtmlPath(servicehtmlPath);
				list.add(servicePNode);
				
				ParentNode mapperPNode=new ParentNode();
				mapperPNode.setDescription("数据访问接口");
				mapperPNode.setHtmlPath(mapperhtmlPath);
				list.add(mapperPNode);
				
				
				dataModel.put("type", entity.getKey()+" 功能设计");
				dataModel.put("nodes", list);
				 dataModel.put("cssPath", cssPath);
				String path=DocContext.getDocPath()+File.separator+entity.getKey()+File.separator+entity.getKey()+".html";
				HtmlDocBuilder.buildHtml(dataModel, path, HtmlType.CLASSINDEX);
				
				//Link modelLink=new Link(entity.getKey(), path);
				ParentNode modelPNode=new ParentNode();
				modelPNode.setDescription(entity.getKey());
				modelPNode.setHtmlPath(path);
				modelList.add(modelPNode);
			}
			
			//模块首页
			dataModel.clear();
			dataModel.put("type", "功能设计");
			dataModel.put("nodes", modelList);
			 dataModel.put("cssPath", cssPath);
			String modelIndexpath=DocContext.getDocPath()+File.separator+"model_index.html";
			HtmlDocBuilder.buildHtml(dataModel, modelIndexpath, HtmlType.CLASSINDEX);
		
			//数据实体首页
			dataModel.clear();
			dataModel.put("type", "数据实体");
			dataModel.put("nodes", entityHtmlList);
			 dataModel.put("cssPath", cssPath);
			String entityIndexpath=DocContext.getDocPath()+File.separator+"entity_index.html";
			HtmlDocBuilder.buildHtml(dataModel, entityIndexpath, HtmlType.CLASSINDEX);
		
			modelList.clear();
			ParentNode entityPNode=new ParentNode();
			entityPNode.setDescription("数据实体");
			entityPNode.setHtmlPath(entityIndexpath);
			modelList.add(entityPNode);
			
			ParentNode model=new ParentNode();
			model.setDescription("功能设计");
			model.setHtmlPath(modelIndexpath);
			modelList.add(model);
			dataModel.clear();
			dataModel.put("type", DocContext.getDocsConfig().getDocTitle());
			dataModel.put("nodes", modelList);
			 dataModel.put("cssPath", cssPath);
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
				eNode.setHtmlPath(htmlPath);
				HtmlDocBuilder.buildHtml(eNode, htmlPath, HtmlType.CLASSENTITY);
				 entityHtmlList.add(eNode);
				
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
				mapperNodeMap.put(mNode.getClassName(),mNode);
				
				//设置所属模块
				mNode.setModelName("登录模块");
				
				LogUtils.info("start to generate docs for mapper file : %s", mapperFile.getName());
				 String htmlPath=DocContext.getDocPath()+File.separator+mNode.getModelName()+File.separator+"mapper"+File.separator+mNode.getClassName()+".html";
				 HtmlDocBuilder.buildHtml(mNode, htmlPath, HtmlType.CLASSMAPPER);
				 mNode.setHtmlPath(htmlPath);
			     DocContext.getModelMap().get(mNode.getModelName()).addMapperNode(mNode);
			    
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
				 serviceNodeMap.put(sNode.getClassName(),sNode);
				 
				//设置所属模块
				 sNode.setModelName("登录模块");
				 
				 LogUtils.info("start to generate docs for service file : %s", serviceFile.getName());
				 String htmlPath=DocContext.getDocPath()+File.separator+sNode.getModelName()+File.separator+"service"+File.separator+sNode.getClassName()+".html";
				 HtmlDocBuilder.buildHtml(sNode, htmlPath, HtmlType.CLASSSERVICE);
				 sNode.setHtmlPath(htmlPath);
				 DocContext.getModelMap().get(sNode.getModelName()).addServiceNode(sNode);;
				 
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
                controllerNode.setModelName("登录模块");
               /* String controllerDocs = controllerDocBuilder.buildDoc(controllerNode);
                String docName = controllerNode.getDescription();
                docFileNameList.add(docName);
                Utils.writeToDisk(new File(DocContext.getDocPath(), docName+".html"),controllerDocs);*/
                
                //构造json、Android Code、IOS Code值以及页面
                List<RequestNode> requestNodeList= controllerNode.getRequestNodes();
                for (RequestNode requestNode : requestNodeList) {
                	 JavaCodeGenerator javaCodeGenerator = new JavaCodeGenerator(requestNode.getResponseNode());
                     String javaurl =javaCodeGenerator.generateCode();
                     javaurl=DocContext.getDocPath()+File.separator+javaurl;
                     requestNode.setAndroidCodeUrl(javaurl);
                     
                     ModelCodeGenerator iosCodeGenerator = new ModelCodeGenerator(requestNode.getResponseNode());
                     String iosUrl = iosCodeGenerator.generateCode();
                     iosUrl=DocContext.getDocPath()+File.separator+iosUrl;
                     requestNode.setiOSCodeUrl(iosUrl);
                     
                     requestNode.setResponseJson(requestNode.getResponseNode().toJsonApi());
				}
                
                String htmlPath=DocContext.getDocPath()+File.separator+controllerNode.getModelName()+File.separator+"controller"+File.separator+controllerNode.getClassName()+".html";
				 HtmlDocBuilder.buildHtml(controllerNode, htmlPath, HtmlType.CLASSCONTROLLER);
				 controllerNode.setHtmlPath(htmlPath);
				 LogUtils.info("success to generate docs for controller file : %s", controllerFile.getName());
                DocContext.getModelMap().get(controllerNode.getModelName()).addControlelrNode(controllerNode);
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
