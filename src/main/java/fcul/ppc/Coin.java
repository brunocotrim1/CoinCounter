package fcul.ppc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Coin {
	private static final int nCores = Runtime.getRuntime().availableProcessors();
	private static Map<Integer, Double> resultsSeq = new HashMap<>();
	private static Map<Integer, Double> resultsPar = new HashMap<>();
	public static final int LIMIT = 999;
	public static final int MAX_TASKS = (nCores/2);

	public static int[] createRandomCoinSet(int N) {
		int[] r = new int[N];
		for (int i = 0; i < N; i++) {
			if (i % 10 == 0) {
				r[i] = 400;
			} else {
				r[i] = 4;
			}
		}
		return r;
	}

	public static void main(String[] args) {
		int[] coins = createRandomCoinSet(34);
		System.out.println("Number of cores: " + MAX_TASKS);
		int repeats = 31;
		for (int i = 0; i < repeats; i++) {
/*			long seqInitialTime = System.nanoTime();
			int rs = seq(coins, 0, 0);
			long seqEndTime = System.nanoTime() - seqInitialTime;
			System.out.println(nCores + ";Sequential;" + (double) seqEndTime / 1E9);
			resultsPar.put(i, (double) seqEndTime / 1E9);*/

			long parInitialTime = System.nanoTime();
			int rp = par(coins, 0, 0);
			long parEndTime = System.nanoTime() - parInitialTime;
			System.out.println(nCores + ";Parallel;" + (double) parEndTime / 1E9);
			resultsPar.put(i, (double) parEndTime / 1E9);

/*			if (rp != rs) {
				System.out.println("Wrong Result!");
				System.exit(-1);
			}*/
		}
		String parCsvFileName = "par_surplus2T.csv";

		// Write the data to CSV files
		//
		//writeResultsToCSV(parCsvFileName, resultsPar);
	}

	private static void writeResultsToCSV(String fileName, Map<Integer, Double> data) {
		try (FileWriter writer = new FileWriter(fileName)) {
			// Write CSV header
			writer.append("iterations,time\n");

			// Write data to the CSV file
			for (Map.Entry<Integer, Double> entry : data.entrySet()) {
				writer.append(entry.getKey().toString());
				writer.append(",");
				writer.append(entry.getValue().toString());
				writer.append("\n");
			}

			System.out.println("Data written to " + fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	private static int par(int[] coins, int index, int accumulator) {
		try {
			Thread.sleep(1000);
			RecursiveCoin task = new RecursiveCoin(coins, index, accumulator,0);
			ForkJoinPool pool = new ForkJoinPool(nCores);
			int result = pool.invoke(task);
			pool.shutdown();
			return result;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return -1;
	}


}
