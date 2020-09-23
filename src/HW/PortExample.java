package HW;

import java.util.concurrent.Semaphore;

public class PortExample {
    private static final int COUNT_PORTS = 6;
    private static final int COUNT_SHIPS = 10;
    private static boolean[] CONTROL_PLACES = null;

    private static Semaphore SEMAPHORE = null;

    public static class Ship extends Thread {
        private int shipNum;

        public Ship(int shipNum) {
            this.shipNum = shipNum;
        }

        @Override
        public void run() {
            System.out.printf("Корабль прибыл\n", shipNum);
            try {
                SEMAPHORE.acquire();
                System.out.printf("\t корабль %d проводит поиск свободного порта\n", shipNum);
                int controlNum = -1;
                synchronized (CONTROL_PLACES) {
                    for (int i = 0; i < COUNT_PORTS; i++)
                        if (CONTROL_PLACES[i]) {
                            CONTROL_PLACES[i] = false;
                            controlNum = i;
                            System.out.printf("\t\tкорабль %d в порту %d.\n", shipNum, i);
                            break;
                        }
                }

                Thread.sleep((int) (Math.random() * 15 + 1) * 1000);

                synchronized (CONTROL_PLACES) {
                    CONTROL_PLACES[controlNum] = true;
                }

                SEMAPHORE.release();
                System.out.printf("Корабль %d произвело погрузочную/разгрузочную операцию\n", shipNum);
            } catch (InterruptedException e) {
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CONTROL_PLACES = new boolean[COUNT_PORTS];
        for (int i = 0; i < COUNT_PORTS; i++)
            CONTROL_PLACES[i] = true;
        SEMAPHORE = new Semaphore(CONTROL_PLACES.length, true);

        for (int i = 1; i <= COUNT_SHIPS; i++) {
            new Thread(new Ship(i)).start();
            Thread.sleep(400);
        }
    }
}