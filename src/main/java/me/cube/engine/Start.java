package me.cube.engine;

import me.cube.engine.game.CubeGame;

public class Start {

    public static void main(String[] args) {
        Window window = new Window(new CubeGame(), 1280, 720);

        window.run();
    }
}
