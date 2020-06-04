import me.cube.engine.file.VxmFile;
import org.junit.Test;

import java.io.IOException;

public class VxmLoadTest {


    @Test
    public void testLoad(){
        try {
            VxmFile vxmFile = new VxmFile("sword.vxm");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
