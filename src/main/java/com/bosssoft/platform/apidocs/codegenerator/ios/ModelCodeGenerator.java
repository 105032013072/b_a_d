package com.bosssoft.platform.apidocs.codegenerator.ios;

import java.io.IOException;
import java.util.List;

import com.bosssoft.platform.apidocs.codegenerator.CodeGenerator;
import com.bosssoft.platform.apidocs.codegenerator.IFieldProvider;
import com.bosssoft.platform.apidocs.codegenerator.TemplateProvider;
import com.bosssoft.platform.apidocs.codegenerator.ios.builder.ModelBuilder;
import com.bosssoft.platform.apidocs.codegenerator.ios.builder.ModelFieldBuilder;
import com.bosssoft.platform.apidocs.codegenerator.model.FieldModel;
import com.bosssoft.platform.apidocs.codegenerator.provider.ProviderFactory;
import com.bosssoft.platform.apidocs.parser.mate.ResponseNode;

public class ModelCodeGenerator extends CodeGenerator{
	
	private static final String FILE_FIELD_TEMPLATE = "IOS_Model_Field.tpl";
	private static final String FILE_MODEL_TEMPLATE = "IOS_Model.tpl";
	private static final String FILE_CODE_TEMPLATE = "Code_File.html.tpl";
	public static final String IOS_CODE_DIR = "iosCodes";
	
	private static String sFieldTemplate;
	private static String sModelTemplate;
	private static String sCodeTemplate;
	
	static{
		ModelTemplateProvider resourceTemplateProvider = new ModelTemplateProvider();
		try {
			sFieldTemplate = resourceTemplateProvider.provideTemplateForName(FILE_FIELD_TEMPLATE);
			sModelTemplate = resourceTemplateProvider.provideTemplateForName(FILE_MODEL_TEMPLATE);
			sCodeTemplate = TemplateProvider.provideTemplateForName(FILE_CODE_TEMPLATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ModelCodeGenerator(ResponseNode responseNode) {
		super(responseNode);
	}

	@Override
	public String generateNodeCode(ResponseNode respNode) throws IOException {
		String className = respNode.getClassName();
		IFieldProvider entryProvider = ProviderFactory.createProvider();
		List<FieldModel> entryFields = entryProvider.provideFields(respNode);
		if(entryFields == null || entryFields.isEmpty()){
			return "";
		}
		StringBuilder fieldStrings = new StringBuilder();
		for (FieldModel entryFieldModel : entryFields) {
			ModelFieldBuilder fieldBuilder = new ModelFieldBuilder(sFieldTemplate, entryFieldModel);
			fieldStrings.append(fieldBuilder.build());
		}
		ModelBuilder modelBuilder = new ModelBuilder(sModelTemplate, className, fieldStrings.toString());
		return modelBuilder.build();
	}

	@Override
	public String getRelativeCodeDir() {
		return IOS_CODE_DIR;
	}

	@Override
	public String getCodeTemplate() {
		return sCodeTemplate;
	}
}
