import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Scanner;

public class ClientManager implements Runnable {
    private Socket assigned_client;
    private Archive book_archive;

    String name;

    String library = null;

    public ClientManager(Socket assigned_client, Archive archive) {
        System.out.println("\nCreating client manager assigned to client: "+assigned_client.getRemoteSocketAddress());
        this.assigned_client = assigned_client;
        this.book_archive = archive;
    }

    @Override
    public void run() {
        this.name = Thread.currentThread().getName();

        System.out.println("Starting Client Manager: "+ name);

        Scanner is = null;
        PrintWriter os = null;
        try {
            is = new Scanner(assigned_client.getInputStream());
            os = new PrintWriter(assigned_client.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        boolean goon = true;

        while (goon) {

            System.out.println("\n>> CM " + this.name + " -> waiting for next command");
            String command = is.nextLine();
            System.out.println(">> CM " + this.name + ": received command " + command);

            switch (command) {
                case "COMMAND_ADD":
                    String data = is.nextLine();
                    System.out.println(">> CM " + this.name + ": received data: " + data);

                    var data_scanner = new Scanner(data).useDelimiter(";");

                    String kind = data_scanner.next();
                    String name = data_scanner.next();
                    String year = data_scanner.next();
                    book_archive.add(new Book(kind, name, year, null, null));

                    if(kind.equals("action") || kind.equals("fantasy") || kind.equals("thriller") || kind.equals("other")) {
                        book_archive.save(kind+".dat");

                        os.println("SAVED_CORRECTLY");
                        os.flush();
                    } else {
                        System.out.println(">> CM " + this.name + ": cannot save in library");

                        os.println("SAVED_NOT_CORRECTLY");
                        os.flush();
                    }
                    break;

                case "COMMAND_LOAD":
                    book_archive.getList().clear();

                    library = is.nextLine();

                    File file = new File(library);
                    if(file.length() == 0L) {
                        System.out.println(">> CM " + this.name + ": file " + library + " is empty");

                        os.println("LOADED_EMPTY");
                        os.flush();
                    } else {
                         if (library.equals("action.dat")) {
                            System.out.println(">> CM " + this.name + ": loading from file " + library);
                            System.out.println("\nbook.archive before -> " + book_archive.getList().toString());
                            book_archive.load(library);

                            os.println("LOADED_CORRECTLY");
                            os.flush();
                        } else if (library.equals("fantasy.dat")) {
                            System.out.println(">> CM " + this.name + ": loading from file " + library);
                            System.out.println("\nbook.archive before -> " + book_archive.getList().toString());
                            book_archive.load(library);

                            os.println("LOADED_CORRECTLY");
                            os.flush();
                        } else if (library.equals("thriller.dat")) {
                            System.out.println(">> CM " + this.name + ": loading from file " + library);
                            System.out.println("\nbook.archive before -> " + book_archive.getList().toString());
                            book_archive.load(library);

                            os.println("LOADED_CORRECTLY");
                            os.flush();
                        } else if (library.equals("other.dat")) {
                            System.out.println(">> CM " + this.name + ": loading from file " + library);
                            System.out.println("\nbook.archive before -> " + book_archive.getList().toString());
                            book_archive.load(library);

                            os.println("LOADED_CORRECTLY");
                            os.flush();
                        } else {
                            System.out.println(">> CM " + this.name + ": file " + library + " not found");

                            os.println("LOADED_NOT_CORRECTLY");
                            os.flush();
                        }
                    }
                    System.out.println("book.archive after -> " + book_archive.getList().toString());
                    break;

                case "COMMAND_SEARCH":
                    String search_info = is.nextLine();
                    System.out.println(">> CM " + this.name + ": received info " + search_info);

                    var info_scanner = new Scanner(search_info).useDelimiter(";");
                    String n = info_scanner.next();
                    String y = info_scanner.next();

                    os.println("BEGIN_RESPONSE");

                    var found_books = book_archive.searchByInfo(n, y);

                    for (Book b : found_books) {
                        String to_send = ";" + b.getName() + ";" + b.getYear() + ";";
                        os.println(to_send);
                        os.flush();
                    }
                    os.println("END_RESPONSE");
                    os.flush();
                    break;

                case "COMMAND_RENT":
                    String info_rent = is.nextLine();

                    info_scanner = new Scanner(info_rent).useDelimiter(";");

                    name = info_scanner.next();
                    year = info_scanner.next();

                    found_books = book_archive.searchByInfo(name, year);
                    if (!found_books.isEmpty()) {
                        for (Book b : found_books) {
                            if (b.getRental() == null) {
                                b.setRental(this.name);
                                b.setTimeFromRent(LocalDate.now().toString());
                                book_archive.save(b.getKind()+".dat");
                                System.out.println(">> CM " + this.name + ": " + b.getRental() + " rented the book");

                                os.println("RENT_CORRECTLY");
                                os.flush();
                            } else {
                                System.out.println(">> CM " + this.name + ": impossible to rent");

                                os.println("RENT_NOT_CORRECTLY");
                                os.flush();
                            }
                        }
                    } else {
                        System.out.println(">> CM " + this.name + ": cannot rent, the book isn't in the list");

                        os.println("RENT_EMPTY");
                        os.flush();
                    }
                    break;

                case "COMMAND_REMOVE":
                    String info_remove = is.nextLine();
                    System.out.println(">> CM "+ this.name + ": trying to remove file " + info_remove);

                    info_scanner = new Scanner(info_remove).useDelimiter(";");

                    name = info_scanner.next();
                    year = info_scanner.next();

                    found_books = book_archive.searchByInfo(name, year);

                    if (!found_books.isEmpty()) {
                        for (Book b : found_books) {
                            book_archive.remove(b);
                            book_archive.save(b.getKind()+".dat");
                            System.out.println(">> CM " + this.name + ": book " + found_books + " removed correctly");

                            os.println("REMOVED_CORRECTLY");
                            os.flush();
                        }
                    } else {
                        System.out.println(">> CM " + this.name + ": cannot remove, the book doesn't exist");

                        os.println("REMOVED_NOT_CORRECTLY");
                        os.flush();
                    }
                    break;

                case "COMMAND_CLEAR":
                    System.out.println(">> CM " + this.name + ": executing clear");

                    book_archive.getList().clear();
                    book_archive.save(library);

                    os.println("CLEAR_CORRECTLY");
                    os.flush();
                    break;

                case "COMMAND_QUIT":
                    System.out.println("\n>> CM " + this.name + ": Closing connection with " + assigned_client.getRemoteSocketAddress());
                    goon = false;
                    try {
                        assigned_client.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
            }
        } // while interno
    }
}
