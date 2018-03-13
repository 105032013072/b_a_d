package com.bosssoft.platform.apidocs.parser.mate;

/**
 * a param node corresponds to a request parameter
 */
public class ParamNode {

	public String name;
	public String type;
	public boolean required;
	public String description;
	public boolean needjson=false;// when true ,the json body set to description
    public String jsonBody;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	
	public boolean isNeedjson() {
		return needjson;
	}

	public void setNeedjson(boolean needjson) {
		this.needjson = needjson;
	}

	public String getJsonBody() {
		return jsonBody;
	}

	public void setJsonBody(String jsonBody) {
		this.jsonBody = jsonBody;
	}
	
	
}
