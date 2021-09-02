package me.cube.test;

import me.cube.engine.Window;

public class StartTest {

    public static void main(String[] args) {
        Window window = new Window(new TestGame(), 800, 640);

        window.run();
    }

}
