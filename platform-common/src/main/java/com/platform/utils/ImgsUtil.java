package com.platform.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.io.FileOutputStream;



/**
 * 图片类工具
 * @author lx
 * @email 290988002@qq.com
 * @date 2018年11月9日 下午12:53:33
 * */
public class ImgsUtil {
    private  Font font     = new Font("宋体", Font.PLAIN, 12); // 添加字体的属性设置
    private  Graphics2D g  = null;
    private  int  fontsize = 0;
    private  int  x = 0;
    private  int  y = 0;

    /**
    * URL下载图片到本地
    * */
    public static String downloadImage(String Image_url,String filename)throws IOException {
          System.getProperties().setProperty("http.proxyHost","IP");//设置代理  
          System.getProperties().setProperty("http.proxyPort","Port");
          URL url = new URL(Image_url);
             //打开网络输入流
             DataInputStream dis = new DataInputStream(url.openStream());
             String newImageName="D://tmp//"+filename+".jpg";
             //建立一个新的文件  
             FileOutputStream fos =new FileOutputStream(new File(newImageName));
             byte[] buffer=new byte[1024];
             int length;
             //开始填充数据  
             while((length=dis.read(buffer))>0){
                 fos.write(buffer,0,length);
             }
                dis.close();
                fos.close();
            return newImageName;
    }
    /**
     * 获取图片地址
     */
    public  void draw(String imagePath,String path,String content){
        //读取图片文件，得到BufferedImage对象
        BufferedImage bimg;
        try {
            bimg = ImageIO.read(new FileInputStream(imagePath));
            //得到Graphics2D 对象
            Graphics2D g2d=(Graphics2D)bimg.getGraphics();
            //设置颜色和画笔粗细
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(3));
            g2d.setFont(new Font("TimesRoman", Font.BOLD, 80));

            //绘制图案或文字
            g2d.drawString(content, 150, 468);
            //保存新图片
            ImageIO.write(bimg, "JPG",new FileOutputStream(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 导入本地图片到缓冲区
     */
    public  BufferedImage loadImageLocal(String imgName) {
        try {
            return ImageIO.read(new File(imgName));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    /**
     * 导入网络图片到缓冲区
     */
    public  BufferedImage loadImageUrl(String imgName) {
        try {
            URL url = new URL(imgName);
            return ImageIO.read(url);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    /**
     * 生成新图片到本地
     */
    public  void writeImageLocal(String newImage, BufferedImage img) {
        if (newImage != null && img != null) {
            try {
                File outputfile = new File(newImage);
                ImageIO.write(img, "jpg", outputfile);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    /**
     * 设定文字的字体等
     */
    public  void setFont(String fontStyle, int fontSize) {
        this.fontsize = fontSize;
        this.font = new Font(fontStyle, Font.PLAIN, fontSize);
    }
    /**
     * 修改图片,返回修改后的图片缓冲区（只输出一行文本）
     */
    public  BufferedImage modifyImage(BufferedImage img, Object content, int x, int y) {
        try {
            int w = img.getWidth();
            int h = img.getHeight();
            g = img.createGraphics();
            g.setBackground(Color.WHITE);
            g.setColor(Color.orange);//设置字体颜色
            if (this.font != null)
                g.setFont(this.font);
            // 验证输出位置的纵坐标和横坐标
            if (x >= h || y >= w) {
                this.x = h - this.fontsize + 2;
                this.y = w;
            } else {
                this.x = x;
                this.y = y;
            }
            if (content != null) {
                g.drawString(content.toString(), this.x, this.y);
            }
            g.dispose();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return img;
    }
    /**
     * 修改图片,返回修改后的图片缓冲区（输出多个文本段） xory：true表示将内容在一行中输出；false表示将内容多行输出
     */
    public BufferedImage modifyImage(BufferedImage img, Object[] contentArr, int x, int y,
                                     boolean xory) {
        try {
            int w = img.getWidth();
            int h = img.getHeight();
            g = img.createGraphics();
            g.setBackground(Color.WHITE);
            g.setColor(Color.RED);
            if (this.font != null)
                g.setFont(this.font);
            // 验证输出位置的纵坐标和横坐标
            if (x >= h || y >= w) {
                this.x = h - this.fontsize + 2;
                this.y = w;
            } else {
                this.x = x;
                this.y = y;
            }
            if (contentArr != null) {
                int arrlen = contentArr.length;
                if (xory) {
                    for (int i = 0; i < arrlen; i++) {
                        g.drawString(contentArr[i].toString(), this.x, this.y);
                        this.x += contentArr[i].toString().length() * this.fontsize / 2 + 5;// 重新计算文本输出位置
                    }
                } else {
                    for (int i = 0; i < arrlen; i++) {
                        g.drawString(contentArr[i].toString(), this.x, this.y);
                        this.y += this.fontsize + 2;// 重新计算文本输出位置
                    }
                }
            }
            g.dispose();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return img;
    }
    /**
     * 修改图片,返回修改后的图片缓冲区（只输出一行文本）
     * 时间:2018-11-9     *
     * @param img
     * @return
     */
    public BufferedImage modifyImageYe(BufferedImage img) {
        try {
            int w = img.getWidth();
            int h = img.getHeight();
            g = img.createGraphics();
            g.setBackground(Color.WHITE);
            g.setColor(Color.blue);//设置字体颜色
            if (this.font != null)
                g.setFont(this.font);
            g.drawString("www.hi.baidu.com?xia_mingjian", w - 85, h - 5);
            g.dispose();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return img;
    }
    public BufferedImage modifyImagetogeter(BufferedImage b, BufferedImage d,int x,int y,int w,int h) {
        try {
            g = d.createGraphics();
            g.drawImage(b, x, y, w, h, null);
            g.dispose();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return d;
    }
    /**图片组合示例*/
    public void TestDemo(){
        ImgsUtil tt = new ImgsUtil();

        BufferedImage d = tt.loadImageLocal("\\ploanshare\\2\\11.jpg");
        BufferedImage b = tt.loadImageLocal("\\ploanshare\\2\\22.png");
        //往图片上写文件
        //tt.writeImageLocal("E:\\ploanshare\\2\\22.jpg", tt.modifyImage(d, "000000", 90, 90));

        tt.writeImageLocal("\\ploanshare\\2\\cc.jpg", tt.modifyImagetogeter(b, d,100,20,30,30));
        //将多张图片合在一起
        System.out.println("success");

    }
    /**
     * @param srcImgPath 源图片路径
     * @param tarImgPath 保存的图片路径
     * @param waterMarkContent 水印内容
     * @param markContentColor 水印颜色
     * @param font 水印字体
     */
    public static String addWaterMark(String srcImgPath, String tarImgPath, String waterMarkContent,Color markContentColor,Font font,int x,int y) {
        try {
            // 读取原图片信息
            File srcImgFile = new File(srcImgPath);//得到文件
            Image srcImg = ImageIO.read(srcImgFile);//文件转化为图片
            int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);//获取图片的高
            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            g.setColor(markContentColor); //根据图片的背景设置水印颜色
            g.setFont(font);              //设置字体
            g.drawString(waterMarkContent, x, y);  //画出水印
            g.dispose();
            // 输出图片
            FileOutputStream outImgStream = new FileOutputStream(tarImgPath);
            ImageIO.write(bufImg, "jpg", outImgStream);
            System.out.println("添加水印完成");
            outImgStream.flush();
            outImgStream.close();
            return tarImgPath;
        } catch (Exception e) {
            //  TODO: handle exception
            return "";
        }
    }
    public static int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }
    /**加载水印示例*/
    public void main() {
        Font font = new Font("微软雅黑", Font.PLAIN, 35);                     //水印字体
        String srcImgPath="H:/安静时写写/写写博客/Java实现给图片添加水印/s.jpg"; //源图片地址
        String tarImgPath="H:/安静时写写/写写博客/Java实现给图片添加水印/t.jpg"; //待存储的地址
        String waterMarkContent="图片来源：http://blog.csdn.net/zjq_1314520";  //水印内容
        Color color=new Color(23,20,20,1);                               //水印图片色彩以及透明度
        addWaterMark(srcImgPath, tarImgPath,waterMarkContent, color, font,10,10);
    }
}