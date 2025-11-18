package br.com.menuux.comedoriadatia.Domain;

public class BannerDomain {
    private String id;
    private String url;

    public BannerDomain() {
    }

    public BannerDomain(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
