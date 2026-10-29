attribute vec4 aPosition;
varying vec2 vTexCoord;
void main() {
  gl_Position = aPosition;
  vTexCoord = (aPosition.xy + 1.0) * 0.5;
}
