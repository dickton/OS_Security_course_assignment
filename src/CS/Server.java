package CS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private ServerSocket serverSocket;
    private int port;
    private String fileName;
    byte[] fileNameBuffer;
    String filePath;

    public Server(int port, String fileName, String filePath) throws IOException, InterruptedException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        this.fileName = fileName;
        this.filePath = filePath;
        fileNameBuffer = fileName.getBytes();

    }

    public void run() {
        try {
            for (int i = 0; i < 1; i++) {
                Socket socket = serverSocket.accept();
                FileSendThread fileSendThread = new FileSendThread(socket);
                fileSendThread.run();

            }
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Server terminated!");
    }


    class FileSendThread extends Thread {
        Socket socket;

        public FileSendThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            // Thread will finish after send the file to 1 client
            try {
                OutputStream outputStream = this.socket.getOutputStream();
                File file = new File(filePath, fileName);
                if (file.exists()) {
                    long startTime = System.currentTimeMillis();
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;

                    //outputStream.write(fileNameBuffer,0,fileNameBuffer.length);
                    //outputStream.flush();
                    while ((len = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                    System.out.println(String.format("Send file: " + fileName + " to %s: %d cost %d milliseconds!\n",
                            this.socket.getInetAddress().getHostAddress(),
                            this.socket.getPort(), System.currentTimeMillis() - startTime));
                    fileInputStream.close();

                }
                outputStream.close();
                this.socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
