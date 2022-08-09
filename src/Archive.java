import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Archive implements Serializable {

    private ArrayList<Book> list;
    private String modification_time;

    public Archive() {
        this.list = new ArrayList<>();
    }

    public ArrayList<Book> getList() {
        return list;
    }

    public synchronized void add(Book b) {
        this.list.add(b);
        this.modification_time = LocalDate.now().toString();
    }

    public synchronized void save(String filename) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void load(String filename) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(filename));
            if (ois == null) {
                throw new FileNotFoundException();
            }
            var temp_archive = (Archive) ois.readObject();
            this.list = temp_archive.list;
            this.modification_time = temp_archive.modification_time;
            ois.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println(">> ARCHIVE: loaded list:");
        System.out.println("-------------------------------------------------------------------------");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(i+1 + " - " + list.get(i));
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("modification date: " + modification_time);
    }

    public ArrayList<Book> searchByInfo(String name, String year) {
        ArrayList<Book> found_film = new ArrayList<>();

        for (Book b: list) {
            if (b.getName().equals(name) && b.getYear().equals(year)) {
                found_film.add(b);
            }
        }
        return found_film;
    }

    public String getModification_time() {
        return modification_time;
    }

    public synchronized void remove(Book b) {
        list.remove(b);
    }

    public synchronized void clear() {
        list.clear();
    }
}
