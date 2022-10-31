import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import static java.lang.System.exit;

public class TrainList {
    private ArrayList<Train> trains;

    Scanner sc = new Scanner(System.in);
    private Comparator<Train> comparatorTrain = new TrainComparator();
    private static int number;
    private static String departurePoint;
    private static String destination;
    private static String departureTime;
    private static String arrivalTime;
    private static double ticketPrice;

    TrainList() {
        this.trains = new ArrayList<>();
        scannerData();
    }

    private void scannerData() {
        try (Scanner in = new Scanner(new File("src/data.txt"))) {
            while (in.hasNextLine()) {
                String[] str = in.nextLine().split(",");
                if (str.length != 6 || !str[0].matches("[0-9]+") || !str[5].matches("[0-9]+.[0-9]+") ||
                        !str[3].matches("[0-9][0-9]:[0-9][0-9]") || !str[4].matches("[0-9][0-9]:[0-9][0-9]"))
                    throw new IOException();
                String[] s1 = str[3].split(":");
                String[] s2 = str[4].split(":");
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
        try {
            String s1[], s2[];

            System.out.println("\n***Добавление поезда***\n");

            System.out.print("Введите номер поезда -> ");
            number = sc.nextInt();
            if (number <= 0) throw new MyException("Ошибка!!! Введен неверный номер поезда!");

            System.out.print("Введите пункт отправления -> ");
            departurePoint = sc.next();

            System.out.print("Введите пункт назначения -> ");
            destination = sc.next();

            System.out.print("Введите время отправления -> ");
            departureTime = sc.next();
            if (!departureTime.matches("[0-9][0-9]:[0-9][0-9]"))
                throw new MyException("Ошибка!!! Введен неверный формат времени!");
            s1 = departureTime.split(":");
            if (Integer.parseInt(s1[0]) >= 24 || Integer.parseInt(s1[1]) >= 60)
                throw new MyException("Ошибка!!! Введен неверный формат времени!");

            System.out.print("Введите время прибытия -> ");
            arrivalTime = sc.next();
            if (!arrivalTime.matches("[0-9][0-9]:[0-9][0-9]"))
                throw new MyException("Ошибка!!! Введен неверный формат времени!");
            s2 = arrivalTime.split(":");
            if (Integer.parseInt(s2[0]) >= 24 || Integer.parseInt(s2[1]) >= 60)
                throw new MyException("Ошибка!!! Введен неверный формат времени!");

            System.out.print("Введите стоимость билета -> ");
            ticketPrice = sc.nextDouble();
            if (ticketPrice <= 0.0) throw new MyException("Ошибка!!! Введена неверная стоимость билета!");

            LocalTime t1 = LocalTime.of(Integer.parseInt(s1[0]), Integer.parseInt(s1[1]));
            LocalTime t2 = LocalTime.of(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]));
            Train t = new Train(number, departurePoint, destination, t1, t2, ticketPrice);
            trains.add(t);

        } catch (MyException me) {
            System.out.println(me.getMessage());
        }
    }

    public int removeTrain() {
        int num = -1;
        try {
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
        ArrayList<Train> moveTrains = new ArrayList<>();
        System.out.println("\n***Маршрут поездки по пунктам отправления и назначения***\n");
        System.out.print("Введите пункт отправления -> ");
        departurePoint = sc.next();

        System.out.print("Введите пункт назначения -> ");
        destination = sc.next();

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
        trains.stream().distinct().sorted(comparatorTrain).forEach(System.out::println);

        System.out.println("\n***Список поездов без пересадки***\n");
        trains.stream().filter(t -> t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase()))
                .filter(train -> train.getDestination().toLowerCase().equals(destination.toLowerCase()))
                .sorted(comparatorTrain).forEach(System.out::println);

    }

    public void listPointAndTime() {
        try {
            String s2[];

            System.out.println("\n***Список поездов по пункту отправления и времени прибытия***\n");

            System.out.print("Введите пункт отправления -> ");
            departurePoint = sc.next();

            System.out.print("Введите время прибытия -> ");
            arrivalTime = sc.next();
            if (!arrivalTime.matches("[0-9][0-9]:[0-9][0-9]"))
                throw new MyException("Ошибка!!! Введен неверный формат времени!");
            s2 = arrivalTime.split(":");
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

        System.out.println("\n***Список поездов по пунктам отправления и назначения***\n");
        System.out.print("Введите пункт отправления -> ");
        departurePoint = sc.next();

        System.out.print("Введите пункт назначения -> ");
        destination = sc.next();

        System.out.println("\n***Список поездов***\n");

        trains.stream().filter(t -> t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase()))
                .filter(train -> train.getDestination().toLowerCase().equals(destination.toLowerCase()))
                .sorted(comparatorTrain).forEach(System.out::println);
    }

    public void printTrains() {
        System.out.println("\n***Список поездов***\n");
        trains.stream().sorted(comparatorTrain).forEach(System.out::println);
    }
}
