#version 440

in vec3 v_Normal;
out vec4 out_Color;

void main(){

    vec3 adjusted = (v_Normal + 1.0) / 2.0;

    out_Color = vec4(adjusted, 1.0);
}