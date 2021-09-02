package me.cube.editor;

import me.cube.editor.EditorGame;
import me.cube.engine.Window;

public class StartEditor {

    public static void main(String[] args) {
        Window window = new Window(new EditorGame(), 1920, 1080);

        window.run();
    }

}
