precision highp float;
uniform sampler2D uTexSampler;
uniform vec2 uPixel;
uniform vec3 uLuma;
uniform float uEnabled;
uniform float uStyle;
uniform float uPreview;
varying vec2 vTexCoord;

vec4 outputColor(vec4 linearColor) {
  // Bitmap previews need display-referred RGB; the video graph applies its own transfer.
  return vec4(uPreview > 0.5 ? pow(clamp(linearColor.rgb, 0.0, 1.0), vec3(0.454545)) : linearColor.rgb, linearColor.a);
}

float lightAt(vec2 offset) {
  vec3 rgb = texture2D(uTexSampler, clamp(vTexCoord + offset * uPixel, 0.0, 1.0)).rgb;
  return pow(clamp(dot(rgb, uLuma), 0.0, 1.0), 0.454545);
}
void main() {
  vec4 source = texture2D(uTexSampler, vTexCoord);
  if (uEnabled < 0.5) { gl_FragColor = outputColor(source); return; }
  float a = lightAt(vec2(-1.5, -1.5));
  float b = lightAt(vec2( 0.0, -1.5));
  float c = lightAt(vec2( 1.5, -1.5));
  float d = lightAt(vec2(-1.5,  0.0));
  float e = lightAt(vec2( 0.0,  0.0));
  float f = lightAt(vec2( 1.5,  0.0));
  float g = lightAt(vec2(-1.5,  1.5));
  float h = lightAt(vec2( 0.0,  1.5));
  float i = lightAt(vec2( 1.5,  1.5));
  float gx = -a - 2.0*d - g + c + 2.0*f + i;
  float gy = -a - 2.0*b - c + g + 2.0*h + i;
  float edge = clamp(length(vec2(gx, gy)) * 1.4, 0.0, 0.96);
  float blur = (a + 2.0*b + c + 2.0*d + 4.0*e + 2.0*f + g + 2.0*h + i) / 16.0;
  float detail = clamp(e / (blur + 0.025), 0.0, 1.0);
  vec2 pixel = vTexCoord / uPixel;
  // Static paper grain and hatching: no time-based flicker in moving video.
  float grain = fract(sin(dot(floor(pixel), vec2(12.9898, 78.233))) * 43758.5453);
  float hatch = step(0.78, fract((pixel.x + pixel.y) / 6.0)) * (1.0 - e) * 0.15;
  vec3 result;
  if (uStyle < 1.5) {
    // Fine pencil: soft graphite, local detail and warm paper.
    float ink = clamp((1.0 - edge) * mix(0.65, 1.0, detail) - hatch, 0.035, 1.0);
    result = vec3(0.98, 0.965, 0.935) * ink * (0.97 + grain * 0.03);
  } else if (uStyle < 2.5) {
    // Charcoal: broad dark tonal masses with coarse, stable paper texture.
    float shade = pow(e, 0.72) * (1.0 - edge * 0.85);
    float charcoal = clamp(shade * (0.72 + grain * 0.28) - hatch * 1.5, 0.015, 0.92);
    result = vec3(charcoal);
  } else if (uStyle < 3.5) {
    // Ink hatch: two directional line families rather than smooth gray fills.
    float line1 = step(0.72, fract((pixel.x + pixel.y) / 5.0));
    float line2 = step(0.78, fract((pixel.x - pixel.y) / 6.0));
    float lines = max(line1 * step(e, 0.85), line2 * step(e, 0.48));
    float contour = smoothstep(0.12, 0.28, edge);
    result = vec3(1.0 - max(contour, lines) * 0.97);
  } else if (uStyle < 4.5) {
    // Color pencil: retain source color with fine strokes and lighter paper.
    vec3 color = pow(clamp(source.rgb, 0.0, 1.0), vec3(0.454545));
    color = floor(color * 14.0 + 0.5) / 14.0;
    result = mix(vec3(0.98, 0.96, 0.91), color, 0.86);
    result *= (0.94 + grain * 0.06) * (1.0 - edge * 0.65 - hatch * 0.45);
  } else if (uStyle < 5.5) {
    // White outline: keep flat regions black and draw only image structure.
    float outline = smoothstep(0.045, 0.35, edge + abs(e - blur) * 1.5);
    result = vec3(outline);
  } else {
    // Comic: flat color blocks and firm black contours, without paper grain.
    vec3 color = pow(clamp(source.rgb, 0.0, 1.0), vec3(0.454545));
    color = floor(color * 5.0 + 0.5) / 5.0;
    float contour = smoothstep(0.10, 0.30, edge);
    result = color * (1.0 - contour * 0.96);
  }
  gl_FragColor = outputColor(vec4(pow(clamp(result, 0.0, 1.0), vec3(2.2)), source.a));
}
