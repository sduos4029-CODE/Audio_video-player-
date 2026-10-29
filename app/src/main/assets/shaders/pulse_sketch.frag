precision highp float;
uniform sampler2D uTexSampler;
uniform vec2 uPixel;
uniform vec3 uLuma;
uniform float uEnabled;
varying vec2 vTexCoord;

float lightAt(vec2 offset) {
  vec3 rgb = texture2D(uTexSampler, clamp(vTexCoord + offset * uPixel, 0.0, 1.0)).rgb;
  return pow(clamp(dot(rgb, uLuma), 0.0, 1.0), 0.454545);
}
void main() {
  vec4 source = texture2D(uTexSampler, vTexCoord);
  if (uEnabled < 0.5) { gl_FragColor = source; return; }
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
  float ink = clamp((1.0 - edge) * mix(0.65, 1.0, detail) - hatch, 0.035, 1.0);
  vec3 paper = vec3(0.98, 0.965, 0.935) * ink * (0.97 + grain * 0.03);
  gl_FragColor = vec4(pow(paper, vec3(2.2)), source.a);
}
