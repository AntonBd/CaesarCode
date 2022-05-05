import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.io.*;

public class CaesarCode {

    static String alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ.,:-!? ";
    static int key;
    static int variant;
    static String fileFrom;
    static String fileTo;
    static String ornament = "=-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.=";
    static String notFound = "Файл по данному адресу не найден.";
    static boolean badFile;
    static Map<Integer, String> versions = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        greet();
        select();
        receiveFilePath();

        if (variant == 1) {
            encrypt();
        }
        else {
            applyBruteForce();
        }

        if (!badFile) {
            report();
        }
    }

    public static void greet() {
        System.out.println("""
                
                Университет JavaRush, 3-ий поток
                Итоговая работа к модулю 1: Java Syntax
                Проект \u0094Написание криптоанализатора\u0093""");
        System.out.println(ornament);
        System.out.println("""
                Укажите ниже вариант работы программы:
                \u2460 Зашифровать текст с использованием шифра Цезаря или
                \u2461 Расшифровать текст с применением метода Brute force.""");
    }

    public static void select() {
        variant = enterNumber(2);

        if (variant == 1) {
            System.out.println("Укажите значение ключа в диапазоне от 1 до 72:");
            key = enterNumber(72);
        }
        System.out.println(ornament);
    }

    public static int enterNumber(int valueTo) {
        int number;

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                number = Integer.parseInt(scanner.nextLine());
                if (number > 0 && number < valueTo+1) {
                    break;
                }
                else {
                    System.out.println("Значение должно быть в диапазоне от 1 до " + valueTo);
                }
            }
            catch (NumberFormatException e) {
                System.out.println("Необходимо указать число");
            }
        }
        return number;
    }

    public static void receiveFilePath() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Укажите путь к файлу, в котором содержится текст:");
        fileFrom = scanner.nextLine();

        try {
            int lastSlash = fileFrom.lastIndexOf("\\");                 //Определяем позицию последнего \ в имени файла
            fileTo = fileFrom.substring(0, lastSlash);                   //Получаем путь к папке, где хранится исходный файл
            if (variant == 1) {
                fileTo = fileTo + "\\caesarcode.txt";                       //Файл для зашифрованного сообщения
            }
            else {
                fileTo = fileTo + "\\decryption.txt";                       //Файл для расшифрованного сообщения
            }
        }
        catch (RuntimeException e) {
            System.out.print("Произошла ошибка. ");
            return;
        }

        System.out.println("Ищу файл и выполняю задание:");
        for (int i = 0; i < 10; i++) {                                      //Имитируем кропотливую работу
            System.out.print("\u231B ");
            Thread.sleep(600);
        }
        System.out.println("\n" + ornament);
    }

    public static void encrypt() {
        try(FileReader reader = new FileReader(fileFrom);
            FileWriter writer = new FileWriter(fileTo)) {
            while (reader.ready()) {
                char x = (char)reader.read();                           //Читаем байт из потока и переводим его в символ

                int position = alphabet.indexOf(x);                     //Определяем текущую позицию символа в алфавите
                int sec_position = position + key;                      //Секретная позиция = позиция в алфавите + ключ

                if (position + key >= alphabet.length()) {              //Если позиция + ключ выходит за пределы длины алфавита
                    sec_position = sec_position - alphabet.length();    //Секретная позиция переходит в начало алфавита
                }
                writer.write(alphabet.charAt(sec_position));            //Пишем зашифрованный символ в файл
            }
        }
        catch (IOException e) {
            System.out.println(notFound);
            badFile = true;
        }
    }

    public static void applyBruteForce() {
        String text = "";
        String result = "";

        try(BufferedReader reader = new BufferedReader(new FileReader(fileFrom))) {
            while (reader.ready()) {
                text = text + reader.readLine();                                //Собираем построчно текст из файла
            }
        }
        catch (IOException e) {
            System.out.println(notFound);
            badFile = true;
        }

            //Цикл для перебора всех вариантов ключей
            for (int i = 0; i < alphabet.length(); i++) {
                key = i;

                //Цикл для декодировки текста
                //Цикл начинается с 1, т.к. в позиции 0 стоит служебный символ, который файл txt добавляет перед текстом
                //Если на других компьютерах txt не добавляет служебных символов, то int j следует указать = 0
                for (int j = 1; j < text.length(); j++) {
                    char x = text.charAt(j);                                    //Пишем в переменную текущий символ
                    int position = alphabet.indexOf(x);                         //Определяем текущую позицию символа в алфавите
                    int new_position = position - key;                          //Новая позиция: позиция в алфавите - ключ

                    if (new_position < 0) {                                     //Если новая позиция становится отрицательной
                        new_position = alphabet.length() + new_position;        //Новая позиция переходит в конец алфавита
                    }
                    result = result + alphabet.charAt(new_position);            //Собираем результат декодировки
                }

                cryptoAnalyse(result);                                          //Осуществляем анализ получившейся строки

            result = "";                                                        //Обнуляем результат для новой сборки
        }

        int max = 0;
        for(Map.Entry<Integer, String> pair : versions.entrySet()) {                 //Перебираем значения в Мар
            max = pair.getKey() > max ? pair.getKey() : max;                         //Ищем максимальное значение
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileTo))) {
            writer.write(versions.get(max));                                           //Записываем результат в файл
        }
        catch (IOException | NullPointerException e) {
            System.out.println("Возможно, указан неверный адрес.");
        }
    }

    public static void cryptoAnalyse(String result) {
        /** Логика отбора вариантов:
        * 1. Текст должен иметь хотя бы один пробел;
        * 2. Текст должен начинаться с буквы;
        * 3. В тексте знаки препинания не должны идти внутри слова;
        * 4. Первое слово начинается с заглавной буквы, остальные буквы в слове - строчные     = 3 балла;
        * 5. Первое слово содержит только строчные или только заглавные буквы                  = 2 балла;
        * 6. В тексте присутствуют пробелы после запятой, двоеточия или тире                   = 1 балл;
        * 7. Текст заканчивается на точку, вопросительный или восклицательный знак             = 1 балл;
        * 8. Текст имеет адекватное соотношение количества пробелов к количеству знаков        = 2 или 1 балл;
        * 9. Текст должен набрать хотя бы 2 балла.
        * Количество баллов выбрано по значимости параметра в логике обычного построения письменной речи.
        */
        result = result.trim();                                             //Удаляем лишние пробелы, если они есть

        if (!result.contains(" ")) {                                        //1
            return;
        }
        if (result.matches("\\p{Punct}.*")) {                       //2
            return;
        }

        if (result.matches(".*(\\?|:|,|!|\\.)[а-яА-Я]+.*")) {         //3 Написание этого выражения заняло два вечера
            return;
        }

        //Варианты, прошедшие отсеивание по пп. 1 - 3, набирают баллы
        int bonus = 0;
        String[] sentense = result.split("\\s");                      //Делим текст, чтобы выделить первое слово
        if (sentense[0].matches("[А-Я][а-я]*(,|:)*")) {               //4
            bonus = bonus + 3;
        }
        else if (sentense[0].matches("[А-Я]*|[а-я]*(,|:)*")) {        //5
            bonus = bonus + 2;
        }

        if (result.matches(".*(, |: | - ).*")) {                      //6
            bonus = bonus + 1;
        }
        if (result.matches(".*(\\.|\\?|!)")) {                        //7
            bonus = bonus + 1;
        }

                                                                    //8
        int letters_count = result.length();                        //Общее количество символов в тексте
        int spaces_count = sentense.length-1;                       //Количество пробелов в тексте
        int proportion = spaces_count*100/letters_count;            //Вычисляем отношение пробелов к количеству символов
        if (proportion > 11) {                                      //Соотношение близко к норме русского языка
            bonus = bonus + 2;
        }
        else if (proportion > 7) {                                  //Минимально адекватное количество пробелов
            bonus = bonus + 1;
        }

        if (bonus < 2) {                                             //9
            return;
        }

        versions.put(bonus, result);                                      //Записываем значение в Map
    }

    public static void report() {
        String prefix = (variant == 1) ? "За" : "Рас";
        System.out.println(prefix + "шифровка текста выполнена.");
        System.out.println("Результат размещен по адресу: " + fileTo);
    }
}