#version 440

uniform vec3 u_AmbientLight;
uniform vec3 u_LightDirection;
uniform vec3 u_LightColor;

in vec3 v_Position;
in vec3 v_Color;
in vec3 v_Normal;

out vec4 out_Color;

void main(){

    vec3 lightDir = u_LightDirection;

    float diff = max(dot(v_Normal, lightDir), 0.0);

    vec3 diffuse = diff * u_LightColor;

    vec3 multiplier = min(u_AmbientLight + diffuse, 1.0);

    out_Color = vec4(v_Color * multiplier, 1.0);
}