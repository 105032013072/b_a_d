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
import com.bosssoft.platform.apidocs.WordUtils;
import com.bosssoft.platform.apidocs.parser.mate.ControllerNode;
import com.bosssoft.platform.apidocs.parser.mate.EntityNode;
import com.bosssoft.platform.apidocs.parser.mate.MapperNode;
import com.bosssoft.platform.apidocs.parser.mate.ServiceNode;
import com.bosssoft.platform.common.utils.FileUtils;


public class WordDocBuilder {

	private List<ControllerNode> controllerNodeList;
	private List<ServiceNode> serviceNodeList;
	private List<MapperNode> mapperNodeList;
	private List<EntityNode> entityNodeList;
    
	public WordDocBuilder(List<ControllerNode> controllerNodeList, List<ServiceNode> serviceNodeList,
			List<MapperNode> mapperNodeList, List<EntityNode> entityNodeList) {
      this.controllerNodeList=controllerNodeList;
      this.serviceNodeList=serviceNodeList;
      this.mapperNodeList=mapperNodeList;
      this.entityNodeList=entityNodeList;
	}
	
	public void buidWordDoc() {
		try{
			WordUtils wordUtils=new WordUtils();
			String docTitle=DocContext.getDocsConfig().getDocTitle();
			String path=DocContext.getDocPath()+File.separator+docTitle+".doc";
			wordUtils.initDocument(path, docTitle);
			
			
			
			//数据实体
			wordUtils.renderTitle("数据实体", WordTitleType.TITLE_1);
			int number=1;
			for (EntityNode entityNode : entityNodeList) {
				wordUtils.renderTitle((number++)+"."+entityNode.getClassName(), WordTitleType.TITLE_2);
				wordUtils.renderContent("1) 实体名: "+entityNode.getClassName());
				wordUtils.renderContent("2) 对应表: "+entityNode.getTableName());
				wordUtils.renderContent("3) 属性列表： ");
				wordUtils.renderContent("表4-1  SelectSqlObj类字段说明");
				wordUtils.renderEntityTable(entityNode.getAttributeList());
			}
			
			wordUtils.endDoc();
		}catch(Exception e){
			LogUtils.error("create word error", e);
		}
		
	}
	
}
