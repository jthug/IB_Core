package com.lianer.core.lauch.bean;

import com.lianer.core.base.BaseBean;

/**
 * 版本请求返回数据
 */
public class VersionResponse extends BaseBean{

    VersionBean data;

    public VersionBean getData() {
        return data;
    }

    public void setData(VersionBean data) {
        this.data = data;
    }
}
