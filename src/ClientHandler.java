import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username=bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage(username+" has entered the chat");
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    private void broadcastMessage(String message) {

        for(ClientHandler clientHandler:clientHandlers){
            try{
                if(!clientHandler.username.equals(username)){
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }
    }

    public void sendToSingleClient(String message,String name){
        try{
            for(ClientHandler clientHandler:clientHandlers){
                if(clientHandler.username.equals(name) && !clientHandler.username.equals(username)){
                    clientHandler.bufferedWriter.write(username+": "+message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                    break;
                }
            }
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage(username+" left the chat");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message;
        while(socket.isConnected()){
            try{
                message=bufferedReader.readLine();
                String rawMessage= message.split(": ")[1];
                if(rawMessage.charAt(0)=='@'){
                    String mention=rawMessage.split(" ")[0];
                    String name=mention.substring(1);
                    sendToSingleClient(rawMessage,name);
                }else broadcastMessage(message);
            }catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }
}
