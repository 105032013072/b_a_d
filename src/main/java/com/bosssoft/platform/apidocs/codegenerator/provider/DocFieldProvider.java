package com.bosssoft.platform.apidocs.codegenerator.provider;


import java.util.ArrayList;

import java.util.List;

import com.bosssoft.platform.apidocs.Utils;
import com.bosssoft.platform.apidocs.codegenerator.IFieldProvider;
import com.bosssoft.platform.apidocs.codegenerator.model.FieldModel;
import com.bosssoft.platform.apidocs.parser.mate.FieldNode;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;

public class DocFieldProvider implements IFieldProvider {

	@Override
	public List<FieldModel> provideFields(ResponseNode respNode) {
		List<FieldNode>recordNodes = respNode.getChildNodes();
		if(recordNodes == null || recordNodes.isEmpty()){
			return null;
		}
		List<FieldModel> entryFieldList = new ArrayList<>();
		FieldModel entryField;
		for (FieldNode recordNode : recordNodes) {
			entryField = new FieldModel();
			String fieldName = DocFieldHelper.getPrefFieldName(recordNode.getName());
			entryField.setCaseFieldName(Utils.capitalize(fieldName));
			entryField.setFieldName(fieldName);
			entryField.setFieldType(DocFieldHelper.getPrefFieldType(recordNode.getType()));
			entryField.setRemoteFieldName(recordNode.getName());
			entryField.setComment(recordNode.getDescription());
			entryFieldList.add(entryField);
		}
		return entryFieldList;
	}
	
}
