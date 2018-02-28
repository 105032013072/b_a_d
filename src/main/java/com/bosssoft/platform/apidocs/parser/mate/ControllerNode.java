package com.bosssoft.platform.apidocs.parser.mate;

import java.util.ArrayList;
import java.util.List;

/**
 * a controller node corresponds to a controller file
 *
 * @author yeguozhong yedaxia.github.com
 */
public class ControllerNode {

	private String controlelrName;//类名
    private String author;
    private String description;
    private String baseUrl;
    private List<Explain> autowiredServiceList= new ArrayList<>();

    private List<RequestNode> requestNodes = new ArrayList<>();

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

    public String getBaseUrl() {
        return baseUrl == null ? "" : baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<RequestNode> getRequestNodes() {
        return requestNodes;
    }

    public void setRequestNodes(List<RequestNode> requestNodes) {
        this.requestNodes = requestNodes;
    }

    public void addRequestNode(RequestNode requestNode){
        requestNodes.add(requestNode);
    }

	public String getControlelrName() {
		return controlelrName;
	}

	public void setControlelrName(String controlelrName) {
		this.controlelrName = controlelrName;
	}

	public List<Explain> getAutowiredServiceList() {
		return autowiredServiceList;
	}

	public void setAutowiredServiceList(List<Explain> autowiredServiceList) {
		this.autowiredServiceList = autowiredServiceList;
	}

	public void addAutowiredService(Explain autowiredService){
		this.autowiredServiceList.add(autowiredService);
	}
    
    
    
}
