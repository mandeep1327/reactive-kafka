package com.example.demo.factory;

public class Email implements Notification {

    @Override
    public void notifyUser() {
        System.out.println("email");
    }
}
