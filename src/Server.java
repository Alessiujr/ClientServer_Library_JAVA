import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    private ServerSocket serverSocket;
    private Socket client_socket;
    private PrintWriter os;
    private Scanner is;
    private int port;

    private Archive film_archive = new Archive();

    public Server(int server_port) {
        this.port = server_port;
    }

    private void startServer() {
        try {
            var server = new ServerSocket(port);

            while (true) {
                System.out.println(">> SERVER: waiting for connection...");
                var client = server.accept();
                System.out.println(">> SERVER: Accepted connection form " + client.getRemoteSocketAddress());

                var cm = new ClientManager(client,film_archive);
                Thread t = new Thread(cm);
                t.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);

        System.out.println("\nStarting server on port " + port);

        var server = new Server(port);
        server.startServer();
    }


}
