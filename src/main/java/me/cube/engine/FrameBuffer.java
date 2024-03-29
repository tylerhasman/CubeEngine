package me.cube.engine;

import org.lwjgl.opengl.GL11;

import java.util.Arrays;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30C.*;

public class FrameBuffer {

    private final int handle;

    private int[] textureObjects;
    private int textureObjectIndex;

    private int[] depthBuffers;

    private int[] attachements;

    /**
     * This will unbind the current framebuffer!!!
     */
    public FrameBuffer(){
        textureObjects = new int[0];
        attachements = new int[0];
        depthBuffers = new int[0];
        textureObjectIndex = 0;
        handle = glGenFramebuffersEXT();
    }

    public void createTexture(int width, int height, int internalFormat, int format, int attachment){

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, handle);

        textureObjects = Arrays.copyOf(textureObjects, textureObjects.length+1);
        attachements = Arrays.copyOf(attachements, attachements.length+1);
        depthBuffers = Arrays.copyOf(depthBuffers, attachements.length+1);

        int index = textureObjectIndex++;

        int texId = glGenTextures();

        textureObjects[index] = texId;
        attachements[index] = attachment;

        glBindTexture(GL_TEXTURE_2D, texId);

        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_FLOAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, attachment, GL_TEXTURE_2D, texId, 0);

        depthBuffers[index] = glGenRenderbuffersEXT();
        glBindRenderbufferEXT(GL_RENDERBUFFER, depthBuffers[index]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffers[index]);

    }

    public int getHandle(){
        return handle;
    }

    public void bindTexture(int index, int unit){
        glActiveTexture(unit);
        glBindTexture(GL11.GL_TEXTURE_2D, textureObjects[index]);
    }

    public void bind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, handle);
        glDrawBuffers(attachements);
    }

    public void unbind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    public void dispose(){
        glDeleteRenderbuffersEXT(depthBuffers);
        glDeleteTextures(textureObjects);
        glDeleteFramebuffersEXT(handle);
    }

}
