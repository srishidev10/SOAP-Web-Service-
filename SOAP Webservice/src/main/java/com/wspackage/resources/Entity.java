/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wspackage.resources;

/**
 *
 * @author Dell
 */
public class Entity {
    private String accesssKey;
    private String secretKey;
    private String region;

    public String getAccesssKey() {
        return accesssKey;
    }

    public void setAccesssKey(String accesssKey) {
        this.accesssKey = accesssKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
