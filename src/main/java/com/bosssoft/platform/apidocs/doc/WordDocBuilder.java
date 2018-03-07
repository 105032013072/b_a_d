package com.bosssoft.platform.apidocs.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bosssoft.platform.apidocs.DocContext;
import com.bosssoft.platform.apidocs.Docs.DocsConfig;
import com.bosssoft.platform.apidocs.enumtype.ClassType;
import com.bosssoft.platform.apidocs.enumtype.WordListType;
import com.bosssoft.platform.apidocs.enumtype.WordTitleType;
import com.bosssoft.platform.apidocs.LogUtils;
import com.bosssoft.platform.apidocs.WordUtils;
import com.bosssoft.platform.apidocs.parser.mate.ControllerNode;
import com.bosssoft.platform.apidocs.parser.mate.EntityNode;
import com.bosssoft.platform.apidocs.parser.mate.Explain;
import com.bosssoft.platform.apidocs.parser.mate.InterfaceNode;
import com.bosssoft.platform.apidocs.parser.mate.MapperNode;
import com.bosssoft.platform.apidocs.parser.mate.Model;
import com.bosssoft.platform.apidocs.parser.mate.RequestNode;
import com.bosssoft.platform.apidocs.parser.mate.ReturnNode;
import com.bosssoft.platform.apidocs.parser.mate.ServiceNode;
import com.bosssoft.platform.common.utils.FileUtils;
import com.bosssoft.platform.common.utils.StringUtils;
import com.lowagie.text.DocumentException;


public class WordDocBuilder {

	public static Map<String,Model>  modelMap;
	public static List<EntityNode> entityNodeList;
	public static Map<String,ServiceNode> serviceNodeMap=new HashMap<>();
    public static Map<String,MapperNode> mapperNodeMap=new HashMap<>();
    
    public static int  javaCodeOrder=1;
    public static int  iosCodeOrder=1;
    public static Map<String,String> javaCodeMap=new LinkedHashMap<>();
    public static Map<String,Integer> javaCodeOrderMap=new HashMap<>();
    
    public static Map<String,Integer> iosCodeOrderMap=new HashMap<>();
    public static Map<String,String> iosCodeMap=new LinkedHashMap<>();
    
	public WordDocBuilder(List<EntityNode> entityNodeList,Map<String,Model> modelMap,Map<String,ServiceNode> serviceNodeMap,Map<String,MapperNode> mapperNodeMap) {
      this.modelMap=modelMap;
      this.entityNodeList=entityNodeList;
      this.serviceNodeMap=serviceNodeMap;
      this.mapperNodeMap=mapperNodeMap;
	}
	
	public void buidWordDoc() {
		try{
			WordUtils wordUtils=new WordUtils();
			String docTitle=DocContext.getDocsConfig().getDocTitle();
			String path=DocContext.getDocPath()+File.separator+docTitle+".doc";
			wordUtils.initDocument(path, docTitle);
			
			
			
			//数据实体
			wordUtils.renderTitle("1.数据实体", WordTitleType.TITLE_1);
			int number=1;
			for (EntityNode entityNode : entityNodeList) {
				wordUtils.renderTitle("1."+(number++)+"  "+entityNode.getClassName(), WordTitleType.TITLE_2);
				wordUtils.renderContent("1) 实体名: "+entityNode.getClassName());
				wordUtils.renderContent("2) 对应表: "+entityNode.getTableName());
				wordUtils.renderContent("3) 属性列表： ");
				
				/*String titleContent="表"+(number++)+"-1 "+entityNode.getClassName()+"属性说明";
				wordUtils.renderTableTitle(titleContent);*/
				wordUtils.renderEntityTable(entityNode.getAttributeList());
			}
			
			//功能模块
			wordUtils.renderTitle("2.功能设计", WordTitleType.TITLE_1);
			number=1;
			for (Map.Entry<String,Model> entity : modelMap.entrySet()) {
				wordUtils.renderTitle("2."+number+"  "+entity.getKey(), WordTitleType.TITLE_2);
				
				//controlelr
				buildControlelr("2."+number+".1",entity.getValue().getControllerNodeList(),wordUtils);
				
				
				//service
				buildService("2."+number+".2",entity.getValue().getServiceNodeList(),wordUtils);
				
				//mapper
				buildMapper("2."+number+".3",entity.getValue().getMapperNodeList(),wordUtils);
				
				number++;
			}
			
			
			//附录
			wordUtils.renderTitle("3.附录", WordTitleType.TITLE_1);
			
			//javacode
			wordUtils.renderTitle("3.1 Android Code", WordTitleType.TITLE_2);
			number=1;
			for (Entry<String, String> entity : javaCodeMap.entrySet()) {
				wordUtils.renderTitle("3.1."+(number++)+"  "+entity.getKey(), WordTitleType.TITLE_3);
				wordUtils.renderJsonStr(formateStr(entity.getValue()));
			}
		
			//IOSCode
			wordUtils.renderTitle("3.2 IOS Code", WordTitleType.TITLE_2);
			number=1;
			for (Entry<String, String> entity : iosCodeMap.entrySet()) {
				wordUtils.renderTitle("3.2."+(number++)+"  "+entity.getKey(), WordTitleType.TITLE_3);
				wordUtils.renderJsonStr(formateStr(entity.getValue()));
			}
			
			wordUtils.endDoc();
		}catch(Exception e){
			LogUtils.error("create word error", e);
		}
		
	}

	private String formateStr(String str){
		str=str.replace("\r\n", "\n");
		//去除html元素
		str=str.replace("<pre class=\"prettyprint lang-java\">\n", "");
		str=str.replace("</pre>\n", "");
		str=str.replace("<pre class=\"prettyprint\">\n", "");
		str=str.replace("<xmp>\n", "");
		str=str.replace("</xmp>\n", "");
		str=str.replace("</pre>\n", "");
		return str;
		
	}

	private void buildMapper(String titleStr, List<MapperNode> mapperNodeList, WordUtils wordUtils) throws Exception{
		wordUtils.renderTitle(titleStr+"  数据访接口", WordTitleType.TITLE_3);
		int number=1;
		for (MapperNode mapperNode : mapperNodeList) {
			wordUtils.renderTitle(titleStr+"."+(number++)+"  "+mapperNode.getDescription(), WordTitleType.TITLE_4);
			//对应实体
			/*wordUtils.renderListTitle("对应实体",WordListType.DIAMOND);*/
			wordUtils.renderCorrespondingEntity(mapperNode.getRelationEntity());
			/*List list=new ArrayList<>();
			Explain explain=mapperNode.getRelationEntity();
			list.add(explain);
			
			wordUtils.renderList(list, ClassType.ENTITY);*/
			
			buildMethod(mapperNode.getInterfaceNodes(),wordUtils);
		}
		
	}

	private void buildService(String titleStr, List<ServiceNode> serviceNodeList, WordUtils wordUtils) throws Exception{
		wordUtils.renderTitle(titleStr+"  服务接口", WordTitleType.TITLE_3);
		int number=1;
		for (ServiceNode serviceNode : serviceNodeList) {
			wordUtils.renderTitle(titleStr+"."+(number++)+"  "+serviceNode.getDescription(), WordTitleType.TITLE_4);
			//数据访问接口调用
			wordUtils.renderListTitle("数据访问接口调用",WordListType.DIAMOND);
			wordUtils.renderList(serviceNode.getAutowiredMapperList(),ClassType.MAPPER);
		
			buildMethod(serviceNode.getInterfaceNodes(),wordUtils);
		}
		
	}

	private void buildMethod(List<InterfaceNode> interfaceNodes, WordUtils wordUtils) throws Exception {
		//接口列表
		wordUtils.renderListTitle("请求列表",WordListType.DIAMOND);
		int requestnum=1;
		for (InterfaceNode interfaceNode : interfaceNodes) {
			wordUtils.renderContent((requestnum++)+"."+interfaceNode.getDescription());
			wordUtils.renderListTitle("方法名: "+interfaceNode.getMethodName(), WordListType.SPOTS);
			//参数类表
			if(interfaceNode.getParamNodes()!=null && interfaceNode.getParamNodes().size()>0){
				wordUtils.renderListTitle("参数列表: ", WordListType.SPOTS);
				wordUtils.renderParamTableHash(interfaceNode.getParamNodes());
			}else{
				wordUtils.renderListTitle("参数列表:  无", WordListType.SPOTS);
			}

			
			//异常列表
			if(interfaceNode.getThrowsNode()!=null && interfaceNode.getThrowsNode().size()>0){
				wordUtils.renderListTitle("异常列表: ", WordListType.SPOTS);
				wordUtils.renderExceptionTable(interfaceNode.getThrowsNode());
			}else{
				wordUtils.renderListTitle("异常列表: 无", WordListType.SPOTS);
			}
			
			//返回结果
			ReturnNode returnNode=interfaceNode.getReturnNode();
			String returnstr=returnNode.getType();
			if(StringUtils.isNotNullAndBlank(returnNode.getDescription())) returnstr=returnstr+"   "+returnNode.getDescription();
			wordUtils.renderListTitle("返回结果: "+returnstr, WordListType.SPOTS);
			if(StringUtils.isNotNullAndBlank(returnNode.getJsonString())){
				wordUtils.renderJsonStr(returnNode.getJsonString());
			}
		}
		
	}

	private void buildControlelr(String titleStr, List<ControllerNode> controllerNodeList, WordUtils wordUtils) throws Exception {
		wordUtils.renderTitle(titleStr+"  页面控制器", WordTitleType.TITLE_3);
		int number=1;
		for (ControllerNode controllerNode : controllerNodeList) {
			wordUtils.renderTitle(titleStr+"."+(number++)+"  "+controllerNode.getDescription(), WordTitleType.TITLE_4);
			//服务接口调用
			wordUtils.renderListTitle("服务接口调用",WordListType.DIAMOND);
			wordUtils.renderList(controllerNode.getAutowiredServiceList(),ClassType.SERVICE);
			
			//请求列表
			wordUtils.renderListTitle("请求列表",WordListType.DIAMOND);
			int requestnum=1;
			for (RequestNode requestNode : controllerNode.getRequestNodes()) {
				wordUtils.renderContent((requestnum++)+"."+requestNode.getDescription());
				wordUtils.renderListTitle("method: "+String.join(",", requestNode.getMethod()), WordListType.SPOTS);
				wordUtils.renderListTitle("url: "+requestNode.getUrl(), WordListType.SPOTS);
				wordUtils.renderListTitle("参数列表: ", WordListType.SPOTS);
				wordUtils.renderParamTableHashRequired(requestNode.getParamNodes());
				//wordUtils.renderListTitle("返回结果: ", WordListType.SPOTS);
				//wordUtils.renderContent(requestNode.getResponseJson());
				wordUtils.renderCode(requestNode.getResponseNode().getClassName());
				wordUtils.renderJsonStr(requestNode.getResponseJson());
			}
		}
	}

	public static Map<String, String> getJavaCodeMap() {
		return javaCodeMap;
	}

	public static void setJavaCodeMap(Map<String, String> javaCodeMap) {
		WordDocBuilder.javaCodeMap = javaCodeMap;
	}

	public static Map<String, String> getIosCodeMap() {
		return iosCodeMap;
	}

	public static void setIosCodeMap(Map<String, String> iosCodeMap) {
		WordDocBuilder.iosCodeMap = iosCodeMap;
	}
	
	
	public static void addJavaCode(String className,String codeStr){
		WordDocBuilder.javaCodeMap.put(className, codeStr);
	}
	
	public static void addIOSCode(String className,String codeStr){
		WordDocBuilder.iosCodeMap.put(className, codeStr);
	}
	
	
	public static void addJavaCodeOrder(String className,Integer order){ 
		WordDocBuilder.javaCodeOrderMap.put(className, order);
	}
	
	public static void addIOSCodeOrder(String className,Integer order){
		WordDocBuilder.iosCodeOrderMap.put(className, order);
	}
	
}
