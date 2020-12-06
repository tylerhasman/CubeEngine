package me.cube.engine;

import org.lwjgl.opengl.GL;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11C.*;

public class FrameBuffer {

    private final int fbHandle, texHandle;

    /**
     * This will unbind the current framebuffer!!!
     */
    private FrameBuffer(int width, int height){

        if(Window.capabilities.GL_EXT_framebuffer_object){
            fbHandle = glGenFramebuffersEXT();
            texHandle = glGenTextures();

            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbHandle);

            glBindTexture(GL_TEXTURE_2D, texHandle);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_INT, (ByteBuffer) null);

            glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texHandle, 0);


            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        }else{
            fbHandle = 0;
            texHandle = 0;
        }

    }

    public int getHandle(){
        return fbHandle;
    }

    public void bind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbHandle);
    }

    public void unbind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    public void dispose(){
        glDeleteTextures(texHandle);
        glDeleteFramebuffersEXT(fbHandle);
    }

}
