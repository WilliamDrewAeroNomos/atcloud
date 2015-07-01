package com.atcloud.persistence.internal;

import com.atcloud.model.ATCloudDataModelException;
import com.atcloud.model.SchemaFactorySourceLocator;

/**
 * Custom schema locator for unit testing
 * 
 */
public class PersistenceTestingSchemaFactorySourceLocator implements
		SchemaFactorySourceLocator {

	private String schemaDir = "";

	public PersistenceTestingSchemaFactorySourceLocator() {
		schemaDir = System.getenv("ATCLOUD_SCHEMA_DIR");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.model.SchemaFactorySourceLocator#getURL(java.lang.String)
	 */
	@Override
	public String getURL(String sourceName) throws ATCloudDataModelException {
		return schemaDir + "/" + sourceName;
	}

}