# OS_Security_course_assignment
### 1. 程序架构设计

根据上机要求，我们应当设计一个程序对大文件进行加解密操作、且使用这个程序对这个大文件进行网络传输，同时要尽可能提升效率，因此本设计尽量使用了多线程设计，旨在提升程序表现。



#### 1.1 File I/O

考虑到上机要求中对大文件加解密、并提升加解密效率的要求，大文件的I/O操作将会占用较多的时间，故本设计采用了多线程I/O以完成对明文文件的快速读取/写入功能。

其中的要点为多线程下文件的安全访问、文件I/O任务分块。



#### 1.2 Encrypt & Decrypt

<img src="http://image.lvesu.com/uploads/ueditor/image/202005/11/1589164730850977.png" style="zoom:50%;" />

##### 1.2.1 Encrypt

本设计采用CBC模式下的DES算法，CBC算法需要串行化地计算密文，也即
$$
C_i\ =\ Encrypt_{DES}(Key,\ P_i)\ \oplus\ C_{i-1} \ \ \  (i\ \geqslant \ 2)
$$
因此加密算法无法使用多线程方法进行加速。



##### 1.2.2 Decrypt

对于解密计算，不难看出
$$
P_i\ =\ Decrypt_{DES}(Key,\ (C_i\, \oplus\, C_{i-1}) ) \ (i\ \geqslant \ 2)
$$
因此，在解密计算中，我们可以采用多线程的办法以提升解密速度。



### 1.3 文件的网络传输

本设计采用了经典的 *Server/Client* 式架构以实现网络传输服务

#### 1.3.1 Server

Server线程设立于10086端口，在启动服务后进行监听。
当有客户端到达，就new一个服务线程`FileSendThread`，由`FileSendThread` 负责为客户端进行服务，Server线程则继续等待新客户到达。



##### 1.3.2 Client

Client客户端主要功能为连接Server、验证本设计的文件传输服务，再对接收到的加密文件进行解密操作。
