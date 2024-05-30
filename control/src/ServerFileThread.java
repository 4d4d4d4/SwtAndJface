
import java.awt.Container;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;

import javax.swing.JOptionPane;

public class ServerFileThread extends Thread {
  
   private static final int SERVER_PORT = 8899 ; // ����˶˿�
  
   private static DecimalFormat df = null ;
   private Container container;
  
   static {
     // �������ָ�ʽ������һλ��ЧС��
     df = new DecimalFormat( "#0.0" );
     df.setRoundingMode(RoundingMode.HALF_UP);
     df.setMinimumFractionDigits( 1 );
     df.setMaximumFractionDigits( 1 );
   }
  
   public ServerFileThread(Container container) throws Exception {
	   this.container = container;
   }
  
   /**
    * ʹ���̴߳���ÿ���ͻ��˴�����ļ�
    */

   public void run() {
	   ServerSocket serverSocket;
	try {
			serverSocket = new ServerSocket(SERVER_PORT);
	     while ( true ) {
	       // server���Խ�������Socket����������server��accept����������ʽ��
	       Socket socket = serverSocket.accept();
	       /**
	        * ���ǵķ���˴���ͻ��˵�����������ͬ�����еģ� ÿ�ν��յ����Կͻ��˵����������
	        * ��Ҫ�ȸ���ǰ�Ŀͻ���ͨ����֮������ٴ�����һ���������� ���ڲ����Ƚ϶������»�����Ӱ���������ܣ�
	        * Ϊ�ˣ����ǿ��԰�����Ϊ���������첽������ͻ���ͨ�ŵķ�ʽ
	        */
	       // ÿ���յ�һ��Socket�ͽ���һ���µ��߳���������
	       new Thread( new Task(socket,container)).start();
	     }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
  
   /**
    * ����ͻ��˴���������ļ��߳���
    */
   class Task implements Runnable {
  
     private Socket socket;
  
     private DataInputStream dis;
  
     private FileOutputStream fos;
     
     private Container container;
  
     public Task(Socket socket,Container container) {
       this.socket = socket;
       this.container = container;
     }
  
     @Override
     public void run() {
       try {
         dis = new DataInputStream(socket.getInputStream());
  
         // �ļ����ͳ���
         String fileName = dis.readUTF();
         long fileLength = dis.readLong();
         System.out.println("ing~");
         int result = JOptionPane.showConfirmDialog(container, "�Ƿ���ܿ��ƶ˷������ļ�����"+fileName+"��", "��ʾ",
				   JOptionPane.YES_NO_OPTION);
         if(result == 0) {
        	 System.out.println(result);
             File directory = new File( "D:\\�����ļ�" );
             if (!directory.exists()) {
               directory.mkdir();
             }
             File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
             fos = new FileOutputStream(file);
      
             // ��ʼ�����ļ�
             byte [] bytes = new byte [ 1024 ];
             int length = 0 ;
             while ((length = dis.read(bytes, 0 , bytes.length)) != - 1 ) {
               fos.write(bytes, 0 , length);
               fos.flush();
             }
             System.out.println( "======== �ļ����ճɹ� [File Name��" + fileName + "] [Size��" + getFormatFileSize(fileLength) + "] ========" );
         }else {
        	 System.out.println("ȡ�������ļ�");
         }
        
       } catch (Exception e) {
         e.printStackTrace();
       } finally {
         try {
           if (fos != null )
             fos.close();
           if (dis != null )
             dis.close();
           socket.close();
         } catch (Exception e) {}
       }
     }
   }
  
   /**
    * ��ʽ���ļ���С
    * @param length
    * @return
    */
   private String getFormatFileSize( long length) {
     double size = (( double ) length) / ( 1 << 30 );
     if (size >= 1 ) {
       return df.format(size) + "GB" ;
     }
     size = (( double ) length) / ( 1 << 20 );
     if (size >= 1 ) {
       return df.format(size) + "MB" ;
     }
     size = (( double ) length) / ( 1 << 10 );
     if (size >= 1 ) {
       return df.format(size) + "KB" ;
     }
     return length + "B" ;
   }
  
   /**
    * ���
    * @param args
    */
   public static void main(String[] args) {
     try {
       //ServerFileThread server = new ServerFileThread(); // ���������
      // server.load(null);
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
}