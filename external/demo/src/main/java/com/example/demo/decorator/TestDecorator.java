package com.example.demo.decorator;

public class TestDecorator {
    public static void main(String[] args) {
        shape circle=new Circle();
        circle.draw();
        shape redCircle=new RedCircle(new Circle());
        redCircle.draw();
    }
}
