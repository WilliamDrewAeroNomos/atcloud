/**
 * 
 */
package com.atcloud.commons;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public enum ServiceEndPointArgumentNames {
	FEDERATE_NAME(
			"federateName"),
	FEDERATE_DESCRIPTION(
			"federateDescription"),
	FEM_NAME(
			"femName"),
	GROUP_NAME(
			"groupName"),
	FEM_DESCRIPTION(
			"femDescription"),
	FIRST_NAME(
			"firstName"),
	MIDDLE_NAME(
			"middleName"),
	LAST_NAME(
			"lastName"),
	GROUP_DESCRIPTION(
			"groupDescription"),
	FEM(
			"FEM");

	private String argName;

	/**
	 * 
	 * @param argName
	 */
	ServiceEndPointArgumentNames(final String argName) {
		this.setArgName(argName);
	}

	/**
	 * 
	 * @return
	 */
	public String getArgName() {
		return argName;
	}

	/**
	 * 
	 * @param argName
	 */
	public void setArgName(String argName) {
		this.argName = argName;
	}

}
