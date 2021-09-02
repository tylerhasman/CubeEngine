package me.cube.engine;

public abstract class Game {

    private Renderer renderer;

    public Game(){

    }

    public void resize(int width, int height){
        if(renderer != null)
            renderer.dispose();

        renderer = new Renderer(width, height);
    }

    public abstract void init();

    public abstract void update(float delta);

    public abstract void render(Renderer renderer);

    public abstract void onKeyPress(int key, int action);

    public abstract void onCursorMove(double dx, double dy);

    public abstract void onMouseScroll(double delta);

    public abstract void onMousePress(int button, int action);

    public abstract void destroy();

    public abstract String getTitle();


    public final void render(){

        render(renderer);


        renderer.renderScene();

    }

}
