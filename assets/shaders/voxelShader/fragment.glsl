

#version 440


in vec3 v_Color;

out vec4 out_Color;

void main(){

    out_Color = vec4(v_Color, 1.0);
}