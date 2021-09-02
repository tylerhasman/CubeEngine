#version 440

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec3 a_Normal;
layout (location = 3) in vec2 a_TexCoord;

out vec2 v_TexCoord;

void main(){
    v_TexCoord = a_TexCoord;

    gl_Position = vec4(a_Position, 1.0);
}