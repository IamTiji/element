#version 330 core

#define THRESHOLD 0.37

in vec2 TexCoord;
in vec3 Color;

out vec4 fragColor;

uniform sampler2D texture_;

float median(float a, float b, float c) {
    return max(min(a, b), min(max(a, b), c));
}

void main() {
    vec4 textureColor = texture(texture_, TexCoord);
    float r = textureColor.r;
    float g = textureColor.g;
    float b = textureColor.b;

    float sd = median(r, g, b);
    float w = fwidth(sd);
    float opacity = smoothstep(THRESHOLD-w, THRESHOLD+w, sd);

    if (opacity < 0.01) discard;

    fragColor = vec4(Color, opacity);
}