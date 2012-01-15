
package com.reindeermobile.mvpexample.entities;

import java.util.List;

public class ResultList {
    private List<Result> resultList;

    public ResultList(List<Result> resultList) {
        super();
        this.resultList = resultList;
    }

    public List<Result> getResultList() {
        return resultList;
    }

    public void setResultList(List<Result> resultList) {
        this.resultList = resultList;
    }
}
