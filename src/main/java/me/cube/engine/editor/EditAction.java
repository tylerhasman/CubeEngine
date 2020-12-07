package me.cube.engine.editor;

import me.cube.engine.game.world.Terrain;

public interface EditAction {

    void execute(Terrain terrain);

    void undo(Terrain terrain);

}
