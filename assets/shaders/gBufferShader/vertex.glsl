#version 440

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec3 a_Normal;

out vec3 v_Position;
out vec3 v_Normal;
out vec4 v_Color;
out float v_Depth;

uniform mat4 ModelMatrix;
uniform mat4 ViewMatrix;
uniform mat4 ProjectionMatrix;
uniform mat3 NormalMatrix;

void main(){
    v_Normal = normalize(NormalMatrix * mat3(ViewMatrix) * a_Normal);
    v_Position = vec3(ViewMatrix * ModelMatrix * vec4(a_Position, 1.0));
    v_Color = a_Color;

    gl_Position = ProjectionMatrix * ViewMatrix * ModelMatrix * vec4(a_Position, 1.0);

    v_Depth = -(ViewMatrix * ModelMatrix * vec4(a_Position, 1.0)).z;
}