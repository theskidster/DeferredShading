package dev.theskidster.light.graphics;

import org.joml.Vector3f;

/**
 * Nov 18, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Light {

    public boolean enabled = true;
    
    public float brightness;
    public float contrast;
    
    public Vector3f position;
    public Color ambientColor;
    public Color diffuseColor;
    public Color specularColor;
    
    public Light(float brightness, float contrast, Vector3f position, Color ambientColor, Color diffuseColor, Color specularColor) {        
        this.brightness    = brightness;
        this.contrast      = clamp(0, 1, contrast);
        this.position      = position;
        this.ambientColor  = ambientColor;
        this.diffuseColor  = diffuseColor;
        this.specularColor = specularColor;
    }
    
    public Light(float brightness, float contrast, Vector3f position, Color color) {
        this(brightness, contrast, position, color, color, color);
    }
    
    private float clamp(float minValue, float maxValue, float userValue) {
        float result = 0;
        
        if(userValue > maxValue) {
            result = maxValue;
        } else if(userValue < minValue) {
            result = minValue;
        } else {
            result = userValue;
        }
        
        return result;
    }
    
    public static final Light daylight() {
        return new Light(1, 0.45f, new Vector3f(-10, 30, 7.5f), Color.WHITE);
    }
    
    public static final Light sunset() {
        return new Light(0.87f, 0.5f, new Vector3f(-8, 7.5f, -30), Color.create(173, 141, 162), Color.create(255, 204, 86), Color.WHITE);
    }
    
    public static final Light midnight() {
        return new Light(0.82f, 0.55f, new Vector3f(6.5f, 16.7f, 30), Color.create(38, 48, 76), Color.create(48, 62, 80), Color.WHITE);
    }
    
}