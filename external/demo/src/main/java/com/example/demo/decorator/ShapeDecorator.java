package com.example.demo.decorator;

public abstract class ShapeDecorator implements shape{
    protected shape shape;

    public ShapeDecorator(shape shape) {
        this.shape = shape;
    }
    public void draw(){
        shape.draw();
    }
}
