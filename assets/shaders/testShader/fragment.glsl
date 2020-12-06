#version 440

in vec3 v_Position;
in vec3 v_Color;
in vec3 v_Normal;

out vec4 out_Color;

void main(){
    out_Color = vec4(abs(v_Normal), 1.0);
}