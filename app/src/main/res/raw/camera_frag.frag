//<<FragmentShader:片元着色器>>
#extension GL_OES_EGL_image_external: require//必须写的固定的, 意思要用采样器.

precision mediump float;

varying vec2 aCoord;//像素点,纹理坐标(x,y).用易变变量,从"顶点着色器"传值给"片元着色器"的同名变量.
uniform samplerExternalOES vTexture;//采样器.

void main() {
    vec4 rgba = texture2D(vTexture, aCoord);//OpenGL自带函数.
    gl_FragColor = vec4(rgba.r, rgba.g, rgba.b, rgba.a);//为内置变量赋值.
}