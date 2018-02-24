package ru.prokurornsk;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class SortFile {

    private Path inPath;
    private Path outPath;
    private Type type;
    private Order order;

    /*Значения по умолчанию:
     * inPath - может быть просто имя файла относительно рабочей дирректории
     * outPath - out.txt рядом с файлом источником
     * type - строка
     * order - по возрастанию*/
    public SortFile(String inString) {
        inPath = Paths.get(inString).toAbsolutePath();
        outPath = Paths.get(inPath.getParent() + "\\out.txt");
        type = Type.STRING;
        order = Order.ASCENDING;
    }

    public SortFile(String inString, String outString) {
        this(inString);
        outPath = Paths.get(outString).toAbsolutePath();
    }

    public SortFile(String inString, String outString, String typeString) {
        this(inString, outString);
        type = Type.findEnum(typeString);
    }

    public SortFile(String inString, String outString, String typeString, String orderString) {
        this(inString, outString, typeString);
        order = Order.findEnum(orderString);
    }

    public static void main(String[] args) {
        SortFile sortFile;

        try {
            switch (args.length) {
                case 0:
                    System.out.println("Ошибка! Должен быть указан хотя бы один параметр - путь к файлу источнику");
                    return;
                case 1:
                    sortFile = new SortFile(args[0]);
                    break;
                case 2:
                    sortFile = new SortFile(args[0], args[1]);
                    break;
                case 3:
                    sortFile = new SortFile(args[0], args[1], args[2]);
                    break;
                default:
                    sortFile = new SortFile(args[0], args[1], args[2], args[3]);
                    break;
            }

            List<String> lines = Files.readAllLines(sortFile.inPath);
            lines = sortFile.insertionSort(lines);
            Files.deleteIfExists(sortFile.outPath);
            Files.write(sortFile.outPath, lines, StandardOpenOption.CREATE);

        } catch (InvalidPathException e) {
            System.out.printf("Некорректные символы в пути - %s", e.getMessage());
        } catch (NumberFormatException e) {
            System.out.printf("Невозможно преобразовать строку к числу - %s", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.printf("Некорректный параметр типа данных или порядка сортировки %s", e.getMessage());
        } catch (IOException e) {
            System.out.printf("Произошла ошибка чтения или записи файла - %s", e.getMessage());
        }

    }

    public List<String> insertionSort(List<String> lines) throws NumberFormatException {
        List<String> result = new ArrayList<>();
        result.add(lines.get(0));

        for (int i = 1; i < lines.size(); i++) {
            int j;
            //Пустой цикл для определения позиции вставки.
            for (j = i; j > 0 && isStop(lines.get(i), result.get(j - 1)); j--) {}
            result.add(j, lines.get(i));
        }

        return result;
    }

    private boolean isStop(String one, String two) {
        boolean result = true;
        //Можно расширять на любой тип
        switch (type) {
            case STRING:
                result = one.compareTo(two) <= 0;
                break;
            case INTEGER:
                result = (Integer.parseInt(one) <= Integer.parseInt(two));
                break;
            case FLOAT:
                result = (Float.parseFloat(one) <= Float.parseFloat(two));
                break;
        }
        return (order == Order.ASCENDING) == result;
    }

    private enum Type {
        INTEGER("-i"),
        STRING("-s"),
        FLOAT("-f");

        private final String parameter;

        Type(String parameter) {
            this.parameter = parameter;
        }

        static Type findEnum(String parameter) throws IllegalArgumentException {
            for (Type type : Type.values()) {
                if (parameter.equals(type.parameter)) {
                    return type;
                }
            }
            throw new IllegalArgumentException(parameter);
        }
    }

    private enum Order {
        ASCENDING("-a"),
        DESCENDING("-d");

        private final String parameter;

        Order(String parameter) {
            this.parameter = parameter;
        }

        static Order findEnum(String parameter) throws IllegalArgumentException {
            for (Order type : Order.values()) {
                if (parameter.equals(type.parameter)) {
                    return type;
                }
            }
            throw new IllegalArgumentException(parameter);
        }
    }
}
