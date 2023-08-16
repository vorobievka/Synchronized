import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        Character r = 'R';
        List<Thread> threads = new ArrayList<Thread>();

        Runnable searchMax = () -> {
            while (!Thread.interrupted()) {
                try {
                    synchronized (sizeToFreq) {
                        sizeToFreq.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException("Печатающий поток остановлен");
                }
                Integer key = Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getKey();
                System.out.println("Самое частое количество повторений " + key + " (встретилось " + sizeToFreq.get(key) + " раз)");
            }

        };

        Thread threadForSearchMax = new Thread(searchMax);
        threadForSearchMax.start();

        for (int j = 0; j < 1000; j++) {
            String result = generateRoute("RLRFR", 100);
            Runnable logic = () -> {
                int count = 0;
                for (int i = 0; i < result.length(); i++) {
                    if (r.equals(result.charAt(i))) {
                        count++;
                    }
                }
                synchronized (sizeToFreq) {
                    if (sizeToFreq.get(count) != null) {
                        sizeToFreq.put(count, sizeToFreq.get(count) + 1);
                        sizeToFreq.notify();
                    } else {
                        sizeToFreq.put(count, 1);
                        sizeToFreq.notify();
                    }
                }
            };

            Thread thread = new Thread(logic);
            thread.start();
            threads.add(thread);

        }

        for (Thread threading : threads) {
            threading.join();
        }
        threadForSearchMax.interrupt();

    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

}