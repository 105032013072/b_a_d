package com.bosssoft.platform.apidocs.parser.mate;

import java.util.ArrayList;
import java.util.List;

public class MapperNode {
	private String mapperName;//类名
	
    private String author;
	
    private String description;
    
    private List<InterfaceNode> interfaceNodes = new ArrayList<>();
    
    private Explain relationEntity;
    
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

	public String getMapperName() {
		return mapperName;
	}

	public void setMapperName(String mapperName) {
		this.mapperName = mapperName;
	}

	public Explain getRelationEntity() {
		return relationEntity;
	}

	public void setRelationEntity(Explain relationEntity) {
		this.relationEntity = relationEntity;
	}

	
	
    
}
