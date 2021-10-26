#version 440

layout (location = 0) out vec3 g_Position;
layout (location = 1) out vec3 g_Normal;
layout (location = 2) out vec4 g_Albedo;
layout (location = 3) out float g_Depth;

in vec3 v_Position;
in vec3 v_Normal;
in vec4 v_Color;
in float v_Depth;

uniform float u_Near;
uniform float u_Far;

void main(){

    g_Position = v_Position;
    g_Normal = normalize(v_Normal);

    g_Albedo = vec4(v_Color.rgb, 1.0);//Only supports opaque surfaces

    g_Depth = v_Depth;

}