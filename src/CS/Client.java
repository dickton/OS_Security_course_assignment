package CS;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client extends Thread{
    private int port;
    private Socket socket;

    public Client(String ip,int port) throws IOException, InterruptedException {
        this.socket=new Socket(InetAddress.getByName(ip),port);

    }

    public void run(){
        try {
            ClientRecvThread recvThread=new ClientRecvThread();
            recvThread.run();
            recvThread.join();
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    class ClientRecvThread extends Thread{
        @Override
        public void run() {
            InputStream inputStream= null;
            byte[] buffer=new byte[1024];
            int len;
            try {
                inputStream = socket.getInputStream();
//                if ((len = inputStream.read(buffer)) != -1) {
//                    File file = new File("C:\\temp\\cp_"+(new String(buffer)));
//                    System.out.println("C:\\temp\\cp_"+(new String(buffer, StandardCharsets.UTF_8)));
                File file = new File("C:\\temp\\cp_cam6.pdf");
                System.out.println("C:\\temp\\cp_cam6.pdf");

                if(!file.exists()){
                        file.createNewFile();
                    }
                    OutputStream outputStream=new FileOutputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                    outputStream.close();
                    inputStream.close();
                //}
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
