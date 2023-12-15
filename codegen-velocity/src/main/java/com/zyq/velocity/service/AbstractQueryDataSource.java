package com.zyq.velocity.service;


public abstract class AbstractQueryDataSource implements QueryDataSource {

    protected String url;
    protected String userName;
    protected String passWord;

    @Override
    public void iniDateSource(String url, String userName, String passWord) {
        this.url = url;
        this.userName = userName;
        this.passWord = passWord;
    }

}
