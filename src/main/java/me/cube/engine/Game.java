package me.cube.engine;

public interface Game {

    void update(float delta);

    void render();

    void onKeyPress(int key, int action);

    void onCursorMove(double v, double v1);
}
