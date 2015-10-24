package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/24/2015.
 */
public class Sync {
String UserId;
String JsonSync;
String TableName;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getJsonSync() {
        return JsonSync;
    }

    public void setJsonSync(String jsonSync) {
        JsonSync = jsonSync;
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String tableName) {
        TableName = tableName;
    }
}
