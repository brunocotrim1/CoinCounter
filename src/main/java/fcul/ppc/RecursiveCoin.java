package fcul.ppc;

import java.util.concurrent.RecursiveTask;

public class RecursiveCoin extends RecursiveTask<Integer> {

    private static final int LIMIT = 999;
    private int[] coins;
    private int index;
    private int accumulator;

    private int depth;
    public RecursiveCoin(int[] coins, int index, int accumulator, int depth) {
        this.coins = coins;
        this.index = index;
        this.accumulator = accumulator;
        this.depth = depth;
    }

    @Override
    protected Integer compute() {
        //  System.out.println("Computing " + index + " " + accumulator);
        if (index >= coins.length) {
            if (accumulator < LIMIT) {
                return accumulator;
            }
            return -1;
        }
        if (accumulator + coins[index] > LIMIT) {
            return -1;
        }

        if(getQueuedTaskCount() > Coin.MAX_TASKS){
            int a = seq(coins, index + 1, accumulator);
            int b = seq(coins, index + 1, accumulator + coins[index]);
            return Math.max(a, b);
        }

/*        if ( depth == 10){
            int a = seq(coins, index + 1, accumulator);
            int b = seq(coins, index + 1, accumulator + coins[index]);
            return Math.max(a, b);
        }*/

        // Surplus: if the current queue has more than 2 tasks than the average
/*        if (RecursiveTask.getSurplusQueuedTaskCount() > 2) {
            int a = seq(coins, index + 1, accumulator);
            int b = seq(coins, index + 1, accumulator + coins[index]);
            return Math.max(a, b);
        }*/

        RecursiveCoin aTask = new RecursiveCoin(coins, index + 1, accumulator, depth + 1);
        aTask.fork();
        RecursiveCoin bTask = new RecursiveCoin(coins, index + 1, accumulator + coins[index]
                , depth + 1);
        bTask.fork();
        int a = aTask.join();
        int b = bTask.join();
        return Math.max(a, b);
    }

    private static int seq(int[] coins, int index, int accumulator) {

        if (index >= coins.length) {
            if (accumulator < LIMIT) {
                return accumulator;
            }
            return -1;
        }
        if (accumulator + coins[index] > LIMIT) {
            return -1;
        }
        int a = seq(coins, index + 1, accumulator);
        int b = seq(coins, index + 1, accumulator + coins[index]);
        return Math.max(a, b);
    }
}
