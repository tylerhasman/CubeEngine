package me.cube.engine;

import me.cube.engine.test.TestGame;

public class Start {

    public static void main(String[] args) {
        Window window = new Window(new TestGame(), "Test", 600, 480);

        window.run();
    }
}
