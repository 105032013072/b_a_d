package com.bosssoft.platform.apidocs.parser.mate;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目模块
 * @author huangxw
 *
 */
public class Model {
    private String modelName;
    
    private String modelLinkUrl;
    
   /* private List<Link> controllerLinks=new ArrayList<>();
    
    private List<Link> serviceLinks=new ArrayList<>();
    
    private List<Link> mapperLinks=new ArrayList<>();*/
    private List<ControllerNode> controllerNodeList=new ArrayList<>();
    
    private List<ServiceNode> serviceNodeList=new ArrayList<>();
    
    private List<MapperNode> mapperNodeList=new ArrayList<>();

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelLinkUrl() {
		return modelLinkUrl;
	}

	public void setModelLinkUrl(String modelLinkUrl) {
		this.modelLinkUrl = modelLinkUrl;
	}

	public List<ControllerNode> getControllerNodeList() {
		return controllerNodeList;
	}

	public void setControllerNodeList(List<ControllerNode> controllerNodeList) {
		this.controllerNodeList = controllerNodeList;
	}

	public List<ServiceNode> getServiceNodeList() {
		return serviceNodeList;
	}

	public void setServiceNodeList(List<ServiceNode> serviceNodeList) {
		this.serviceNodeList = serviceNodeList;
	}

	public List<MapperNode> getMapperNodeList() {
		return mapperNodeList;
	}

	public void setMapperNodeList(List<MapperNode> mapperNodeList) {
		this.mapperNodeList = mapperNodeList;
	}

	public void addControlelrNode(ControllerNode node){
		this.controllerNodeList.add(node);
	}
	
	public void addServiceNode(ServiceNode node){
		this.serviceNodeList.add(node);
	}
	
	public void addMapperNode(MapperNode node){
		this.mapperNodeList.add(node);
	}
}
