//<<VertexShader:顶点着色器>>

//"varying"声明的变量,表示可变变量(易变变量),在vsh和fsh声明同名的该类型变量后,一边赋值改变另一半也会变.
//"attribute"声明的变量,表示要从Java传来的变量.
//"uniform"声明的变量,表示要从Java传来但不能被着色器程序修改的变量(只能用,不能改). 通过函数glUniform**()函数赋值的.
//"uniform"变量一般用来表示: 变换矩阵,材质,光照参数和颜色等信息.
//如果"uniform"变量在顶点和片元两者之间声明方式完全一样,则它可以在vertex和fragment共享使用.（相当于一个被vertex和fragment共享的全局变量）

precision mediump float;//"precision"表示设置某个类型的精度(高highp/中mediump/低lowp). 此处表示所有"float"使用"mediump"精度.

attribute vec4 vPosition;//从java端传来的float[4]类型变量,表示一个顶点的坐标(x,y,z,w). 把顶点坐标给这个变量,确定要画的形状.
attribute vec4 vCoord;//接收纹理坐标(x,y),接收采样器采样图片的坐标.根据vPosition形状对应的从图片上采集像素的位置. CPU Camera
uniform mat4 vMatrix;//OpenGL和Camera坐标系不一样,需要矩阵变换.
varying vec2 aCoord;//像素点,纹理坐标(x,y).用易变变量,从"顶点着色器"传值给"片元着色器"的同名变量.

void main() {
    gl_Position = vPosition;//gl_Position是着色器内部变量,只要将坐标给它,OpenGL就会自动处理.确定GPU需要渲染什么形状图像.
    //没有for循环遍历的,因为性能比较低.
    aCoord = (vMatrix * vCoord).xy;//坐标点跟矩阵相乘,就可以得到正确的显示方向.
}
