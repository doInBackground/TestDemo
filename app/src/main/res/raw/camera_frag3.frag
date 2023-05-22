//<<FragmentShader:片元着色器->冷色滤镜>>
#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 aCoord;
uniform samplerExternalOES vTexture;

void main(){
    vec4 rgba = texture2D(vTexture, aCoord);
    //冷色滤镜思路: 蓝加值,红和绿和透明不变.
    gl_FragColor = rgba + vec4(0.0, 0.0, 0.3, 0.0);
}