//<<FragmentShader:片元着色器->暖色滤镜>>
#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 aCoord;
uniform samplerExternalOES vTexture;

void main(){
    vec4 rgba = texture2D(vTexture, aCoord);
    //暖色滤镜思路: 红和绿加值,蓝和透明不变.
    gl_FragColor = rgba + vec4(0.2, 0.2, 0.0, 0.0);
}