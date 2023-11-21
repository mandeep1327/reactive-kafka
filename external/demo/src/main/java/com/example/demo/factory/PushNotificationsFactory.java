package com.example.demo.factory;

import com.example.demo.factory.Notification;
import com.example.demo.factory.NotificationsFactory;
import com.example.demo.factory.Push;

public class PushNotificationsFactory extends NotificationsFactory {
    @Override
    protected Notification createNotification()
    {
        return new Push();
    }
}