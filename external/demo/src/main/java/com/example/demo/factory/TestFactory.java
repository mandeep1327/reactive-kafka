package com.example.demo.factory;

import com.example.demo.factory.EmailNotificationsFactory;
import com.example.demo.factory.NotificationsFactory;
import com.example.demo.factory.PushNotificationsFactory;

public class TestFactory {
    private static NotificationsFactory notificationsFactory;

    public static void main(String[] args) {
		   args= new String[]{"email", "push"};
        if (args[1].equalsIgnoreCase("email")) {
            notificationsFactory = new EmailNotificationsFactory();
        } else {
            notificationsFactory = new PushNotificationsFactory();
        }
        notificationsFactory.create();
    }

}