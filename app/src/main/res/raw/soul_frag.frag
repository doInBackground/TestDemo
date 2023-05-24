varying highp vec2 aCoord;

uniform sampler2D vTexture;
uniform highp float scalePercent;//缩放值,属于变动值,取值范围:1-2.
uniform lowp float mixturePercent;//透明度,属于变动值,取值范围:0-1.

void main() {
    //获取新采样坐标.
    highp vec2 textureCoordinateToUse = aCoord;//临时位置坐标,取自原像素位置坐标.
    highp vec2 center = vec2(0.5, 0.5);//纹理中心点.
    textureCoordinateToUse -= center;//临时位置坐标与中心点的偏移.
    textureCoordinateToUse = textureCoordinateToUse / scalePercent;//按比例缩小偏移量.
    textureCoordinateToUse += center;//新的临时位置坐标(比原像素位置更接近中心点).

    lowp vec4 textureColor2 = texture2D(vTexture, textureCoordinateToUse);//新的临时位置的颜色值.
    lowp vec4 textureColor = texture2D(vTexture, aCoord);//原像素位置的颜色值.

    //gl_FragColor  =  textureColor;
    gl_FragColor = mix(textureColor, textureColor2, mixturePercent);//混合新旧颜色值输出到原像素位置坐标.
}
