#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2D gSSAO;

uniform vec3 viewPos;

uniform vec3 u_AmbientLight;

void main()
{
    // retrieve data from G-buffer
    vec3 FragPos = texture(gPosition, TexCoords).rgb;
    vec3 Normal = texture(gNormal, TexCoords).rgb;
    vec3 Albedo = texture(gAlbedoSpec, TexCoords).rgb;
    float Specular = texture(gAlbedoSpec, TexCoords).a;
    float AmbientOcclusion = texture(gSSAO, TexCoords).r;

    // then calculate lighting as usual
    vec3 lighting = Albedo * u_AmbientLight * AmbientOcclusion; // hard-coded ambient component

    FragColor = vec4(lighting, 1.0);
}