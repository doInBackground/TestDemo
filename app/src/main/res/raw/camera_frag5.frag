//<<FragmentShader:片元着色器->分屏滤镜>>
#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 aCoord;
uniform samplerExternalOES vTexture;

void main(){
    float x = aCoord.x;
    //双分屏.
//    if (x < 0.5) {
//        x += 0.25;
//    } else {
//        x -= 0.25;
//    }
    //三分屏.
    if (x < 1.0/3.0){
        x += 1.0/3.0;
    } else if (x > 2.0/3.0) {
        x -= 1.0/3.0;
    }
    gl_FragColor = texture2D(vTexture, vec2(x, aCoord.y));;
}