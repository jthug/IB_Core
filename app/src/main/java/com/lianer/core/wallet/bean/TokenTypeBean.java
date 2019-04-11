package com.lianer.core.wallet.bean;

/**
 * 资产-币种信息
 *
 * @author allison
 */
public class TokenTypeBean {

    private String tokenLogo;//图片logo地址
    private String tokenName;//代币名称
    private String tokenAmount;

    public TokenTypeBean() {
    }

    public TokenTypeBean(String tokenLogo, String tokenName, String tokenAmount) {
        this.tokenLogo = tokenLogo;
        this.tokenName = tokenName;
        this.tokenAmount = tokenAmount;
    }

    public String getTokenLogo() {
        return tokenLogo;
    }

    public void setTokenLogo(String tokenLogo) {
        this.tokenLogo = tokenLogo;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenAmount() {
        return tokenAmount;
    }

    public void setTokenAmount(String tokenAmount) {
        this.tokenAmount = tokenAmount;
    }
}
