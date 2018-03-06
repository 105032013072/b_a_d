package com.bosssoft.platform.apidocs.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosssoft.platform.apidocs.DocContext;
import com.bosssoft.platform.apidocs.WordTitleType;
import com.bosssoft.platform.apidocs.Docs.DocsConfig;
import com.bosssoft.platform.apidocs.LogUtils;
import com.bosssoft.platform.apidocs.WordListType;
import com.bosssoft.platform.apidocs.WordUtils;
import com.bosssoft.platform.apidocs.parser.mate.ControllerNode;
import com.bosssoft.platform.apidocs.parser.mate.EntityNode;
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

	private Map<String,Model>  modelMap;
	private List<EntityNode> entityNodeList;
    
	public WordDocBuilder(List<EntityNode> entityNodeList,Map<String,Model> modelMap) {
      this.modelMap=modelMap;
      this.entityNodeList=entityNodeList;
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
			
			wordUtils.endDoc();
		}catch(Exception e){
			LogUtils.error("create word error", e);
		}
		
	}

	private void buildMapper(String titleStr, List<MapperNode> mapperNodeList, WordUtils wordUtils) throws Exception{
		wordUtils.renderTitle(titleStr+"  数据访接口", WordTitleType.TITLE_3);
		int number=1;
		for (MapperNode mapperNode : mapperNodeList) {
			wordUtils.renderTitle(titleStr+"."+(number++)+"  "+mapperNode.getDescription(), WordTitleType.TITLE_4);
			//对应实体
			wordUtils.renderListTitle("对应实体: "+mapperNode.getRelationEntity().getDescription(),WordListType.DIAMOND);
			
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
			wordUtils.renderList(serviceNode.getAutowiredMapperList());
		
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
			wordUtils.renderList(controllerNode.getAutowiredServiceList());
			
			//请求列表
			wordUtils.renderListTitle("请求列表",WordListType.DIAMOND);
			int requestnum=1;
			for (RequestNode requestNode : controllerNode.getRequestNodes()) {
				wordUtils.renderContent((requestnum++)+"."+requestNode.getDescription());
				wordUtils.renderListTitle("method: "+String.join(",", requestNode.getMethod()), WordListType.SPOTS);
				wordUtils.renderListTitle("url: "+requestNode.getUrl(), WordListType.SPOTS);
				wordUtils.renderListTitle("参数列表: ", WordListType.SPOTS);
				wordUtils.renderParamTableHashRequired(requestNode.getParamNodes());
				wordUtils.renderListTitle("返回结果: ", WordListType.SPOTS);
				//wordUtils.renderContent(requestNode.getResponseJson());
				wordUtils.renderJsonStr(requestNode.getResponseJson());
			}
		}
	}
	
}
