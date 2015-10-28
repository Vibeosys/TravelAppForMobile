package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/21/2015.
 */
public class TableDataDTO {
    String tableName;
    String tableData;

   public TableDataDTO(String tableName, String tableData) {
        this.tableName = tableName;
        this.tableData = tableData;
    }

   public TableDataDTO() {

    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        tableName = tableName;
    }

    public String getTableData() {
        return tableData;
    }

    public void setTableData(String tableData) {
        tableData = tableData;
    }
}