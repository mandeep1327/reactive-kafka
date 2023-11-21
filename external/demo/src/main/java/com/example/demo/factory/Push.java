package com.example.demo.factory;

import com.example.demo.factory.Notification;

public class Push implements Notification {

    @Override
    public void notifyUser() {
        System.out.println("push notification");
    }
}
