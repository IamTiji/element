#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 Color;

layout (std140) uniform SharedUniform {
    mat4 projection;
};

out vec3 color;

void main()
{
    gl_Position = projection * vec4(aPos, 1.0);
    color = Color;
}