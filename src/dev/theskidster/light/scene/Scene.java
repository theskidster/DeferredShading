package dev.theskidster.light.scene;

import dev.theskidster.jlogger.JLogger;
import dev.theskidster.light.entity.Entity;
import dev.theskidster.light.graphics.Light;
import dev.theskidster.light.main.Camera;
import dev.theskidster.light.main.Window;
import dev.theskidster.shadercore.GLProgram;
import java.util.HashMap;
import org.joml.Vector3f;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public abstract class Scene {
    
    public static final int MAX_LIGHTS = 32;
    
    private int currLightIndex;
    private int numLights = 1;
    
    public final String name;
    private static Camera camera;
    private final Vector3f noValue = new Vector3f();
    
    public final HashMap<String, Entity> entities = new HashMap<>();
    private final LightSource[] lightSources      = new LightSource[MAX_LIGHTS];
    
    public Scene(String name) {
        this.name = name;
        lightSources[0] = new LightSource(true, Light.daylight());
    }
    
    public abstract void update();
    
    public abstract void render(GLProgram sceneProgram, Camera camera);    

    public abstract void exit();
    
    //TODO: the following methods will be package private in XJGE.
    public void setLightingUniforms(GLProgram sceneProgram) {
        for(int i = 0; i < Scene.MAX_LIGHTS; i++) {
            if(lightSources[i] != null) {
                if(lightSources[i].getEnabled()) {
                    sceneProgram.setUniform("uLights[" + i + "].brightness", lightSources[i].getBrightness());
                    sceneProgram.setUniform("uLights[" + i + "].contrast",   lightSources[i].getContrast());
                    sceneProgram.setUniform("uLights[" + i + "].position",   lightSources[i].getPosition());
                    sceneProgram.setUniform("uLights[" + i + "].ambient",    lightSources[i].getAmbientColor());
                    sceneProgram.setUniform("uLights[" + i + "].diffuse",    lightSources[i].getDiffuseColor());
                } else {
                    sceneProgram.setUniform("uLights[" + i + "].brightness", 0);
                    sceneProgram.setUniform("uLights[" + i + "].contrast",   0);
                    sceneProgram.setUniform("uLights[" + i + "].position",   noValue);
                    sceneProgram.setUniform("uLights[" + i + "].ambient",    noValue);
                    sceneProgram.setUniform("uLights[" + i + "].diffuse",    noValue);
                }
            }
        }
        sceneProgram.setUniform("uNumLights", numLights);
    }
    
    public void updateLightSources() {
        for(LightSource lightSource : lightSources) {
            if(lightSource != null) lightSource.update();
        }
    }
    
    public void renderLightSources(GLProgram sceneProgram, Camera camera) {
        for(LightSource lightSource : lightSources) {
            if(lightSource != null) lightSource.render(sceneProgram, camera.getPosition(), camera.getDirection(), camera.getUp());
        }
    }
    
    public static void setCameraReference(Camera reference) {
        camera = reference;
    }
    
    protected final void setCameraPosition(float x, float y, float z) {
        camera.setPosition(x, y, z);
    }
    
    protected final void setCameraDirection(float yaw, float pitch) {
        camera.setDirection(yaw, pitch, Window.getMouseX(), Window.getMouseY());
    }
    
    private void findNumLights() {
        numLights = 1;
        
        for(LightSource lightSource : lightSources) {
            if(lightSource != null) numLights++;
        }
    }
    
    protected final void addLight(Light light) {
        boolean search = true;
        
        for(int i = 1; search; i++) {
            if(i < MAX_LIGHTS) {
                if(lightSources[i] != null) {
                    if(!lightSources[i].getEnabled()) {
                        lightSources[i] = new LightSource(false, light, lightSources[i]);
                    }
                } else {
                    lightSources[i] = new LightSource(false, light);
                    search = false;
                }
            } else {
                currLightIndex = (currLightIndex == MAX_LIGHTS - 1) ? 1 : currLightIndex + 1;
                lightSources[currLightIndex] = new LightSource(false, light, lightSources[currLightIndex]);
                search = false;
            }
        }
        
        findNumLights();
    }
    
    protected final void addLightAtIndex(int index, Light light) {
        try {
            if(light == null) {
                throw new NullPointerException();
            } else {
                lightSources[index] = new LightSource(index == 0, light, lightSources[index]);
                findNumLights();
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            JLogger.setModule("core");
            JLogger.logWarning("Failed to add light object at index " + index, e);
            JLogger.setModule(null);
        }
    }
    
}