package me.cube.engine;

import org.lwjgl.opengl.GL;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.glActiveTexture;

public class FrameBuffer {

    private final int fbHandle, texHandle;

    /**
     * This will unbind the current framebuffer!!!
     */
    public FrameBuffer(int width, int height){
        this(width, height, GL_RGBA8, GL_RGBA);
    }

    public FrameBuffer(int width, int height, int internalFormat, int colorMode){
        if(Window.capabilities.GL_EXT_framebuffer_object){
            fbHandle = glGenFramebuffersEXT();
            texHandle = glGenTextures();

            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbHandle);

            glBindTexture(GL_TEXTURE_2D, texHandle);
            glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, colorMode, GL_INT, (ByteBuffer) null);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

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

    public void bindTexture(int unit){
        glActiveTexture(unit);
        glBindTexture(GL_TEXTURE_2D, texHandle);
    }

   public void unbind(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }


    public void dispose(){
        glDeleteTextures(texHandle);
        glDeleteFramebuffersEXT(fbHandle);
    }

}
