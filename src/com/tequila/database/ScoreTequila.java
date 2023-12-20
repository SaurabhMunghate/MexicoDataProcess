package com.tequila.database;

public enum ScoreTequila {
	MAIN_TABLE("dataset"),
	UPDATED_TABLE("updatedDataset"),
	DELETED_TABLE("deletedDataset"),
	ALL_TABLE("_ALL_TABLE_");
	
	private String table = null;
	private ScoreTequila(String table) {
		this.table = table;
	}
	public String getTable(){
		return table;
	}
}
