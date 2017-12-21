package com.framework.function;

import java.awt.image.BufferedImage;  
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;  
import java.util.Map;  
  
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.springframework.http.HttpHeaders;  
import org.springframework.http.HttpStatus;  
import org.springframework.http.MediaType;  
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.framework.file.ImageUtils;
import com.framework.utils.HttpUtils;
import com.google.zxing.BarcodeFormat;  
import com.google.zxing.Binarizer;  
import com.google.zxing.BinaryBitmap;  
import com.google.zxing.DecodeHintType;  
import com.google.zxing.EncodeHintType;  
import com.google.zxing.LuminanceSource;  
import com.google.zxing.MultiFormatReader;  
import com.google.zxing.MultiFormatWriter;  
import com.google.zxing.NotFoundException;  
import com.google.zxing.Result;  
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;  
import com.google.zxing.common.HybridBinarizer; 

/**
 * 二维码相关
 * 好像只支持JDK1.8及以上
 * 只提供基础的二维码生成，二维码美化推荐草料二维码http://mh.cli.im/
 * @author minqi 2017-12-20 14:02:29
 *
 */
@Controller
@RequestMapping("/trust/qrCode")
public class QrCode {
	
	private static final String format = "png";		//二维码图片类型
	private static final int width = 300;			//图像宽度
	private static final int height = 300;			//图像高度
	
	public static void main(String[] args) throws Exception {  
		getQrEncode("");  
//        getQrDecode();  
    }  
  
    /**
     * 生成二维码 
     * @param content 二维码扫码内容
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static void getQrEncode(String content) throws WriterException, IOException {
        content = "测试zxing生成二维码";
        
        //准备参数
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        
        //生成矩阵
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        
        //生成图片
        File file = new File("D://zxing.png");
        MatrixToImageWriter.writeToFile(bitMatrix, format, file);				//输出到文件
        
//        MatrixToImageWriter.writeToStream(bitMatrix, format, outPutStream);		//输出到流
        
        System.out.println("输出成功.");
    }  
  
    /** 
     * 解析二维码 
     */  
    public static void getQrDecode() {  
        String filePath = "D://zxing.png";
        BufferedImage image;
        try {
            image = ImageIO.read(new File(filePath));
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            
            Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result result = new MultiFormatReader().decode(binaryBitmap, hints);//对图像进行解码
            
            System.out.println("图片中内容author： " + result.getText());
            System.out.println("图片中格式encode： " + result.getBarcodeFormat());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 直接获取网络图片显示在页面上
     * http://localhost:8080/marshow/trust/qrCode/test.do
     * @param response
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/test.do")
    public ResponseEntity test(HttpServletResponse response) throws ClientProtocolException, IOException{
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);  
        String imgUrl = "http://www.ksks001.com/img1/5370105154200175440.jpeg";  
        byte[] imgByte = ImageUtils.getNetImg(imgUrl);  
        ResponseEntity entity = new ResponseEntity(imgByte, headers, HttpStatus.CREATED);
        return entity; 
    }
    
	
}
