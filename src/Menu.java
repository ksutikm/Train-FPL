import java.util.Scanner;

import static java.lang.System.exit;

public class Menu {

    private TrainList trains;

    public Menu() {
        this.trains = new TrainList();
    }

    public void getMenu() {
        try (Scanner sc = new Scanner(System.in)){
            while (true) {
                printMenu();
                switch (sc.nextInt()) {
                    case 1:
                        trains.addTrain();
                        System.out.println("\nПоезд добавлен!");
                        break;
                    case 2:
                        int num = trains.removeTrain();
                        if (num != -1) {
                            System.out.println("\nПоезд №" + num + " удален!");
                        }
                        break;
                    case 3:
                        trains.moveTrain();
                        break;
                    case 4:
                        trains.listPointAndTime();
                        break;
                    case 5:
                        trains.listPoints();
                        break;
                    case 6:
                        trains.printTrains();
                        break;
                    case 7:
                        exit(0);
                        break;
                    default:
                        System.out.println("Введён неверный номер!");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
}
