import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.exit;

public class TrainList {
    private ArrayList<Train> trains;

    TrainList() {
        this.trains = new ArrayList<>();
        scannerData();
    }

    private String getPoint(Scanner sc, String message) {
        printMessage(message);
        return sc.next();
    }

    private int getNumberTrain(Scanner sc) {
        printMessage("Введите номер поезда");
        int number =sc.nextInt();
        if (number <= 0) try {
            throw new MyException("Ошибка!!! Введен неверный номер поезда!");
        } catch (MyException e) {
            e.printStackTrace();
        }
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
        }  catch (MyException me) {
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

    private void scannerData() {
        try (Scanner in = new Scanner(new File("src/data.txt"))) {
            while (in.hasNextLine()) {
                String[] str = getArrayStrings(in.nextLine(), ",");
                if (str.length != 6 || !str[0].matches("[0-9]+") || !str[5].matches("[0-9]+.[0-9]+") ||
                        !str[3].matches("[0-9][0-9]:[0-9][0-9]") || !str[4].matches("[0-9][0-9]:[0-9][0-9]"))
                    throw new IOException();
                String[] s1 = getArrayStrings(str[3], ":");
                String[] s2 = getArrayStrings(str[3], ":");
                LocalTime t1 = LocalTime.of(Integer.parseInt(s1[0]), Integer.parseInt(s1[1]));
                LocalTime t2 = LocalTime.of(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]));
                trains.add(new Train(Integer.parseInt(str[0]), str[1], str[2], t1, t2, Double.parseDouble(str[5])));
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
        trains.add(new Train(
                getNumberTrain(sc),
                getPoint(sc, "Введите пункт отправления"),
                getPoint(sc, "Введите пункт назначения"),
                getTime(sc, "Введите время отправления"),
                getTime(sc, "Введите время прибытия"),
                getTicketPrice(sc)
        ));
    }

    public int removeTrain() {
        int num = -1;
        try(Scanner sc = new Scanner(System.in)) {
            System.out.println("\n***Удаление поезда***");
            printTrains();
            System.out.print("Введите номер поезда -> ");
            num = sc.nextInt();
            if (num <= 0) throw new MyException("Ошибка!!! Введен отрицательный или нулевой номер поезда!");
            boolean flag = false;
            int i = 0;
            for (Train t : trains) {
                if (t.getNumber() == num) {
                    flag = true;
                    break;
                }
                i++;
            }

            if (flag) {
                trains.remove(i);
            } else {
                num = -1;
                throw new MyException("Введен номер поезда, которого нет в списках!");
            }

        } catch (MyException me) {
            System.out.println(me.getMessage());
        }
        return num;
    }

    public void moveTrain() {
        Scanner sc = new Scanner(System.in);
        ArrayList<Train> moveTrains = new ArrayList<>();
        System.out.println("\n***Маршрут поездки по пунктам отправления и назначения***\n");
        System.out.print("Введите пункт отправления -> ");
        String departurePoint = sc.next();

        System.out.print("Введите пункт назначения -> ");
        String destination = sc.next();

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

        System.out.println("\n***Список поездов с пересадкой***\n");
        trains.stream().distinct().sorted(new TrainComparator()).forEach(System.out::println);

        System.out.println("\n***Список поездов без пересадки***\n");
        trains.stream().filter(t -> t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase()))
                .filter(train -> train.getDestination().toLowerCase().equals(destination.toLowerCase()))
                .sorted(new TrainComparator()).forEach(System.out::println);

    }

    public void listPointAndTime() {
        try (Scanner sc = new Scanner(System.in)){
            String s2[];

            System.out.println("\n***Список поездов по пункту отправления и времени прибытия***\n");

            System.out.print("Введите пункт отправления -> ");
            String departurePoint = sc.next();

            System.out.print("Введите время прибытия -> ");
            String arrivalTime = sc.next();
            if (!arrivalTime.matches("[0-9][0-9]:[0-9][0-9]"))
                throw new MyException("Ошибка!!! Введен неверный формат времени!");
            s2 = getArrayStrings(arrivalTime, ":");
            if (Integer.parseInt(s2[0]) >= 24 || Integer.parseInt(s2[1]) >= 60)
                throw new MyException("Ошибка!!! Введен неверный формат времени!");

            LocalTime t2 = LocalTime.of(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]));

            System.out.println("\n***Список поездов***\n");

            trains.stream().filter(t -> t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase()))
                    .filter(t -> t2.equals(t.getArrivalTime())).forEach(System.out::println);

        } catch (MyException me) {
            System.out.println(me.getMessage());
        }
    }

    public void listPoints() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n***Список поездов по пунктам отправления и назначения***\n");
        System.out.print("Введите пункт отправления -> ");
        String departurePoint = sc.next();

        System.out.print("Введите пункт назначения -> ");
        String destination = sc.next();

        System.out.println("\n***Список поездов***\n");

        trains.stream().filter(t -> t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase()))
                .filter(train -> train.getDestination().toLowerCase().equals(destination.toLowerCase()))
                .sorted(new TrainComparator()).forEach(System.out::println);
    }

    public void printTrains() {
        System.out.println("\n***Список поездов***\n");
        trains.stream().sorted(new TrainComparator()).forEach(System.out::println);
    }

    private void printMessage(String message) {
        System.out.print(message + " -> ");
    }
}
