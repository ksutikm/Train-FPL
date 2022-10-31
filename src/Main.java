/*
3. Написать программу, моделирующую информационную систему на железнодорожном вокзале.
   Сведения о каждом поезде содержат:
        - номер,
        - пункт отправления,
        - пункт назначения,
        - время отправления,
        - время прибытия,
        - стоимость билета.
   Программа должна создавать список поездов.
   Начальное формирование данных осуществляется из файла (или файлов).
   С помощью меню необходимо обеспечить следующие функции:
        a) добавление поезда;
        b) удаление поезда;
        c) по пунктам отправления и назначения разработать маршрут поездки, возможно с пересадками;
        d) вывод всех поездов по заданному пункту отправления и времени прибытия;
        e) вывод всех поездов по заданному пункту отправления и назначения.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import static java.lang.System.exit;

public class Main {

    private static ArrayList<Train> trainsList = new ArrayList<>();
    private static ArrayList<Train> trainsTwo = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    private static Comparator<Train> comparatorTrain = new ComparatorTrain();

    private static void scannerData() {
        try{
            Scanner in = new Scanner(new File("src/data.txt"));
            while (in.hasNextLine()) {
                String []str = in.nextLine().split(",");
                if(str.length!=6 || !str[0].matches("[0-9]+") || !str[5].matches("[0-9]+.[0-9]+") ||
                        !str[3].matches("[0-9][0-9]:[0-9][0-9]") || !str[4].matches("[0-9][0-9]:[0-9][0-9]"))
                    throw new IOException();
                String []s1 = str[3].split(":");
                String []s2 = str[4].split(":");
                LocalTime t1 = LocalTime.of(Integer.parseInt(s1[0]), Integer.parseInt(s1[1]));
                LocalTime t2 = LocalTime.of(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]));
                trainsList.add(new Train(Integer.parseInt(str[0]), str[1], str[2], t1, t2, Double.parseDouble(str[5])));
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("К сожалению, данные из файла не загрузились.");
        } catch (IOException e) {
            System.out.println("Ошибка!!! Некорректные данные файла.");
            exit(0);
        }
    }

    private static void printMenu() {
        System.out.println("\n***Выберите номер***\n");
        System.out.println("1 - Добавление поезда");
        System.out.println("2 - Удаление поезда");
        System.out.println("3 - Маршрут поездки по пунктам отправления и назначения");
        System.out.println("4 - Вывести список поездов по пункту отправления и времени прибытия");
        System.out.println("5 - Вывести список поездов по пунктам отправления и назначения");
        System.out.println("6 - Вывести список поездов");
        System.out.println("7 - Завершить работу приложения");
        System.out.print("\nВведите номер -> ");
    }

    private static void menu() {
        try {
            int value = 0;
            while (value != 7) {
                printMenu();
                value = sc.nextInt();

                switch (value) {
                    case 1:
                        addTrain();
                        System.out.println("\nПоезд добавлен!");
                        break;
                    case 2:
                        int num = removeTrain();
                        if (num != -1) {
                            System.out.println("\nПоезд №" + num + " удален!");
                        }
                        break;
                    case 3:
                        moveTrain();
                        break;
                    case 4:
                        listPointAndTime();
                        break;
                    case 5:
                        listPoints();
                        break;
                    case 6:
                        printTrains(trainsList);
                        break;
                    case 7:
                        break;
                    default:
                        System.out.println("Введён неверный номер!");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static int number;
    private static String departurePoint;
    private static String destination;
    private static String departureTime;
    private static String arrivalTime;
    private static double ticketPrice;

    private static void addTrain() {
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
            if (!departureTime.matches("[0-9][0-9]:[0-9][0-9]")) throw new MyException("Ошибка!!! Введен неверный формат времени!");
            s1 = departureTime.split(":");
            if (Integer.parseInt(s1[0]) >= 24 || Integer.parseInt(s1[1]) >= 60) throw new MyException("Ошибка!!! Введен неверный формат времени!");

            System.out.print("Введите время прибытия -> ");
            arrivalTime = sc.next();
            if (!arrivalTime.matches("[0-9][0-9]:[0-9][0-9]")) throw new MyException("Ошибка!!! Введен неверный формат времени!");
            s2 = arrivalTime.split(":");
            if (Integer.parseInt(s2[0]) >= 24 || Integer.parseInt(s2[1]) >= 60) throw new MyException("Ошибка!!! Введен неверный формат времени!");

            System.out.print("Введите стоимость билета -> ");
            ticketPrice = sc.nextDouble();
            if (ticketPrice <= 0.0) throw new MyException("Ошибка!!! Введена неверная стоимость билета!");

            LocalTime t1 = LocalTime.of(Integer.parseInt(s1[0]), Integer.parseInt(s1[1]));
            LocalTime t2 = LocalTime.of(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]));
            Train t = new Train(number, departurePoint, destination, t1, t2, ticketPrice);
            trainsList.add(t);

        } catch (MyException me) {
            System.out.println(me.getMessage());
        }
    }

    private static int removeTrain() {
        int num = -1;
        try {
            System.out.println("\n***Удаление поезда***");
            printTrains(trainsList);
            System.out.print("Введите номер поезда -> ");
            num = sc.nextInt();
            if (num <= 0) throw new MyException("Ошибка!!! Введен отрицательный или нулевой номер поезда!");
            boolean flag = false;
            int i = 0;
            for (Train t: trainsList) {
                if (t.getNumber() == num) {
                    flag = true;
                    break;
                }
                i++;
            }

            if (flag) {
                trainsList.remove(i);
            } else {
                num = -1;
                throw new MyException("Введен номер поезда, которого нет в списках!");
            }

        } catch (MyException me) {
            System.out.println(me.getMessage());
        }
        return num;
    }

    private static void moveTrain() {
        ArrayList<Train> trains = new ArrayList<>();
        System.out.println("\n***Маршрут поездки по пунктам отправления и назначения***\n");
        System.out.print("Введите пункт отправления -> ");
        departurePoint = sc.next();

        System.out.print("Введите пункт назначения -> ");
        destination = sc.next();

        for (Train t: trainsList) {
            if (t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase())) {
                for (Train t2: trainsList) {
                    if (t.getDestination().toLowerCase().equals(t2.getDeparturePoint().toLowerCase()) &&
                            t2.getDestination().toLowerCase().equals(destination.toLowerCase())) {
                        trains.add(t);
                        trains.add(t2);
                    }
                }
            }
        }

        System.out.println("\n***Список поездов с пересадкой***\n");
        trains.stream().distinct().sorted(comparatorTrain).forEach(System.out::println);

        System.out.println("\n***Список поездов без пересадки***\n");
        trainsList.stream().filter(t -> t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase()))
                .filter(train -> train.getDestination().toLowerCase().equals(destination.toLowerCase()))
                .sorted(comparatorTrain).forEach(System.out::println);

    }

    private static void listPointAndTime() {
        try {
            String s2[];

            System.out.println("\n***Список поездов по пункту отправления и времени прибытия***\n");

            System.out.print("Введите пункт отправления -> ");
            departurePoint = sc.next();

            System.out.print("Введите время прибытия -> ");
            arrivalTime = sc.next();
            if (!arrivalTime.matches("[0-9][0-9]:[0-9][0-9]")) throw new MyException("Ошибка!!! Введен неверный формат времени!");
            s2 = arrivalTime.split(":");
            if (Integer.parseInt(s2[0]) >= 24 || Integer.parseInt(s2[1]) >= 60) throw new MyException("Ошибка!!! Введен неверный формат времени!");

            LocalTime t2 = LocalTime.of(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]));

            System.out.println("\n***Список поездов***\n");

            trainsList.stream().filter(t -> t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase()))
                    .filter(t -> t2.equals(t.getArrivalTime())).forEach(System.out::println);

        } catch (MyException me) {
            System.out.println(me.getMessage());
        }
    }

    private static void listPoints() {
        System.out.println("\n***Список поездов по пунктам отправления и назначения***\n");
        System.out.print("Введите пункт отправления -> ");
        departurePoint = sc.next();

        System.out.print("Введите пункт назначения -> ");
        destination = sc.next();

        System.out.println("\n***Список поездов***\n");

        trainsList.stream().filter(t -> t.getDeparturePoint().toLowerCase().equals(departurePoint.toLowerCase()))
                .filter(train -> train.getDestination().toLowerCase().equals(destination.toLowerCase()))
                .sorted(comparatorTrain).forEach(System.out::println);
    }

    private static void printTrains(ArrayList<Train> trains) {
        System.out.println("\n***Список поездов***\n");
        trains.stream().sorted(comparatorTrain).forEach(System.out::println);
    }

    static class ComparatorTrain implements Comparator<Train> {

        @Override
        public int compare(Train t1, Train t2) {
            return t1.getNumber() > t2.getNumber() ? 1: (t1.getNumber() < t2.getNumber()) ? -1: 0;
        }
    }

    public static void main(String[] args) {
        scannerData();
        menu();
    }
}
