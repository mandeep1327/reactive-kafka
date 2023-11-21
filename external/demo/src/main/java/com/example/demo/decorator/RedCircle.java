package com.example.demo.decorator;

public class RedCircle extends ShapeDecorator{
    public RedCircle(shape shape) {
        super(shape);
    }

    @Override
    public void draw() {
        super.draw();
        setColour();
    }

    public void setColour(){
        System.out.println("Border is red:circle");
    }
}
