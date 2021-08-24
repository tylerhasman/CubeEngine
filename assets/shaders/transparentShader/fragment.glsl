#version 440

#define PI 3.14159265359

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

uniform vec3 u_CameraPosition;

vec3 diffuse(vec3 position, vec3 color, float intensity){
    vec3 lightDir = normalize(position - v_Position);
    float diff = max(dot(normalize(v_Normal), lightDir), 0.0);

    return diff * vec3(v_Color) * color * intensity;
}

void main(){

    vec3 d0 = diffuse(DiffuseLight0_Position, DiffuseLight0_Color, DiffuseLight0_Intensity);
    vec3 d1 = diffuse(DiffuseLight1_Position, DiffuseLight1_Color, DiffuseLight1_Intensity);

    vec3 ambient = u_AmbientLight * vec3(v_Color) * u_Hue;

    vec3 toCamera = u_CameraPosition - v_Position;

    //Calculate angle from this vector to the plane defined by (1, 0, 0) and (0, 0, 1)

    vec3 planeNormal = cross(vec3(1.0, 0.0, 0.0), vec3(0.0, 0.0, 1.0));

    float angle = acos(dot(toCamera, planeNormal) / (length(toCamera) * length(planeNormal)));

    float intensity = 1.0 - angle / PI;

    out_Color = vec4(clamp(d0 + d1 + ambient, 0.0, 1.0), mix(v_Color.a, 1.0, intensity));
}