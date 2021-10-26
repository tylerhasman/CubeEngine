package me.cube.editor;

import me.cube.game.world.Terrain;

public interface EditAction {

    void execute(Terrain terrain);

    void undo(Terrain terrain);

}
