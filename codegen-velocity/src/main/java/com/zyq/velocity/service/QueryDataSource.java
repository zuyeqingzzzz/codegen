package com.zyq.velocity.service;

import com.zyq.velocity.domain.GenTable;

public interface QueryDataSource {

    void iniDateSource(String url, String userName, String passWord);

    GenTable queryDb(String tableName);

}
