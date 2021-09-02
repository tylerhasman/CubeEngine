#version 440

out float FragColor;

in vec2 v_TexCoord;

uniform sampler2D u_PositionTex;
uniform sampler2D u_NormalTex;
uniform sampler2D u_NoiseTex;

uniform mat4 ProjectionMatrix;

uniform vec3 samples[64];

const vec2 noiseScale = vec2(1280.0 / 4.0, 720.0 / 4.0);//Screen width/height divied by noise width/height
const int kernalSize = 64;
const float radius = 0.5;

const float bias = 0.025;

void main(){
    vec3 fragPos = texture(u_PositionTex, v_TexCoord).rgb;
    vec3 normal = texture(u_NormalTex, v_TexCoord).rgb;
    vec3 randomVec = texture(u_NormalTex, v_TexCoord * noiseScale).rgb;

    vec3 tangent = normalize(randomVec - normal * dot(randomVec, normal));
    vec3 bitangent = cross(normal, tangent);
    mat3 TBN = mat3(tangent, bitangent, normal);

    float occlusion = 0.0;

    for(int i = 0; i < kernalSize; ++i){
        vec3 samplePos = TBN * samples[i];
        samplePos = fragPos + samplePos * radius;

        vec4 offset = vec4(samplePos, 1.0);
        offset = ProjectionMatrix * offset;
        offset.xyz /= offset.w;
        offset.xyz = offset.xyz * 0.5 + 0.5;

        float sampleDepth = texture(u_PositionTex, offset.xy).z;

        float rangeCheck = smoothstep(0.0, 1.0, radius / abs(fragPos.z - sampleDepth));

        occlusion += ((sampleDepth >= samplePos.z + bias) ? 1.0 : 0.0) * rangeCheck;
    }

    occlusion = 1.0 - (occlusion / kernalSize);
    FragColor = occlusion;

}