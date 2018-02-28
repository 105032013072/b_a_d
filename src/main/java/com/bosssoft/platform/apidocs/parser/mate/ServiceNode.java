package com.bosssoft.platform.apidocs.parser.mate;

import java.util.ArrayList;
import java.util.List;

public class ServiceNode {
	
	private String serviceName;//类名
	
	private String author;
	
    private String description;
    
    private List<Explain> autowiredMapperList= new ArrayList<>();
    
    private List<InterfaceNode> interfaceNodes = new ArrayList<>();

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<InterfaceNode> getInterfaceNodes() {
		return interfaceNodes;
	}

	public void setInterfaceNodes(List<InterfaceNode> interfaceNodes) {
		this.interfaceNodes = interfaceNodes;
	}
    
    public void addInterfaceNodes(InterfaceNode interfaceNode){
    	interfaceNodes.add(interfaceNode);
    }

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	
    
	public List<Explain> getAutowiredMapperList() {
		return autowiredMapperList;
	}

	public void setAutowiredMapperList(List<Explain> autowiredMapperList) {
		this.autowiredMapperList = autowiredMapperList;
	}

	public void addAutowiredMapper(Explain autowiredMapper) {
		this.autowiredMapperList.add(autowiredMapper);
	}
}
