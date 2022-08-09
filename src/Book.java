import java.io.Serializable;

public class Book implements Serializable, Comparable<Book> {
    private String kind;
    private String name;
    private String year;

    private String rental;

    private String timeFromRent;

    public Book(String kind, String name, String year, String rental, String timeFromRent) {
        this.kind = kind;
        this.name = name;
        this.year = year;
        this.rental = rental;
        this.timeFromRent = timeFromRent;
    }

    public Book(String kind, String name, String year) {
        this.kind = kind;
        this.name = name;
        this.year = year;
    }

    public Book(String name, String year) {
        this.name = name;
        this.year = year;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRental() {
        return rental;
    }

    public void setRental(String rental) {
        this.rental = rental;
    }

    public String getTimeFromRent() {
        return timeFromRent;
    }

    public void setTimeFromRent(String timeFromRent) {
        this.timeFromRent = timeFromRent;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", year='" + year + '\'' +
                ", rental='" + rental + '\'' +
                ", timeFromRent='" + timeFromRent + '\'';
    }

    @Override
    public int compareTo(Book b) {
        if (name.equals(b.getName()) && year.equals(b.getYear())) {
            return 0;
        } else return -1;
    }
}
