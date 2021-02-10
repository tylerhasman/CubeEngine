package me.cube.engine;

import me.cube.engine.editor.EditorGame;

public class StartEditor {

    public static void main(String[] args) {
        Window window = new Window(new EditorGame(), 1280, 720);

        window.run();
    }

}
