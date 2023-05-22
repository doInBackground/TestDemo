//<<FragmentShader:片元着色器->灰色滤镜>>
#extension GL_OES_EGL_image_external: require

precision mediump float;

varying vec2 aCoord;
uniform samplerExternalOES vTexture;

void main() {
    vec4 rgba = texture2D(vTexture, aCoord);
    float color = (rgba.r + rgba.g + rgba.b) / 3.0;//灰色滤镜.
    vec4 tempColor = vec4(color, color, color, 1);
    gl_FragColor = tempColor;
}