#version 330 core

in vec2 TexCoord;
out vec4 fragColor;

uniform sampler2D texture_;

void main() {
    fragColor = texture(texture_, TexCoord);
}