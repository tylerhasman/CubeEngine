#version 440

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Color;
layout (location = 2) in vec3 a_Normal;

out vec3 v_Color;
out vec3 v_Normal;
out vec3 v_Position;

uniform mat4 ModelMatrix;
uniform mat4 ViewMatrix;
uniform mat4 ProjectionMatrix;

void main(){
    v_Normal = mat3(ModelMatrix) * a_Normal;
    v_Position = vec3(ModelMatrix * vec4(a_Position, 1.0)) + vec3(sin(a_Position.x) * 10.0);
    v_Color = a_Color;

    gl_Position = ProjectionMatrix * ViewMatrix * ModelMatrix * vec4(a_Position, 1.0);
}