#version 440

uniform vec3 u_AmbientLight;
uniform vec3 u_LightDirection;
uniform vec3 u_LightColor;

in vec3 v_Position;
in vec4 v_Color;
in vec3 v_Normal;
in vec3 v_ViewPos;

out vec4 out_Color;

uniform vec3 DiffuseLight0_Position, DiffuseLight0_Color;
uniform float DiffuseLight0_Intensity;

uniform vec3 DiffuseLight1_Position, DiffuseLight1_Color;
uniform float DiffuseLight1_Intensity;

uniform vec3 u_Hue;

vec3 diffuse(vec3 position, vec3 color, float intensity){
    vec3 lightDir = normalize(position - v_Position);
    float diff = max(dot(normalize(v_Normal), lightDir), 0.0);

    return diff * vec3(v_Color) * color * intensity;
}

void main(){

    vec3 d0 = diffuse(DiffuseLight0_Position, DiffuseLight0_Color, DiffuseLight0_Intensity);
    vec3 d1 = diffuse(DiffuseLight1_Position, DiffuseLight1_Color, DiffuseLight1_Intensity);

    vec3 ambient = u_AmbientLight * vec3(v_Color) * u_Hue;

    out_Color = vec4(clamp(d0 + d1 + ambient, 0.0, 1.0), v_Color.a);
}