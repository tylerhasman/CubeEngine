#version 440

layout (location = 0) in vec3 a_Position;

out vec2 TexCoords;

uniform mat4 Frame;

void main(){
    TexCoords = vec2(a_Position + 0.5);

    gl_Position = Frame * vec4(a_Position, 1.0);
}