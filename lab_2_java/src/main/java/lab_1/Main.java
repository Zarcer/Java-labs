package lab_1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    private final AtomicLong totalSortSteps = new AtomicLong(0);
    private final MyList<String> custom;
    private final List<String> libList;
    private final int delayMillis;
    private final int workersCount;
    private final boolean useCustom;

    private final List<BubbleWorker> workers = new ArrayList<>();

    public Main(int delayMillis, int workersCount, boolean useCustom) {
        this.delayMillis = delayMillis;
        this.workersCount = workersCount;
        this.useCustom = useCustom;

        if (useCustom) {
            custom = new MyList<>();
            libList = null;
        } else {
            libList = Collections.synchronizedList(new ArrayList<>());
            custom = null;
        }
    }

    private void launch() {
        for (int i = 1; i <= workersCount; i++) {
            BubbleWorker w = new BubbleWorker(i);
            workers.add(w);
            w.start();
        }

        Scanner sc = new Scanner(System.in);
        while (true) {
            String line = sc.nextLine();
            if (line.isEmpty()) {
                displayContents();
            } else {
                pushInput(line);
            }
        }
    }

    private void pushInput(String line) {
        if (useCustom) {
            custom.add(line);
        } else {
            libList.add(0, line);
        }
    }

    private void displayContents() {
        if (useCustom) {
            for (String s : custom) {
                System.out.println(s);
            }
        } else {
            for (String s : libList) {
                System.out.println(s);
            }
        }
        System.out.println("Total sort steps " + totalSortSteps.get());
    }

    private class BubbleWorker extends Thread {
        private final int id;
        private volatile boolean active = true;

        BubbleWorker(int id) {
            this.id = id;
            setName("BubbleWorker-" + id);
        }

        @Override
        public void run() {
            System.out.println("Thread " + id + " started.");
            while (active && !isInterrupted()) {
                try {
                    if (useCustom) {
                        stepCustom();
                    } else {
                        stepLibrary();
                    }
                    if (delayMillis > 0) {
                        Thread.sleep(delayMillis);
                    }
                } catch (InterruptedException e) {
                    interrupt();
                    break;
                } catch (Exception ex) {
                    System.err.println("Error in worker " + id + " " + ex.getMessage());
                }
            }
        }


        private void stepCustom() throws InterruptedException {
            synchronized (custom) {
                if (custom.size < 2) {
                    return;
                }
                boolean anySwap = false;
                for (int i = 0; i < custom.size - 1 && active; i++) {
                    totalSortSteps.incrementAndGet();
                    String a = custom.get(i);
                    String b = custom.get(i + 1);
                    if (a.compareTo(b) > 0) {
                        custom.swap(i);
                        anySwap = true;
                        if (delayMillis > 0) {
                            Thread.sleep(delayMillis);
                        }
                    }
                }

                if (!anySwap && active) {
                    Thread.sleep(delayMillis);
                }
            }
        }


        private void stepLibrary() throws InterruptedException {
            synchronized (libList) {
                if (libList.size() < 2) return;

                boolean anySwap = false;
                for (int i = 0; i < libList.size() - 1 && active; i++) {
                    totalSortSteps.incrementAndGet();
                    String a = libList.get(i);
                    String b = libList.get(i + 1);
                    if (a.compareTo(b) > 0) {
                        libList.set(i, b);
                        libList.set(i + 1, a);
                        anySwap = true;
                        if (delayMillis > 0) Thread.sleep(delayMillis);
                    }
                }

                if (!anySwap && active) {
                    Thread.sleep(delayMillis);
                }
            }
        }
    }

    public static void main(String[] args) {
        int threads = 2;
        int delay = 400;
        boolean useMyList = false;
        Main app = new Main(delay, threads, useMyList);
        app.launch();
    }
}
