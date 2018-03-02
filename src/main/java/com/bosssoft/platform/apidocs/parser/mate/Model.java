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
    
    private List<Link> controllerLinks=new ArrayList<>();
    
    private List<Link> serviceLinks=new ArrayList<>();
    
    private List<Link> mapperLinks=new ArrayList<>();

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

	public List<Link> getControllerLinks() {
		return controllerLinks;
	}

	public void setControllerLinks(List<Link> controllerLinks) {
		this.controllerLinks = controllerLinks;
	}

	public List<Link> getServiceLinks() {
		return serviceLinks;
	}

	public void setServiceLinks(List<Link> serviceLinks) {
		this.serviceLinks = serviceLinks;
	}

	public List<Link> getMapperLinks() {
		return mapperLinks;
	}

	public void setMapperLinks(List<Link> mapperLinks) {
		this.mapperLinks = mapperLinks;
	}
    
    public void addControllerLinks(Link link){
    	controllerLinks.add(link);
    }
    
    public void addServiceLinks(Link link){
    	serviceLinks.add(link);
    }
    
    public void addMappLinks(Link link){
    	mapperLinks.add(link);
    }
}
