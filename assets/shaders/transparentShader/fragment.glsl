#version 440

uniform vec3 u_AmbientLight;
uniform vec3 u_LightPos0;

in vec3 v_Position;
in vec3 v_Color;
in vec3 v_Normal;

out vec4 out_Color;

void main(){

    //Diffuse Lighting
    vec3 posToLightDir = normalize(u_LightPos0 - v_Position);
    vec3 diffuseColor = vec3(1.0, 1.0, 1.0);
    float diffuse = dot(v_Normal, posToLightDir);

    diffuseColor *= diffuse;

    vec3 modifier = clamp(u_AmbientLight + diffuseColor, 0, 1);

    out_Color = vec4(v_Color, 0.5);
}