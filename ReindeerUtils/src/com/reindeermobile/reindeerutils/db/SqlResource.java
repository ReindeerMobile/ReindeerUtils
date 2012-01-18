package com.reindeermobile.reindeerutils.db;

public class SqlResource {
	private String createScript;
	private String dropScript;
	private String insertScript;
	private String alterScript;

	public SqlResource(String dropScript, String createScript, String insertScript, String alterScript) {
		super();
		this.createScript = createScript;
		this.dropScript = dropScript;
		this.insertScript = insertScript;
		this.alterScript = alterScript;
	}
	
	public final String getDropScriptByDbVersion(int dbVersion) {
		return this.dropScript;
	}

	public final String getCreateScript() {
		return this.createScript;
	}

	public final String getDropScript() {
		return this.dropScript;
	}

	public final String getInsertScript() {
		return this.insertScript;
	}

	public final void setCreateScript(String createScript) {
		this.createScript = createScript;
	}

	public final void setDropScript(String dropScript) {
		this.dropScript = dropScript;
	}

	public final void setInsertScript(String insertScript) {
		this.insertScript = insertScript;
	}

	public String getAlterScript() {
		return alterScript;
	}

	public void setAlterScript(String alterScript) {
		this.alterScript = alterScript;
	}

	@Override
	public String toString() {
		return "SqlResource [createScript=" + this.createScript + ", dropScript=" + this.dropScript + ", insertScript=" + this.insertScript + "]";
	}

}
