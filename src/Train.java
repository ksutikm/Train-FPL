import java.time.LocalTime;

public class Train {

    private int number;
    private String departurePoint;
    private String destination;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private double ticketPrice;

    public Train(int number, String departurePoint, String destination, LocalTime departureTime, LocalTime arrivalTime, double ticketPrice) {
        this.number = number;
        this.departurePoint = departurePoint;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.ticketPrice = ticketPrice;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setDeparturePoint(String departurePoint) {
        this.departurePoint = departurePoint;
    }

    public String getDeparturePoint() {
        return departurePoint;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(number + ": ")
                .append(departurePoint + "(" + departureTime + ")").append(" -> ")
                .append(destination + "(" + arrivalTime + ")")
                .append("\n     Стоимость билета: " + ticketPrice + "\n");

        return str.toString();
    }


    public boolean equals(Object o) {
        if (!(o instanceof Train)) {
            return false;
        }

        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (this.number == ((Train) o).number &&
                this.departurePoint.toLowerCase().equals(((Train) o).departurePoint.toLowerCase()) &&
                this.destination.toLowerCase().equals(((Train) o).destination.toLowerCase()) &&
                this.departureTime.equals(((Train) o).departureTime) &&
                this.arrivalTime.equals(((Train) o).arrivalTime) &&
                this.ticketPrice == ((Train) o).ticketPrice) {
            return true;
        }

        return false;
    }

}











