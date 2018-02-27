package com.bosssoft.platform.apidocs.parser.mate;

import java.util.ArrayList;
import java.util.List;

public class MapperNode {
    private String author;
	
    private String description;
    
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
}
