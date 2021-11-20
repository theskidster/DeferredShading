package dev.theskidster.light.entity;

import static dev.theskidster.light.graphics.Color.WHITE;
import dev.theskidster.light.graphics.Graphics;
import dev.theskidster.light.graphics.Texture;
import dev.theskidster.light.main.App;
import dev.theskidster.shadercore.GLProgram;
import org.joml.Matrix3f;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import org.lwjgl.system.MemoryUtil;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EntityCube extends Entity {

    public float angleX;
    public float angleY;
    public float angleZ;
    
    private Graphics g;
    private Matrix3f normal = new Matrix3f();
    
    public EntityCube(float x, float y, float z, float width, float height, float depth) {
        super(x, y, z);
        
        float w = width / 2;
        float h = height / 2;
        float d = depth / 2;
        
        g = new Graphics();
        
        g.vertices = MemoryUtil.memAllocFloat(144);
        g.indices  = MemoryUtil.memAllocInt(36);
        
        //Front
        g.vertices.put(-w) .put(h).put(-d).put(0).put(0).put(-1);   //0
        g.vertices .put(w) .put(h).put(-d).put(0).put(0).put(-1);   //1
        g.vertices .put(w).put(-h).put(-d).put(0).put(0).put(-1);   //2
        g.vertices.put(-w).put(-h).put(-d).put(0).put(0).put(-1);   //3
        
        //Back
        g.vertices .put(w) .put(h).put(d).put(0).put(0).put(1);     //4
        g.vertices.put(-w) .put(h).put(d).put(0).put(0).put(1);     //5
        g.vertices.put(-w).put(-h).put(d).put(0).put(0).put(1);     //6
        g.vertices .put(w).put(-h).put(d).put(0).put(0).put(1);     //7
        
        //Top
        g.vertices.put(-w).put(h) .put(d).put(0).put(1).put(0);     //8
        g.vertices .put(w).put(h) .put(d).put(0).put(1).put(0);     //9
        g.vertices .put(w).put(h).put(-d).put(0).put(1).put(0);     //10
        g.vertices.put(-w).put(h).put(-d).put(0).put(1).put(0);     //11
        
        //Bottom
        g.vertices.put(-w).put(-h).put(-d).put(0).put(-1).put(0);   //12
        g.vertices .put(w).put(-h).put(-d).put(0).put(-1).put(0);   //13
        g.vertices .put(w).put(-h) .put(d).put(0).put(-1).put(0);   //14
        g.vertices.put(-w).put(-h) .put(d).put(0).put(-1).put(0);   //15
        
        //Left
        g.vertices.put(-w) .put(h) .put(d).put(-1).put(0).put(0);   //16
        g.vertices.put(-w) .put(h).put(-d).put(-1).put(0).put(0);   //17
        g.vertices.put(-w).put(-h).put(-d).put(-1).put(0).put(0);   //18
        g.vertices.put(-w).put(-h) .put(d).put(-1).put(0).put(0);   //19
        
        //Right
        g.vertices.put(w) .put(h).put(-d).put(1).put(0).put(0);     //20
        g.vertices.put(w) .put(h) .put(d).put(1).put(0).put(0);     //21
        g.vertices.put(w).put(-h) .put(d).put(1).put(0).put(0);     //22
        g.vertices.put(w).put(-h).put(-d).put(1).put(0).put(0);     //23
        
        g.indices.put(0).put(1).put(2).put(2).put(3).put(0);       //Front
        g.indices.put(4).put(5).put(6).put(6).put(7).put(4);       //Back
        g.indices.put(8).put(9).put(10).put(10).put(11).put(8);    //Top
        g.indices.put(12).put(13).put(14).put(14).put(15).put(12); //Bottom
        g.indices.put(16).put(17).put(18).put(18).put(19).put(16); //Left
        g.indices.put(20).put(21).put(22).put(22).put(23).put(20); //Right
        
        g.vertices.flip();
        g.indices.flip();
        
        g.bindBuffers();
        
        MemoryUtil.memFree(g.vertices);
        MemoryUtil.memFree(g.indices);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(3);
    }

    @Override
    public void update() {
        normal.set(g.modelMatrix.invert());
        
        g.modelMatrix.translation(position);
        g.modelMatrix.rotateX((float) Math.toRadians(angleX));
        g.modelMatrix.rotateY((float) Math.toRadians(angleY));
        g.modelMatrix.rotateZ((float) Math.toRadians(angleZ));
    }
    
    @Override
    public void render(GLProgram sceneProgram) {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glBindVertexArray(g.vao);
        
        sceneProgram.setUniform("uType", 1);
        sceneProgram.setUniform("uColor", WHITE.asVec3());
        sceneProgram.setUniform("uModel", false, g.modelMatrix);
        sceneProgram.setUniform("uNormal", true, normal);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        
        App.checkGLError();
    }

}
