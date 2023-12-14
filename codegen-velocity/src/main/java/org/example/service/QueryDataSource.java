package org.example.service;

import org.example.domain.GenTable;

public interface QueryDataSource {

    void iniDateSource(String url, String userName, String passWord);

    GenTable queryDb(String tableName);

}
