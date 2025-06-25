#version 330 core

#define clamp(x, a, b) min(max(x, a), b)
#define BLUR_RADIUS 5

in vec2 TexCoord;

out vec4 fragColor;

uniform sampler2D texture_;
uniform sampler2D id;

uniform int width;
uniform int height;
uniform int blurDir;

void main() {
    vec4 main_color = texture(texture_, TexCoord);
    float current_id = texture(id, TexCoord).r;

    float NormX = 1 / float(width);
    float NormY = 1 / float(height);
    vec2 dir = vec2(blurDir == 0 ? NormX : 0, blurDir != 0 ? NormY : 0);
    vec2 pos = TexCoord + dir * -BLUR_RADIUS;
    vec4 sumColor = vec4(0, 0, 0, 0);

    int colorCount = 0;
    for (int i = 0; i < 2 * BLUR_RADIUS + 1; i++) {
        pos = pos + dir;
        vec4 color = texture(texture_, pos);
        if (color.a <= 0) {
            continue;
        }

        color.a = color.a * 1.5;
        sumColor += color * color.a;
        colorCount++;
    }

    sumColor.a = sumColor.a / (BLUR_RADIUS * 2 + 1);
    if (colorCount == 0) {
        fragColor = vec4(0);
    } else {
        fragColor = vec4((sumColor / colorCount * sumColor.a).rgb, smoothstep(0.0, 0.5, sumColor.a));
    }
}
