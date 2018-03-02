package com.bosssoft.platform.apidocs.parser.mate;

/**
 * 链接
 * @author huangxw
 *
 */
public class Link {
  
	private String linkDesc;
	
	private String linkUrl;
	
	public Link(String linkDesc,String linkUrl){
		this.linkDesc=linkDesc;
		this.linkUrl=linkUrl;
	}

	public String getLinkDesc() {
		return linkDesc;
	}

	public void setLinkDesc(String linkDesc) {
		this.linkDesc = linkDesc;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	
	
}
