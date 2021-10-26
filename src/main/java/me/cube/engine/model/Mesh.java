package me.cube.engine.model;

import me.cube.engine.util.FloatArray;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;

public class Mesh {

    /**
     *         GL_POINTS         = 0x0
     *         GL_LINES          = 0x1
     *         GL_LINE_LOOP      = 0x2
     *         GL_LINE_STRIP     = 0x3
     *         GL_TRIANGLES      = 0x4
     *         GL_TRIANGLE_STRIP = 0x5
     *         GL_TRIANGLE_FAN   = 0x6
     *         GL_QUADS          = 0x7
     *         GL_QUAD_STRIP     = 0x8
     *         GL_POLYGON        = 0x9
     */
    private final int mode;

    private int indices;
    private int vertexHandle, colorHandle, normalHandle, texCoordHandle;
    private boolean initialized = false, disposed = false;

    protected Mesh(int mode){
        if(mode != GL11.GL_QUADS){
            throw new IllegalArgumentException(mode+" is not implemented");
        }
        this.mode = mode;
    }

    public Mesh(int mode, FloatArray vertices, FloatArray colors, FloatArray normals){
        this(mode, vertices.toArray(), colors.toArray(), normals.toArray());
    }

    public Mesh(int mode, float[] vertexBufferData, float[] colorBufferData, float[] normalBufferData, float[] textureCoords) {
        this(mode);
        initialize(vertexBufferData, colorBufferData, normalBufferData, textureCoords);
    }

    public Mesh(int mode, float[] vertexBufferData, float[] colorBufferData, float[] normalBufferData) {
        this(mode);
        initialize(vertexBufferData, colorBufferData, normalBufferData);
    }

    protected void initialize(float[] vertexBufferData, float[] colorBufferData, float[] normalBufferData){
        initialize(vertexBufferData, colorBufferData, normalBufferData, null);
    }

    protected void initialize(float[] vertexBufferData, float[] colorBufferData, float[] normalBufferData, float[] textureData){

        if(initialized)
            throw new IllegalStateException("Mesh already initialized.");

        int quads = vertexBufferData.length / 3;
        int colors = colorBufferData.length / 4;
        int normals = normalBufferData.length / 3;

        if(quads != colors || quads != normals){
            throw new IllegalStateException(quads+" "+colors+" "+normals);
        }

        initialized = true;

        vertexHandle = glGenBuffers();
        colorHandle = glGenBuffers();
        normalHandle = glGenBuffers();
        texCoordHandle = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexBufferData, GL_STATIC_DRAW);

        int[] size = new int[1];

        glGetBufferParameteriv(GL_ARRAY_BUFFER, GL_BUFFER_SIZE, size);

        if(size[0] != vertexBufferData.length * 4){
            throw new IllegalStateException("Buffer size != expected size. "+size[0]+" != "+(vertexBufferData.length * 4));
        }

        glBindBuffer(GL_ARRAY_BUFFER, colorHandle);
        glBufferData(GL_ARRAY_BUFFER, colorBufferData, GL_STATIC_DRAW);

        glGetBufferParameteriv(GL_ARRAY_BUFFER, GL_BUFFER_SIZE, size);

        if(size[0] != colorBufferData.length * 4){
            throw new IllegalStateException("Buffer size != expected size. "+size[0]+" != "+(colorBufferData.length * 4));
        }

        glBindBuffer(GL_ARRAY_BUFFER, normalHandle);
        glBufferData(GL_ARRAY_BUFFER, normalBufferData, GL_STATIC_DRAW);

        glGetBufferParameteriv(GL_ARRAY_BUFFER, GL_BUFFER_SIZE, size);

        if(size[0] != normalBufferData.length * 4){
            throw new IllegalStateException("Buffer size != expected size. "+size[0]+" != "+(normalBufferData.length * 4));
        }

        if(textureData != null){
            glBindBuffer(GL_ARRAY_BUFFER, texCoordHandle);
            glBufferData(GL_ARRAY_BUFFER, textureData, GL_STATIC_DRAW);

            glGetBufferParameteriv(GL_ARRAY_BUFFER, GL_BUFFER_SIZE, size);

            if(size[0] != textureData.length * 4){
                throw new IllegalStateException("Buffer size != expected size. "+size[0]+" != "+(normalBufferData.length * 4));
            }
        }

        indices = vertexBufferData.length / 3 * 4;
    }

    public void dispose(){
        if(!disposed){
            glDeleteBuffers(new int[] {vertexHandle, colorHandle, normalHandle, texCoordHandle});
            disposed = true;
        }else{
            throw new IllegalStateException("Mesh has already been disposed");
        }
    }

    public void render(){

        if(disposed){
            throw new IllegalStateException("Mesh cannot be rendered anymore.");
        }

        if(initialized){
            glEnableClientState(GL_VERTEX_ARRAY);
            glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            glEnableClientState(GL_COLOR_ARRAY);
            glBindBuffer(GL_ARRAY_BUFFER, colorHandle);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

            glEnableClientState(GL_NORMAL_ARRAY);
            glBindBuffer(GL_ARRAY_BUFFER, normalHandle);

            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            glBindBuffer(GL_ARRAY_BUFFER, texCoordHandle);

            glEnableVertexAttribArray(3);
            glVertexAttribPointer(3, 2, GL_FLOAT, false, 0, 0);

            GL11.glDrawArrays(mode, 0, indices);

            glDisableClientState(GL_VERTEX_ARRAY);
            glDisableClientState(GL_COLOR_ARRAY);
            glDisableClientState(GL_NORMAL_ARRAY);
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        }

    }


}
