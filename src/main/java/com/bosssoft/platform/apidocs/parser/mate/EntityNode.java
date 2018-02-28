package com.bosssoft.platform.apidocs.parser.mate;

import java.util.ArrayList;
import java.util.List;

public class EntityNode {
   //实体类名
	private String className;
	//作者
	private String author;
	//描述
	private String description;
	
	private String tableName;
	
	private List<AttributeNode> attributeList=new ArrayList<>();

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

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

	public List<AttributeNode> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<AttributeNode> attributeList) {
		this.attributeList = attributeList;
	}
	
	public void addAttributeNode(AttributeNode attributeNode){
		attributeList.add(attributeNode);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

    
	
}
