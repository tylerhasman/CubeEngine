#version 440

uniform vec3 u_AmbientLight;
//uniform vec3 u_LightPos0;

in vec3 v_Position;
in vec3 v_Color;
in vec3 v_Normal;

out vec4 out_Color;

void main(){



    out_Color = vec4(v_Color * u_AmbientLight, 1.0);
}