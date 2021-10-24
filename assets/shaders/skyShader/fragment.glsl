#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform vec3 zenithColor;
uniform vec3 horizonColor;

uniform mat4 ViewMatrix;

void main()
{

    vec3 eyeDirection = vec3(ViewMatrix * vec4(0.0, 0.0, 1.0, 0.0));

    float yaw = eyeDirection.y;

    vec3 color = mix(horizonColor, zenithColor, TexCoords.y);

    FragColor = vec4(color, 1.0);
}