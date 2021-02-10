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

    private int indices;
    private int vertexHandle, colorHandle, normalHandle;
    private boolean initialized = false;

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

    protected Mesh(int mode){
        this.mode = mode;
    }

    public Mesh(int mode, FloatArray vertices, FloatArray colors, FloatArray normals){
        this(mode, vertices.toArray(), colors.toArray(), normals.toArray());
    }

    public Mesh(int mode, float[] vertexBufferData, float[] colorBufferData, float[] normalBufferData) {
        this(mode);
        initialize(vertexBufferData, colorBufferData, normalBufferData);
    }

    protected void initialize(float[] vertexBufferData, float[] colorBufferData, float[] normalBufferData){

        if(initialized)
            throw new IllegalStateException("Mesh already initialized.");

        initialized = true;

        vertexHandle = glGenBuffers();
        colorHandle = glGenBuffers();
        normalHandle = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexBufferData, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, colorHandle);
        glBufferData(GL_ARRAY_BUFFER, colorBufferData, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, normalHandle);
        glBufferData(GL_ARRAY_BUFFER, normalBufferData, GL_STATIC_DRAW);

        indices = vertexBufferData.length;
    }

    public void dispose(){
        glDeleteBuffers(new int[] {vertexHandle, colorHandle, normalHandle});
    }

    public void render(){

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

            GL11.glDrawArrays(mode, 0, indices);

            glDisableClientState(GL_VERTEX_ARRAY);
            glDisableClientState(GL_COLOR_ARRAY);
            glDisableClientState(GL_NORMAL_ARRAY);
        }

    }


}
