import me.cube.game.world.Chunk;
import org.junit.Test;

import static org.junit.Assert.*;

public class TerrainTest {

    @Test
    public void worldToChunkTest(){
        assertEquals(Chunk.worldToChunk(Chunk.CHUNK_WIDTH), 1);
        assertEquals(Chunk.worldToChunk(-Chunk.CHUNK_WIDTH), -1);
        assertEquals(Chunk.worldToChunk(5), 0);
        assertEquals(Chunk.worldToChunk(0), 0);
        assertEquals(Chunk.worldToChunk(-1), -1);
        assertEquals(Chunk.worldToChunk(-5), -1);
    }

    @Test
    public void worldToChunkTest2(){

    }

}
