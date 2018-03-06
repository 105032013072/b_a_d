package com.bosssoft.platform.apidocs.parser.mate;

import java.util.ArrayList;
import java.util.List;

public class MapperNode extends ParentNode{
	
	
    private List<InterfaceNode> interfaceNodes = new ArrayList<>();
    
    private Explain relationEntity;
    
 
	public List<InterfaceNode> getInterfaceNodes() {
		return interfaceNodes;
	}

	public void setInterfaceNodes(List<InterfaceNode> interfaceNodes) {
		this.interfaceNodes = interfaceNodes;
	}
    
    public void addInterfaceNodes(InterfaceNode interfaceNode){
    	interfaceNodes.add(interfaceNode);
    }


	public Explain getRelationEntity() {
		return relationEntity;
	}

	public void setRelationEntity(Explain relationEntity) {
		this.relationEntity = relationEntity;
	}
	
    
}
