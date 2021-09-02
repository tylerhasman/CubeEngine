#version 440

in vec3 v_Position;

out vec4 out_Color;

void main(){
    out_Color = vec4(v_Position.xyz, 1.0);
}