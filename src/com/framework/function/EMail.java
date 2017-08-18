package com.framework.function;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.framework.utils.PropertiesReader;

/**
 * 邮件类
 * Java EE 5 Libraries不支持；Java EE 6 Libraries不用导入jar包
 * @author minqi 2017-07-31 13:20:37
 *
 */
public class EMail {

	//配置文件读取对象
	public static PropertiesReader property = PropertiesReader.getInstance();
	
    private static String MAIL_HOST = property.getProperty("MAIL_HOST");			//邮箱的 SMTP 服务器地址
    private static String MAIL_SEND = property.getProperty("MAIL_SEND");			//发件箱
    private static String MAIL_PSW = property.getProperty("MAIL_PSW");				//密码或授权码
	
    /**
     * 获取邮件会话对象
     * @return
     */
	public static Session getMailSession(){
		// 参数配置
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");   			// 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", MAIL_HOST);   					// 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            			// 需要请求认证

        // PS: 某些邮箱服务器要求 SMTP 连接需要使用 SSL 安全认证 (为了提高安全性, 邮箱支持SSL连接, 也可以自己开启),
        //     如果无法连接邮件服务器, 仔细查看控制台打印的 log, 如果有有类似 “连接失败, 要求 SSL 安全连接” 等错误,
        //     打开下面 /* ... */ 之间的注释代码, 开启 SSL 安全连接。
  
        // SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
        //                  需要改为对应邮箱的 SMTP 服务器的端口, 具体可查看对应邮箱服务的帮助,
        //                  QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看)
        final String smtpPort = "465";
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);
     

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getInstance(props, new Authenticator() {  
            // 在session中设置账户信息，Transport发送邮件时会使用  
            protected PasswordAuthentication getPasswordAuthentication() {  
                return new PasswordAuthentication(MAIL_SEND, MAIL_PSW);  
            }  
        }); 
        
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log
        return session;
	}

	/**
	 * 发送邮件
	 * @param receiveMail 收件人邮箱地址
	 * @param sendName 显示发件人的名称
	 * @param title 邮件主题
	 * @param content 邮件正文
	 */
	public static void sendMail(String receiveMail,String sendName, String title, String content){
		Session session = getMailSession();
		
        // 3. 创建一封邮件
        MimeMessage message = new MimeMessage(session);     	// 创建邮件对象

        try {
			message.setFrom(new InternetAddress(MAIL_SEND, sendName, "UTF-8"));		//发件箱、显示发件人名称、字符编码
	        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "USER_CC", "UTF-8"));	//收件人
//	        message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("dd@receive.com", "USER_DD", "UTF-8"));	//增加收件人（可选）
//	        message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress("ee@receive.com", "USER_EE", "UTF-8"));	//抄送（可选）
//	        message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress("ff@receive.com", "USER_FF", "UTF-8"));	//密送（可选）
	       
	        message.setSubject(title, "UTF-8");									//邮件主题
	        message.setContent("<div align=\"left\">" + content + "</div>", "text/html;charset=utf-8");//邮件正文
	        message.setSentDate(new Date());											//设置显示的发件时间
	        message.saveChanges();														//保存设置
		
	        
	        // 4. 根据 Session 获取邮件传输对象
	        Transport transport = session.getTransport();
	        
	        // 5. 打开连接
	        transport.connect(MAIL_SEND, MAIL_PSW);

	        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
	        transport.sendMessage(message, message.getAllRecipients());
	        
	        // 7. 关闭连接
	        if(transport != null) transport.close();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 利用邮件模板发送邮件
	 * @param receiveMail
	 * @param sendName
	 * @param title
	 * @param content
	 */
	public static void sendMailTemplate(String receiveMail,String sendName, String title, String content){
		Session session = getMailSession();
        try {
            //也可以根据已有的eml邮件文件创建 MimeMessage 对象
        	MimeMessage message = new MimeMessage(session, new FileInputStream("C://MyEmail.eml"));
        	
			message.setFrom(new InternetAddress(MAIL_SEND, sendName, "UTF-8"));
	        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "USER_CC", "UTF-8"));	//收件人
//	        message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("dd@receive.com", "USER_DD", "UTF-8"));	//增加收件人（可选）
//	        message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress("ee@receive.com", "USER_EE", "UTF-8"));	//抄送（可选）
//	        message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress("ff@receive.com", "USER_FF", "UTF-8"));	//密送（可选）
	       
	        message.setSubject(title, "UTF-8");									//邮件主题
	        message.setContent("<div align=\"left\">" + content + "</div>", "text/html;charset=utf-8");
	        message.setSentDate(new Date());
	        message.saveChanges();
		
	        
	        Transport transport = session.getTransport();
	        transport.connect(MAIL_SEND, MAIL_PSW);
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	
    public static void main(String[] args) throws Exception {
       EMail.sendMail("13986147769@139.com", "sendName", "title", "content");
       
    }
}
