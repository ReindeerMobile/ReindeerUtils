
package com.reindeermobile.mvpexample.entities;

import java.util.List;

public class ResultList {
    private List<Result> resultList;

    public ResultList(final List<Result> resultList) {
        super();
        this.resultList = resultList;
    }

    public List<Result> getResultList() {
        return this.resultList;
    }

    public void setResultList(final List<Result> resultList) {
        this.resultList = resultList;
    }
}
