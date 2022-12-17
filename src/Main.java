import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    static ArrayBlockingQueue<String> abqA = new ArrayBlockingQueue<>(100);
    static ArrayBlockingQueue<String> abqB = new ArrayBlockingQueue<>(100);
    static ArrayBlockingQueue<String> abqC = new ArrayBlockingQueue<>(100);

    static AtomicInteger intA = new AtomicInteger(0);
    static AtomicInteger intB = new AtomicInteger(0);
    static AtomicInteger intC = new AtomicInteger(0);

    static AtomicReference <String> strA = new AtomicReference<>();
    static AtomicReference <String> strB = new AtomicReference<>();
    static AtomicReference <String> strC = new AtomicReference<>();

    public static void main(String[] args) throws InterruptedException {

        Thread threadText = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    abqA.put(text);
                    abqB.put(text);
                    abqC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        threadText.start();

        Thread threadA = new Thread(() -> {
            for (int k = 0; k < 10_000; k++) {
                try {
                    AtomicInteger intValue = new AtomicInteger(0);
                    String textTakeA = abqA.take();
                    for (int i = 0; i < textTakeA.length(); i++) {
                        if (textTakeA.charAt(i) == 'a') {
                            intValue.getAndIncrement();
                        }
                    }

                    if (intValue.get() > intA.get()) {
                        intA = intValue;
                        strA.set(textTakeA);
                    }

                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread threadB = new Thread(() -> {
            for (int k = 0; k < 10_000; k++) {
                try {
                    AtomicInteger intValue = new AtomicInteger(0);
                    String textTakeB = abqB.take();
                    for (int i = 0; i < textTakeB.length(); i++) {
                        if (textTakeB.charAt(i) == 'b') {
                            intValue.getAndIncrement();
                        }
                    }

                    if (intValue.get() > intB.get()) {
                        intB = intValue;
                        strB.set(textTakeB);
                    }

                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread threadC = new Thread(() -> {
            for (int k = 0; k < 10_000; k++) {
                try {
                    AtomicInteger intValue = new AtomicInteger(0);
                    String textTakeC = abqC.take();
                    for (int i = 0; i < textTakeC.length(); i++) {
                        if (textTakeC.charAt(i) == 'c') {
                            intValue.getAndIncrement();
                        }
                    }

                    if (intValue.get() > intC.get()) {
                        intC = intValue;
                        strC.set(textTakeC);
                    }

                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        threadA.start();
        threadB.start();
        threadC.start();

        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("Самое максимальное количество (" + intA.get() + ") символов 'a' в строке " + strA.get());
        System.out.println("Самое максимальное количество (" + intB.get() + ") символов 'b' в строке " + strB.get());
        System.out.println("Самое максимальное количество (" + intC.get() + ") символов 'c' в строке " + strC.get());
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}