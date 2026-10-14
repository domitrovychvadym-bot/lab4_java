import java.io.*;
import java.util.*;

/**
 * Лабораторна робота №4. Файли.
 * Варіант 7.
 */
public class Main {

    public static void main(String[] args) {
        // Забезпечення буферизованого консольного введення
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("\n==================================================");
            System.out.println("   ЛАБОРАТОРНА РОБОТА №4 (ВАРІАНТ 7)");
            System.out.println("==================================================");
            System.out.println("1 - Завдання 1 (Пошук слів та вилучення дублікатів)");
            System.out.println("2 - Завдання 2 (Підрахунок слів у текстовому файлі)");
            System.out.println("0 - Вихід з програми");
            System.out.print("Оберіть пункт меню: ");

            try {
                String choice = consoleReader.readLine();
                if (choice == null || choice.trim().equals("0")) {
                    System.out.println("Роботу програми завершено.");
                    break;
                }

                switch (choice.trim()) {
                    case "1":
                        runTask1(consoleReader);
                        break;
                    case "2":
                        runTask2(consoleReader);
                        break;
                    default:
                        System.out.println("Некоректна опція. Спробуйте ще раз.");
                }
            } catch (IOException e) {
                System.out.println("Помилка введення/виведення: " + e.getMessage());
            }
        }
    }

    // =========================================================================
    // ЗАВДАННЯ 1 (Варіант 7)
    // =========================================================================
    private static void runTask1(BufferedReader reader) throws IOException {
        System.out.println("\n--- ЗАВДАННЯ 1 ---");

        // 1. Отримання та перевірка вхідного файлу зі списком слів
        File searchFile = getFileInput(reader, "Введіть шлях до файлу зі списком слів для пошуку: ");
        if (searchFile == null) return;

        // 2. Отримання та перевірка файлу з текстом
        File textFile = getFileInput(reader, "Введіть шлях до файлу з текстом: ");
        if (textFile == null) return;

        // Зчитати слова з файлів
        List<String> searchWords = readWordsFromFile(searchFile);
        List<String> textWords = readWordsFromFile(textFile);

        // Підрахунок кількості входжень кожного слова у текст
        Map<String, Integer> occurrences = new HashMap<>();
        for (String searchWord : searchWords) {
            int count = 0;
            for (String textWord : textWords) {
                if (textWord.equalsIgnoreCase(searchWord)) {
                    count++;
                }
            }
            occurrences.put(searchWord, count);
        }

        // Сортування списку пошукових слів
        List<String> sortedSearchWords = new ArrayList<>(searchWords);
        sortedSearchWords.sort(String.CASE_INSENSITIVE_ORDER);

        // Виведення результатів пошуку на консоль
        System.out.println("\n=== Відсортований список слів та кількість їх входжень ===");
        for (String word : sortedSearchWords) {
            System.out.println(word + " -> " + occurrences.getOrDefault(word, 0));
        }

        // 3. Отримання вихідного файлу для зберігання унікальних слів з тексту
        File outputFile = getFileOutput(reader, "\nВведіть шлях до вихідного файлу для збереження результату: ");
        if (outputFile == null) {
            System.out.println("Запис у файл скасовано.");
            return;
        }

        boolean appendMode = getAppendChoice(reader);

        // Видалення дублікатів та сортування слів із тексту за допомогою TreeSet
        Set<String> uniqueTextWords = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        uniqueTextWords.addAll(textWords);

        // Запис результату у файл
        writeWordsToFile(outputFile, uniqueTextWords, appendMode);
        System.out.println("Результат успішно збережено у файл: " + outputFile.getAbsolutePath());
    }

    // =========================================================================
    // ЗАВДАННЯ 2 (Варіант 7)
    // =========================================================================
    private static void runTask2(BufferedReader reader) throws IOException {
        System.out.println("\n--- ЗАВДАННЯ 2 ---");

        while (true) {
            File file = getFileInput(reader, "Введіть шлях до текстового файлу (або 'back' для виходу в меню): ");
            if (file == null) {
                break;
            }

            int wordCount = countWordsInFile(file);
            System.out.println("Назва файлу: " + file.getName());
            System.out.println("Кількість слів у файлі: " + wordCount);
            System.out.println("--------------------------------------------------");

            System.out.print("Обробити ще один файл? (y/n): ");
            String answer = reader.readLine();
            if (answer == null || !answer.trim().equalsIgnoreCase("y")) {
                break;
            }
        }
    }

    // =========================================================================
    // ДОПОМІЖНІ МЕТОДИ
    // =========================================================================

    private static File getFileInput(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String input = reader.readLine();
            if (input == null) return null;
            input = input.trim();

            if (input.equalsIgnoreCase("back")) {
                return null;
            }

            File file = new File(input);
            if (file.exists() && file.isFile()) {
                return file;
            } else {
                System.out.println("Помилка: Файл не знайдено або це директорія. Повторіть введення.");
            }
        }
    }

    private static File getFileOutput(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String input = reader.readLine();
            if (input == null) return null;
            input = input.trim();

            File file = new File(input);
            if (!file.exists()) {
                System.out.print("Вихідний файл не існує. Створити його? (y/n): ");
                String choice = reader.readLine();
                if (choice != null && (choice.trim().equalsIgnoreCase("y") || choice.trim().equalsIgnoreCase("так"))) {
                    if (file.createNewFile()) {
                        System.out.println("Файл створено.");
                        return file;
                    } else {
                        System.out.println("Не вдалося створити файл. Спробуйте вказати інший шлях.");
                    }
                }
            } else if (file.isFile()) {
                return file;
            } else {
                System.out.println("Помилка: Вказано каталог, а не файл.");
            }
        }
    }

    private static boolean getAppendChoice(BufferedReader reader) throws IOException {
        System.out.print("Оберіть режим запису [1 - перезаписати файл, 2 - дописати в кінець]: ");
        String mode = reader.readLine();
        return mode != null && mode.trim().equals("2");
    }

    private static List<String> readWordsFromFile(File file) {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("[\\s\\p{Punct}]+");
                for (String token : tokens) {
                    if (!token.trim().isEmpty()) {
                        words.add(token.trim());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Помилка під час читання файлу: " + e.getMessage());
        }
        return words;
    }

    private static void writeWordsToFile(File file, Iterable<String> words, boolean append) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, append))) {
            for (String word : words) {
                bw.write(word);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Помилка під час запису у файл: " + e.getMessage());
        }
    }

    private static int countWordsInFile(File file) {
        int wordCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] words = line.split("\\s+");
                    wordCount += words.length;
                }
            }
        } catch (IOException e) {
            System.out.println("Помилка читання файлу: " + e.getMessage());
        }
        return wordCount;
    }
}