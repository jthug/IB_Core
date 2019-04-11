package com.lianer.core.invest.model;

//{
//        "id":131,
//        "position":"1",
//        "weight":1,
//        "creator":"",
//        "createTime":"",
//        "imageCnUrl":"http://pgu36nd75.bkt.clouddn.com/%E4%BA%A7%E5%93%81%E4%BD%BF%E7%94%A8%E6%95%99%E7%A8%8B%20%E6%94%B91.png",
//        "imageEnUrl":"http://pgu36nd75.bkt.clouddn.com/%E4%BA%A7%E5%93%81%E4%BD%BF%E7%94%A8%E6%95%99%E7%A8%8B%20%E6%94%B91%20%E8%8B%B1%E6%96%87.png",
//        "status":"",
//        "pageCnUrl":"",
//        "pageEnUrl":""
//        }

/**
 * banner
 * @author allison
 */
public class BannerDelete {
    private int id;
    private String position;
    private int weight;
    private String creator;
    private String createTime;
    private String imageCnUrl;
    private String imageEnUrl;
    private String status;
    private String pageCnUrl;
    private String pageEnUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getImageCnUrl() {
        return imageCnUrl;
    }

    public void setImageCnUrl(String imageCnUrl) {
        this.imageCnUrl = imageCnUrl;
    }

    public String getImageEnUrl() {
        return imageEnUrl;
    }

    public void setImageEnUrl(String imageEnUrl) {
        this.imageEnUrl = imageEnUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPageCnUrl() {
        return pageCnUrl;
    }

    public void setPageCnUrl(String pageCnUrl) {
        this.pageCnUrl = pageCnUrl;
    }

    public String getPageEnUrl() {
        return pageEnUrl;
    }

    public void setPageEnUrl(String pageEnUrl) {
        this.pageEnUrl = pageEnUrl;
    }

    @Override
    public String toString() {
        return "BannerDelete{" +
                "id=" + id +
                ", position='" + position + '\'' +
                ", weight=" + weight +
                ", creator='" + creator + '\'' +
                ", createTime='" + createTime + '\'' +
                ", imageCnUrl='" + imageCnUrl + '\'' +
                ", imageEnUrl='" + imageEnUrl + '\'' +
                ", status='" + status + '\'' +
                ", pageCnUrl='" + pageCnUrl + '\'' +
                ", pageEnUrl='" + pageEnUrl + '\'' +
                '}';
    }
}
