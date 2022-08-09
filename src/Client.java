import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private Scanner is;
    private PrintWriter os;
    private String address;
    private int port;

    public Client(String server_address, int server_port) {
        this.address = server_address;
        this.port = server_port;
    }

    public void showMenu(){
        var user_input = new Scanner(System.in);

        boolean goon = true;

        while (goon) {
            System.out.println("\n-----------------WELCOME IN YOUR LIBRARY-----------------");
            System.out.println(" 1 - add book to list");
            System.out.println(" 2 - load list");
            System.out.println(" 3 - search book");
            System.out.println(" 4 - rent book");
            System.out.println(" 5 - remove book");
            System.out.println(" 6 - clear list");
            System.out.println(" 0 - exit");
            System.out.println("---------------------------------------------------------");

            System.out.print(" enter choice: ");

            String choice = user_input.nextLine();

            switch (choice)  {
                case "1":
                    menu_add();
                    break;
                case "2":
                    menu_load();
                    break;
                case "3":
                    menu_search();
                    break;
                case "4":
                    menu_rent();
                    break;
                case "5":
                    menu_remove();
                    break;
                case "6":
                    menu_clear();
                    break;
                case "0":
                    goon = false;
                    menu_exit();
                    break;
                default:
                    System.out.println("\n>> CLIENT: unrecognized choice: " + choice);
                    break;
            }
        }
    }

    public void startConnection() {
        System.out.println("\nStarting client connection to " + address + " port " + port);

        try {
            socket = new Socket(address,port);

            System.out.println("Client connected to "+ socket.getRemoteSocketAddress());

            is = new Scanner(socket.getInputStream());
            os = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String address = args[0];
        int port = Integer.parseInt(args[1]);

        var client = new Client(address,port);
        client.startConnection();
        client.showMenu();
        System.out.println("Client closed");
    }

    private void menu_add() {
        var input = new Scanner(System.in);
        System.out.println("Book kind:\naction - fantasy - thriller - other ");
        String kind = input.nextLine();
        System.out.print("Book name: ");
        String name = input.nextLine();
        System.out.print("Book year: ");
        String year = input.nextLine();

        String to_send = ";"+kind+";"+name+";"+year+";";

        os.println("COMMAND_ADD");
        os.println(to_send);
        os.flush();

        String message_from_server = is.nextLine();
        if(message_from_server.equals("SAVED_CORRECTLY")) {
            System.out.println("\n>> CLIENT: book added and saved correctly");
        } else if(message_from_server.equals("SAVED_NOT_CORRECTLY")) {
            System.out.println("\n>> CLIENT: impossible add the book");
        } else {
            System.out.println("\n>> CLIENT: unknown error");
        }
    }
    private void menu_load() {
        var sc = new Scanner(System.in);
        System.out.print("Insert library:\naction.dat - fantasy.dat - thriller.dat - other.dat\n");
        String library = sc.nextLine();

        os.println("COMMAND_LOAD");
        os.println(library);
        os.flush();

        String message_from_server = is.nextLine();
        if(message_from_server.equals("LOADED_CORRECTLY")) {
            System.out.println("\n>> CLIENT: library loaded correctly");
        } else if (message_from_server.equals("LOADED_EMPTY")) {
            System.out.println("\n>> CLIENT: library is empty");
        } else {
            System.out.println("\n>> CLIENT: library not found");
        }
    }
    private void menu_search() {
        var sc = new Scanner(System.in);

        System.out.println("Insert book name: ");
        String name = sc.nextLine();
        System.out.print("Insert book year: ");
        String year = sc.nextLine();

        String to_send = ";"+name+";"+year+";";

        os.println("COMMAND_SEARCH");
        os.println(to_send);
        os.flush();

        if (is.nextLine().equals("BEGIN_RESPONSE")) {
            boolean end_response = false;

            String s = is.nextLine();

            end_response = s.equals("END_RESPONSE");

            if (end_response) {
                System.out.println("\n>> CLIENT: no book found");
                return;  // to the menu...
            }

            while (!end_response) {
                // nota: cosa succede se si porta fuori la dichiarazione di fs?
                Scanner fs = new Scanner(s).useDelimiter(";");

                System.out.println("\n****************************************************");
                System.out.println("Name: " + fs.next() + " --- Year: " + fs.next());

                s = is.nextLine();
                end_response = s.equals("END_RESPONSE");
            }
            System.out.println("****************************************************");
        }
    }

    public void menu_rent() {
        var input = new Scanner(System.in);
        System.out.println("Insert book name: ");
        String name = input.nextLine();
        System.out.println("Insert book year: ");
        String year = input.nextLine();

        String message_to_server = ";"+name+";"+year+";";

        os.println("COMMAND_RENT");
        os.println(message_to_server);
        os.flush();

        String message_from_server = is.nextLine();

        if (message_from_server.equals("RENT_CORRECTLY")) {
            System.out.println("\n>> CLIENT: you rented the book");
        } else if (message_from_server.equals("RENT_NOT_CORRECTLY")) {
            System.out.println("\n>> CLIENT: the book has already been rented");
        } else {
            System.out.println("\n>> CLIENT: the book isn't present in the library");
        }
    }

    private void menu_remove() {
        var input = new Scanner(System.in);
        System.out.println("Insert book name: ");
        String name = input.nextLine();
        System.out.println("Insert book year: ");
        String year = input.nextLine();

        String message_to_server = ";"+name+";"+year+";";

        os.println("COMMAND_REMOVE");
        os.println(message_to_server);
        os.flush();

        String message_from_server = is.nextLine();
        if(message_from_server.equals("REMOVED_CORRECTLY")) {
            System.out.println("\n>> CLIENT: the book has been removed");
        } else if (message_from_server.equals("REMOVE_NOT_CORRECTLY")) {
            System.out.println("\n>> CLIENT: the book doesn't exist");
        } else {
            System.out.println("\n>> CLIENT: unknown error " + message_from_server);
        }
    }

    private void menu_clear() {
        os.println("COMMAND_CLEAR");
        os.flush();

        String message_from_server = is.nextLine();

        if(message_from_server.equals("CLEAR_CORRECTLY")) {
            System.out.println("\n>> CLIENT: the library has been cleared");
        } else {
            System.out.println("\n>> CLIENT: unknown error -> " + message_from_server);
        }
    }

    public void menu_exit() {
        System.out.println("\n>> CLIENT: closing connection with the server at " + address + " " + port + "\n");

        os.println("COMMAND_QUIT");
        os.flush();
    }
}
