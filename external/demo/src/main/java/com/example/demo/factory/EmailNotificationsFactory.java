package com.example.demo.factory;

public class EmailNotificationsFactory extends NotificationsFactory {
    @Override
    protected Notification createNotification() {
        return new Email();
    }
}