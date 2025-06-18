#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 iTexCoord;
layout (location = 2) in vec3 color;

layout (std140) uniform SharedUniform {
    mat4 projection;
};

out vec2 TexCoord;
out vec3 Color;

void main()
{
    gl_Position = projection * vec4(aPos, 1.0);
    TexCoord = vec2(iTexCoord.x, iTexCoord.y);
    Color = color;
}