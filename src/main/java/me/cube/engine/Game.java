package me.cube.engine;

public interface Game {

    void init();

    void update(float delta);

    void render();

    void onKeyPress(int key, int action);

    void onCursorMove(double dx, double dy);

    void onMouseScroll(double delta);

    void onMousePress(int button, int action);

    void destroy();
}
