#version 440

layout (location = 0) out vec4 g_Albedo;
layout (location = 1) out float g_Depth;

in vec3 v_Position;
in vec3 v_Normal;
in vec4 v_Color;
in float v_Depth;

uniform float u_Near;
uniform float u_Far;

void main(){

    g_Albedo = vec4(v_Color.rgba);//Only supports opaque surfaces

    g_Depth = v_Depth;

}