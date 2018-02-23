package com.bosssoft.platform.apidocs.codegenerator;

import java.util.List;

import com.bosssoft.platform.apidocs.codegenerator.model.FieldModel;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;

public interface IFieldProvider {
	/**
	 * get response fields
	 * @param respNode
	 * @return
	 */
	List<FieldModel> provideFields(ResponseNode respNode);
}
