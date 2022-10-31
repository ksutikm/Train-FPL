import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.System.exit;

public class TrainList {
    private List<Train> trains;

    TrainList() {
        this.trains = new ArrayList<>();
        scannerData();
    }

    private String getPoint(Scanner sc, String message) {
        printMessage(message);
        return sc.next();
    }

    private int getNumberTrain(Scanner sc) throws MyException {
        printMessage("Введите номер поезда");
        int number = sc.nextInt();
        if (number <= 0)
            throw new MyException("Ошибка!!! Введен неверный номер поезда!");
        return number;
    }

    private double getTicketPrice(Scanner sc) {
        printMessage("Введите стоимость билета");
        return sc.nextDouble();
    }

    private LocalTime getTime(Scanner sc, String message) {
        printMessage(message);
        LocalTime time = null;
        try {
            time = getTime(sc);
        } catch (MyException me) {
            System.out.println(me.getMessage());
        }
        return time;
    }

    private LocalTime getTime(Scanner sc) throws MyException {
        String[] time;
        String enteredTime = sc.next();
        if (!enteredTime.matches("[0-9][0-9]:[0-9][0-9]"))
            throw new MyException("Ошибка!!! Введен неверный формат времени!");
        time = getArrayStrings(enteredTime, ":");
        if (Integer.parseInt(time[0]) >= 24 || Integer.parseInt(time[1]) >= 60)
            throw new MyException("Ошибка!!! Введен неверный формат времени!");
        return getLocalTime(time);
    }

    private LocalTime getLocalTime(String[] time) {
        return LocalTime.of(Integer.parseInt(time[0]), Integer.parseInt(time[1]));
    }

    private String[] getArrayStrings(String str, String separator) {
        return str.split(separator);
    }

    private Train getTrainByLine(Scanner in) throws IOException {
        String[] str = getArrayStrings(in.nextLine(), ",");
        if (checkCorrectData(str))
            throw new IOException();
        String[] time1 = getArrayStrings(str[3], ":");
        String[] time2 = getArrayStrings(str[4], ":");
        return getNewTrain(Integer.parseInt(str[0]), str[1], str[2], getLocalTime(time1), getLocalTime(time2), Double.parseDouble(str[5]));
    }

    private boolean checkCorrectData(String[] str) {
        return str.length != 6 || !str[0].matches("[0-9]+") || !str[5].matches("[0-9]+.[0-9]+") ||
                !str[3].matches("[0-9][0-9]:[0-9][0-9]") || !str[4].matches("[0-9][0-9]:[0-9][0-9]");
    }

    private Train getNewTrain(int number, String departurePoint, String destination, LocalTime departureTime, LocalTime arrivalTime, double ticketPrice) {
        return new Train(number, departurePoint, destination, departureTime, arrivalTime, ticketPrice);
    }

    private void scannerData() {
        try (Scanner in = new Scanner(new File("src/data.txt"))) {
            while (in.hasNextLine()) {
                trains.add(getTrainByLine(in));
            }
        } catch (FileNotFoundException e) {
            System.out.println("К сожалению, данные из файла не загрузились.");
        } catch (IOException e) {
            System.out.println("Ошибка!!! Некорректные данные файла.");
            exit(0);
        }
    }

    public void addTrain() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n***Добавление поезда***\n");
        try {
            trains.add(getNewTrain(
                    getNumberTrain(sc),
                    getPoint(sc, "Введите пункт отправления"),
                    getPoint(sc, "Введите пункт назначения"),
                    getTime(sc, "Введите время отправления"),
                    getTime(sc, "Введите время прибытия"),
                    getTicketPrice(sc)
            ));
            System.out.println("\nПоезд добавлен!");
        } catch (MyException me) {
            System.out.println(me.getMessage());
        }
    }

    private Train getTrainForRemove(Scanner sc) throws MyException {
        int numberTrain = getNumberTrain(sc);
        return getTrainByNumber(numberTrain);
    }

    private Train getTrainByNumber(int numberTrain) {
        return trains.stream().filter(t -> t.getNumber() == numberTrain).findFirst().orElse(null);
    }

    public void removeTrain() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("\n***Удаление поезда***");
            printTrains();
            Train train = getTrainForRemove(sc);
            if (train != null) {
                trains.remove(train);
                System.out.println("\nПоезд №" + train.getNumber() + " удален!");
            } else {
                throw new MyException("Введен номер поезда, которого нет в списках!");
            }
        } catch (MyException me) {
            System.out.println(me.getMessage());
        }
    }

    private List<Train> getMoveTrains(String departurePoint, String destination) {
        List<Train> moveTrains = new ArrayList<>();
        for (Train t : trains) {
            if (t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase())) {
                for (Train t2 : trains) {
                    if (t.getDestination().toLowerCase().equals(t2.getDeparturePoint().toLowerCase()) &&
                            t2.getDestination().toLowerCase().equals(destination.toLowerCase())) {
                        moveTrains.add(t);
                        moveTrains.add(t2);
                    }
                }
            }
        }
        return moveTrains;
    }

    private List<Train> getTransferTrains(String departurePoint, String destination) {
        return getMoveTrains(departurePoint, destination).stream().distinct().sorted(new TrainComparator()).collect(Collectors.toList());
    }

    private List<Train> getNotTransferTrains(String departurePoint, String destination) {
        return trains.stream().filter(t -> t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase()))
                .filter(train -> train.getDestination().toLowerCase().equals(destination.toLowerCase()))
                .sorted(new TrainComparator()).collect(Collectors.toList());
    }

    public void moveTrain() {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n***Маршрут поездки по пунктам отправления и назначения***\n");
        String departurePoint = getPoint(sc, "Введите пункт отправления");
        String destination = getPoint(sc, "Введите пункт назначения");

        System.out.println("\n***Список поездов с пересадкой***\n");
        getTransferTrains(departurePoint, destination).forEach(System.out::println);

        System.out.println("\n***Список поездов без пересадки***\n");
        getNotTransferTrains(departurePoint, destination).forEach(System.out::println);
    }

    private List<Train> getListTrainByPointAndTime(String departurePoint, LocalTime arrivalTime) {
        return trains.stream().filter(t -> t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase()))
                .filter(t -> arrivalTime.equals(t.getArrivalTime())).collect(Collectors.toList());
    }

    public void listPointAndTime() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("\n***Список поездов по пункту отправления и времени прибытия***\n");
            String departurePoint = getPoint(sc, "Введите пункт отправления");
            LocalTime arrivalTime = getTime(sc, "Введите время прибытия");
            System.out.println("\n***Список поездов***\n");
            getListTrainByPointAndTime(departurePoint, arrivalTime).forEach(System.out::println);
        }
    }

    public void listPoints() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n***Список поездов по пунктам отправления и назначения***\n");
        String departurePoint = getPoint(sc, "Введите пункт отправления");
        String destination = getPoint(sc, "Введите пункт назначения");
        System.out.println("\n***Список поездов***\n");
        getNotTransferTrains(departurePoint, destination).forEach(System.out::println);
    }

    public void printTrains() {
        System.out.println("\n***Список поездов***\n");
        trains.stream().sorted(new TrainComparator()).forEach(System.out::println);
    }

    private void printMessage(String message) {
        System.out.print(message + " -> ");
    }
}
