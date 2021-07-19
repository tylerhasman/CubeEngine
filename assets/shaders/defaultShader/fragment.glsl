#version 440

uniform vec3 u_AmbientLight;
uniform vec3 u_LightDirection;
uniform vec3 u_LightColor;

in vec3 v_Position;
in vec3 v_Color;
in vec3 v_Normal;
in vec3 v_ViewPos;

out vec4 out_Color;

void main(){

    /*vec3 lightDir = u_LightDirection;

    float diff = max(dot(v_Normal, lightDir), 0.0);

    vec3 viewDir = normalize(v_ViewPos - v_Position);
    vec3 reflectDir = reflect(-u_LightDirection, v_Normal);

    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);

    vec3 specular = spec * u_LightColor;
    vec3 diffuse = diff * v_Color * u_LightColor;*/
    vec3 ambient = u_AmbientLight * v_Color;

    //out_Color = vec4(clamp(diffuse + ambient + specular, 0.0, 1.0), 1.0);
    out_Color = vec4(clamp(ambient, 0.0, 1.0), 1.0);
}