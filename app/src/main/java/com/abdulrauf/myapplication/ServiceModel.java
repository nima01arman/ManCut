package com.abdulrauf.myapplication;


public class ServiceModel {
    private String id;
    private String serviceName;
    private int servicePrice;

    public ServiceModel() {} // Required for Firebase

    public ServiceModel(String id, String serviceName, int servicePrice) {
        this.id = id;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public int getServicePrice() { return servicePrice; }
    public void setServicePrice(int servicePrice) { this.servicePrice = servicePrice; }
}
