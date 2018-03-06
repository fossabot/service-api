/*
 * This file is generated by jOOQ.
*/
package com.epam.ta.reportportal.store.jooq.tables.pojos;

import com.epam.ta.reportportal.store.jooq.enums.JUserRoleEnum;
import com.epam.ta.reportportal.store.jooq.enums.JUserTypeEnum;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * This class is generated by jOOQ.
 */
@Generated(value = { "http://www.jooq.org", "jOOQ version:3.10.5" }, comments = "This class is generated by jOOQ")
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JUsers implements Serializable {

	private static final long serialVersionUID = 1932967092;

	private Integer id;
	private String login;
	private String password;
	private String email;
	private JUserRoleEnum role;
	private JUserTypeEnum type;
	private Integer defaultProjectId;
	private String fullName;
	private Object metadata;

	public JUsers() {
	}

	public JUsers(JUsers value) {
		this.id = value.id;
		this.login = value.login;
		this.password = value.password;
		this.email = value.email;
		this.role = value.role;
		this.type = value.type;
		this.defaultProjectId = value.defaultProjectId;
		this.fullName = value.fullName;
		this.metadata = value.metadata;
	}

	public JUsers(Integer id, String login, String password, String email, JUserRoleEnum role, JUserTypeEnum type, Integer defaultProjectId,
			String fullName, Object metadata) {
		this.id = id;
		this.login = login;
		this.password = password;
		this.email = email;
		this.role = role;
		this.type = type;
		this.defaultProjectId = defaultProjectId;
		this.fullName = fullName;
		this.metadata = metadata;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public JUserRoleEnum getRole() {
		return this.role;
	}

	public void setRole(JUserRoleEnum role) {
		this.role = role;
	}

	public JUserTypeEnum getType() {
		return this.type;
	}

	public void setType(JUserTypeEnum type) {
		this.type = type;
	}

	public Integer getDefaultProjectId() {
		return this.defaultProjectId;
	}

	public void setDefaultProjectId(Integer defaultProjectId) {
		this.defaultProjectId = defaultProjectId;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
	 */
	@java.lang.Deprecated
	public Object getMetadata() {
		return this.metadata;
	}

	/**
	 * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
	 */
	@java.lang.Deprecated
	public void setMetadata(Object metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("JUsers (");

		sb.append(id);
		sb.append(", ").append(login);
		sb.append(", ").append(password);
		sb.append(", ").append(email);
		sb.append(", ").append(role);
		sb.append(", ").append(type);
		sb.append(", ").append(defaultProjectId);
		sb.append(", ").append(fullName);
		sb.append(", ").append(metadata);

		sb.append(")");
		return sb.toString();
	}
}