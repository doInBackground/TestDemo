//<<FragmentShader:片元着色器->旋转滤镜>>
#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 aCoord;
uniform samplerExternalOES vTexture;

void main(){
    //    vec4 rgba = texture2D(vTexture, vec2(1.0 - aCoord.y, aCoord.x));//逆时针旋转90度.
    //    vec4 rgba = texture2D(vTexture, vec2(aCoord.y, 1.0 - aCoord.x));//顺时针旋转90度.
    vec4 rgba = texture2D(vTexture, vec2(aCoord.y, aCoord.x));//顺时针旋转90度且镜像.
    gl_FragColor = vec4(rgba.r, rgba.g, rgba.b, rgba.a);
}