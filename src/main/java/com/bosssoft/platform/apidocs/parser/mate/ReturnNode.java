package com.bosssoft.platform.apidocs.parser.mate;

/**
 * 返回值
 * @author huangxw
 *
 */
public class ReturnNode {
    public String type;
	
	public String description;
	
	public Boolean isBasic;//是否是基础数据类型
	
	public String jsonString;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsBasic() {
		return isBasic;
	}

	public void setIsBasic(Boolean isBasic) {
		this.isBasic = isBasic;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	
}
