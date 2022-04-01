import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;
    private BufferedReader bufferedReader;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{

            while(!serverSocket.isClosed()){
                Socket socket=serverSocket.accept();
                System.out.println("new client connected");
                ClientHandler clientHandler =new ClientHandler(socket);

                Thread thread=new Thread(clientHandler);
                thread.start();

            }

        }catch (IOException e){
            closeServerSocket();
        }
    }

    public void closeServerSocket(){
        try{
            if(serverSocket!=null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket=new ServerSocket(3000);
        Server server=new Server(serverSocket);
        server.startServer();
    }
}
