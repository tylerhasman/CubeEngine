#version 440

layout (location = 0) out vec3 g_Position;
layout (location = 1) out vec3 g_Normal;
layout (location = 2) out vec4 g_Albedo;

in vec3 v_Position;
in vec3 v_Normal;
in vec4 v_Color;

void main(){

    g_Position = v_Position;
    g_Normal = normalize(v_Normal);

    g_Albedo = vec4(v_Color.rgb, 1.0);//Only supports opaque surfaces

}