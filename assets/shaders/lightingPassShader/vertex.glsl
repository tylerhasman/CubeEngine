#version 440

layout (location = 0) in vec3 a_Position;

out vec2 TexCoords;

void main(){
    TexCoords = vec2(a_Position);

    gl_Position = vec4(a_Position, 1.0);
}