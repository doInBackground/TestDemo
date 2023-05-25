precision mediump float;//设置float的精度为中等精度.
varying mediump vec2 aCoord;//当前坐标:当前要采集像素点的纹理坐标(x,y).
uniform sampler2D vTexture;//采样器.
vec2 blurCoordinates[20];//存放当前像素点周围20个点的坐标.
uniform int width;//Java传来的设备像素宽.
uniform int height;//Java传来的设备像素高.

void main(){
    //以下全部假设:屏幕像素是1000*1000,当前像素点的像素坐标是(500,500).

    //高斯模糊图:取当前点周围20个点的值作平均,得到高斯模糊图.

    //宽高方向上每个像素的在纹理坐标系上的单位步长:纹理坐标宽高都是1,再除以设备宽高,便是每个像素的单位步长.
    vec2 singleStepOffset = vec2(1.0/float(width), 1.0/float(height));//纹理宽高永远是1*1,则此处步长[1/1000,1/1000].

    //aCoord.xy:表示当前像素点的纹理坐标.
    //singleStepOffset:表示每个像素在纹理坐标系的单位步长.
    //vec2(0.0, -10.0):表示X轴不变,Y轴向下取10个像素.
    //假如此时当前像素坐标是(500,500),则纹理坐标是[0.5,0.5],下面函数为:[0.5,0.5]+[1/1000,1/1000]*[0,-10].
    blurCoordinates[0] = aCoord.xy + singleStepOffset* vec2(0.0, -10.0);//取(500,490)的纹理坐标.
    blurCoordinates[1] = aCoord.xy + singleStepOffset * vec2(0.0, 10.0);//取(500,510)的纹理坐标.
    blurCoordinates[2] = aCoord.xy + singleStepOffset * vec2(-10.0, 0.0);//取(490,500)的纹理坐标.
    blurCoordinates[3] = aCoord.xy + singleStepOffset * vec2(10.0, 0.0);//取(510,500)的纹理坐标.
    blurCoordinates[4] = aCoord.xy + singleStepOffset * vec2(5.0, -8.0);
    blurCoordinates[5] = aCoord.xy + singleStepOffset * vec2(5.0, 8.0);
    blurCoordinates[6] = aCoord.xy + singleStepOffset * vec2(-5.0, 8.0);
    blurCoordinates[7] = aCoord.xy + singleStepOffset * vec2(-5.0, -8.0);
    blurCoordinates[8] = aCoord.xy + singleStepOffset * vec2(8.0, -5.0);
    blurCoordinates[9] = aCoord.xy + singleStepOffset * vec2(8.0, 5.0);
    blurCoordinates[10] = aCoord.xy + singleStepOffset * vec2(-8.0, 5.0);
    blurCoordinates[11] = aCoord.xy + singleStepOffset * vec2(-8.0, -5.0);
    blurCoordinates[12] = aCoord.xy + singleStepOffset * vec2(0.0, -6.0);
    blurCoordinates[13] = aCoord.xy + singleStepOffset * vec2(0.0, 6.0);
    blurCoordinates[14] = aCoord.xy + singleStepOffset * vec2(6.0, 0.0);
    blurCoordinates[15] = aCoord.xy + singleStepOffset * vec2(-6.0, 0.0);
    blurCoordinates[16] = aCoord.xy + singleStepOffset * vec2(-4.0, -4.0);
    blurCoordinates[17] = aCoord.xy + singleStepOffset * vec2(-4.0, 4.0);
    blurCoordinates[18] = aCoord.xy + singleStepOffset * vec2(4.0, -4.0);
    blurCoordinates[19] = aCoord.xy + singleStepOffset * vec2(4.0, 4.0);
    //以上参考GPUImage/MagicCamera.

    vec4 currentColor = texture2D(vTexture, aCoord);//当前像素点的颜色值.
    vec3 rgb = currentColor.rgb;
    for (int i = 0; i < 20; i++) { //遍历周围20个点的坐标.
        rgb += texture2D(vTexture, blurCoordinates[i].xy).rgb;//累加.
    }
    vec4 blur = vec4(rgb*1.0/21.0, currentColor.a);//rgb取均值,a值取当前像素点的值.此时blur的数据,便是经过高斯模糊的数据.

    //至此,高斯模糊做完了.此例没有做人脸识别,仅通过反差图,将轮廓细节尽量保留,非轮廓的地方尽量模糊,来达到美颜的效果.

    //(1)得到反差图-highPassColor.
    vec4 highPassColor = currentColor - blur;//原值与模糊后的值相减,此时得到反差图.整图颜色值接近0,不同的地方才会大于0(整图黑色,不同的地方能看到轮廓).

    //(2)得到高反差图-highPassBlur.
    //高反差:经过高反差处理后,轮廓会更清晰.再将高反差后的值分布在0-1之间.
    highPassColor.r = clamp(2.0 * highPassColor.r * highPassColor.r * 24.0, 0.0, 1.0);
    highPassColor.g = clamp(2.0 * highPassColor.g * highPassColor.g * 24.0, 0.0, 1.0);
    highPassColor.b = clamp(2.0 * highPassColor.b * highPassColor.b * 24.0, 0.0, 1.0);
    vec4 highPassBlur = vec4(highPassColor.rgb, 1.0);//高反差图:整图黑色,但不同的地方的轮廓更清晰.

    //(3)得到高反差图的rgb最大值-maxChannelColor.
    float maxChannelColor = max(max(highPassBlur.r, highPassBlur.g), highPassBlur.b);//取高反差图rgb的最大值,用来保留细节.

    //(4)得到蓝色通道叠加值-value.
    //蓝色通道的细节明显,作为参考,调用叠加算法算出值,将该值分布在0-1之间.
    float b = min(currentColor.b, blur.b);//原图颜色与高斯模糊的颜色取最小值.
    float value = clamp((b - 0.2) * 5.0, 0.0, 1.0);//叠加算法:(b - 0.2) * 5.0.

    //(5)得到磨皮程度-intensity.
    float intensity = 1.0;//磨皮程度(0.0-1.0f),再大会很模糊.

    //(6)得到最终值:原图与高斯模糊图的占比系数-currentIntensity:细节(即轮廓)的地方值越小,黑色(即非轮廓)的地方值比较大.
    float currentIntensity = (1.0 - maxChannelColor / (maxChannelColor + 0.2)) * value * intensity;

    //(7)按动态比例(即占比系数)在原图和高斯模糊图中取值.
    //细节的地方应该多用原图,痘印的地方应该多用模糊图.
    //通过mix(Mat x,Mat y,Mat a)函数(算法:x*(1−a)+y*a),进行线性融合.
    //实例:[255,0,0]*(1−a)+[56,0,0]*a.
    //算法解析:如果a=0,表示完全去除高斯模糊图y,保留原图x.(细节越多的地方需要保留的越多,故currentIntensity越小,则原图currentColor便会保留的越多).
    vec3 result = mix(currentColor.rgb, blur.rgb, currentIntensity);

    gl_FragColor = vec4(result, 1.0);//赋值渲染.
}