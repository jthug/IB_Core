package com.lianer.core.market.bean;

import java.util.List;

/**
 * 市场列表请求数据实体类
 *
 * @author allison
 */
public class MarketListRequest {

    private String type;
    private String sort;
    private List<String> token;
    private String offset;
    private String page;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<String> getToken() {
        return token;
    }

    public void setToken(List<String> token) {
        this.token = token;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "MarketListRequest{" +
                "type='" + type + '\'' +
                ", sort='" + sort + '\'' +
                ", token=" + token +
                ", offset='" + offset + '\'' +
                ", page='" + page + '\'' +
                '}';
    }
}
