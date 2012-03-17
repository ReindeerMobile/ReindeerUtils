package com.reindeermobile.reindeerutils.db;

import java.util.ArrayList;
import java.util.List;

public class DbListFilter {

	private List<FilterCondition> conditions;

	public class FilterCondition {
		private String columnName;
		private String operator;
		private String value;

		public FilterCondition(String columnName, String operator, String value) {
			super();
			this.columnName = columnName;
			this.operator = operator;
			this.value = value;
		}

	}

	public DbListFilter() {
		this.conditions = new ArrayList<DbListFilter.FilterCondition>();
	}

	public String getWhereClaus() {
		StringBuilder sb = new StringBuilder();
		
		for (FilterCondition condition : this.conditions) {
			sb = sb.append(condition.columnName).append(condition.operator).append(condition.value);
		}
		
		return sb.toString();
	}
}
