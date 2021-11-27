package dev.theskidster.light.main;

import dev.theskidster.light.graphics.Graphics;
import dev.theskidster.shadercore.GLProgram;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * Nov 26, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
class Viewport {

    final int texHandle;
    final Graphics g;
    
    Viewport(int width, int height) {
        texHandle = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, texHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(0)    .put(height).put(0)    .put(1).put(1);
            g.vertices.put(width).put(height).put(0)    .put(0).put(1);
            g.vertices.put(width).put(0)     .put(0)    .put(0).put(0);
            g.vertices.put(0)    .put(0)     .put(0)    .put(1).put(0);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(3).put(2).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    void render(GLProgram sceneProgram) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texHandle);
        glBindVertexArray(g.vao);
        
        sceneProgram.setUniform("uType", 4);
        sceneProgram.setUniform("uTexture", 0);

        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        
        App.checkGLError();
    }
    
}