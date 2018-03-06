package com.bosssoft.platform.apidocs.parser.mate;

import java.util.ArrayList;
import java.util.List;

public class ServiceNode extends ParentNode{

    
    private List<Explain> autowiredMapperList= new ArrayList<>();
    
    private List<InterfaceNode> interfaceNodes = new ArrayList<>();

	
	public List<InterfaceNode> getInterfaceNodes() {
		return interfaceNodes;
	}

	public void setInterfaceNodes(List<InterfaceNode> interfaceNodes) {
		this.interfaceNodes = interfaceNodes;
	}
    
    public void addInterfaceNodes(InterfaceNode interfaceNode){
    	interfaceNodes.add(interfaceNode);
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
