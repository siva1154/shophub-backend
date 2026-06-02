package com.example.demo.dto;

public class NotificationSettingsRequest {

    private Boolean orderNotifications;
    private Boolean promotionalEmails;

    public Boolean getOrderNotifications() {
        return orderNotifications;
    }

    public void setOrderNotifications(Boolean orderNotifications) {
        this.orderNotifications = orderNotifications;
    }

    public Boolean getPromotionalEmails() {
        return promotionalEmails;
    }

    public void setPromotionalEmails(Boolean promotionalEmails) {
        this.promotionalEmails = promotionalEmails;
    }
}