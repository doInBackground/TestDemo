attribute vec4 vPosition;//变量,float[4],一个顶点,java传过来的.
attribute vec2 vCoord;//纹理坐标.
varying vec2 aCoord;

void main(){
    gl_Position = vPosition;//内置变量:把坐标点赋值给gl_position就Ok了.
    aCoord = vCoord;
}