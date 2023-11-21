package com.example.demo.factory;

import com.example.demo.factory.Notification;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class NotificationsFactory {
    public Notification create(){
        Notification notification = createNotification();
        notification.notifyUser();
        return notification;
    }
    protected abstract Notification createNotification();
}
