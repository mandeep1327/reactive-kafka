package com.example.demo.factory;

public class TestFactory {
    private static NotificationsFactory notificationsFactory;

    public static void main(String[] args) {
        args = new String[]{"email", "push"};
        if (args[1].equalsIgnoreCase("email")) {
            notificationsFactory = new EmailNotificationsFactory();
        } else {
            notificationsFactory = new PushNotificationsFactory();
        }
        notificationsFactory.create();
    }

}