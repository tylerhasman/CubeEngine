#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2D gSSAO;
uniform sampler2D gDepth;

uniform sampler2D gTransparentAlbedo;
uniform sampler2D gTransparentDepth;

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
    float Depth = texture(gDepth, TexCoords).r;

    float tDepth = texture(gTransparentDepth, TexCoords).r;
    vec3 tAlbedo = texture(gTransparentAlbedo, TexCoords).rgb;
    float tAlpha = texture(gTransparentAlbedo, TexCoords).a;

    float alpha = tDepth < Depth ? tAlpha : 0.0;//Remove transparent color if its behind opaque thing

    vec3 ambient = vec3(0.8 * Albedo * AmbientOcclusion);

    ambient = mix(ambient, tAlbedo, alpha);

    // then calculate lighting as usual
    vec3 lighting = ambient;

    FragColor = vec4(lighting, 1.0);
}