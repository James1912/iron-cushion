package co.adhoclabs.ironcushion;

import java.util.Arrays;
import java.util.List;

import co.adhoclabs.ironcushion.bulkinsert.BulkInsertConnectionStatistics;
import co.adhoclabs.ironcushion.crud.CrudConnectionStatistics;
import co.adhoclabs.ironcushion.crud.CrudOperations;

/**
 * The results of the benchmark.
 * 
 * @author Michael Parker (michael.g.parker@gmail.com)
 */
public class BenchmarkResults {
	/**
	 * Benchmark results for bulk insertions.
	 */
	public static final class BulkInsertBenchmarkResults {
		public final long timeTaken;
		public final long totalBytesSent;
		public final long totalBytesReceived;
		
		public final SampleStatistics localProcessingStatistics;
		public final SampleStatistics sendDataStatistics;
		public final SampleStatistics remoteProcessingStatistics;
		public final SampleStatistics receiveDataStatistics;
		
		public final double insertRate;
		
		private BulkInsertBenchmarkResults(long timeTaken,
				long totalBytesSent,
				long totalBytesReceived,
				SampleStatistics localProcessingStatistics,
				SampleStatistics sendDataStatistics,
				SampleStatistics remoteProcessingStatistics,
				SampleStatistics receiveDataStatistics,
				double insertRate) {
			this.timeTaken = timeTaken;
			this.totalBytesSent = totalBytesSent;
			this.totalBytesReceived = totalBytesReceived;
			this.localProcessingStatistics = localProcessingStatistics;
			this.sendDataStatistics = sendDataStatistics;
			this.remoteProcessingStatistics = remoteProcessingStatistics;
			this.receiveDataStatistics = receiveDataStatistics;
			this.insertRate = insertRate;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("timeTaken=").append(timeTaken).append(" ms\n");
			sb.append("totalBytesSent=").append(totalBytesSent).append(" bytes\n");
			sb.append("totalBytesReceived=").append(totalBytesReceived).append(" bytes\n");
			sb.append("localProcessing={").append(localProcessingStatistics).append("}\n");
			sb.append("sendData={").append(sendDataStatistics).append("}\n");
			sb.append("remoteProcessing={").append(remoteProcessingStatistics).append("}\n");
			sb.append("receiveDataStatistics={").append(receiveDataStatistics).append("}\n");
			sb.append("insertRate=").append(insertRate).append(" docs/sec");
			return sb.toString();
		}
	}

	/**
	 * Benchmark results for CRUD operations.
	 */
	public static final class CrudBenchmarkResults {
		public final long timeTaken;
		public final long totalBytesSent;
		public final long totalBytesReceived;
		
		public final SampleStatistics localProcessingStatistics;
		public final SampleStatistics sendDataStatistics;
		public final SampleStatistics remoteCreateProcessingStatistics;
		public final SampleStatistics remoteReadProcessingStatistics;
		public final SampleStatistics remoteUpdateProcessingStatistics;
		public final SampleStatistics remoteDeleteProcessingStatistics;
		public final SampleStatistics receiveDataStatistics;
		
		public final double createRate;
		public final double readRate;
		public final double updateRate;
		public final double deleteRate;
		
		public CrudBenchmarkResults(long timeTaken,
				long totalBytesSent,
				long totalBytesReceived,
				SampleStatistics localProcessingStatistics,
				SampleStatistics sendDataStatistics,
				SampleStatistics remoteCreateProcessingStatistics,
				SampleStatistics remoteReadProcessingStatistics,
				SampleStatistics remoteUpdateProcessingStatistics,
				SampleStatistics remoteDeleteProcessingStatistics,
				SampleStatistics receiveDataStatistics,
				double createRate,
				double readRate,
				double updateRate,
				double deleteRate) {
			this.timeTaken = timeTaken;
			this.totalBytesSent = totalBytesSent;
			this.totalBytesReceived = totalBytesReceived;
			this.localProcessingStatistics = localProcessingStatistics;
			this.sendDataStatistics = sendDataStatistics;
			this.remoteCreateProcessingStatistics = remoteCreateProcessingStatistics;
			this.remoteReadProcessingStatistics = remoteReadProcessingStatistics;
			this.remoteUpdateProcessingStatistics = remoteUpdateProcessingStatistics;
			this.remoteDeleteProcessingStatistics = remoteDeleteProcessingStatistics;
			this.receiveDataStatistics = receiveDataStatistics;
			this.createRate = createRate;
			this.readRate = readRate;
			this.updateRate = updateRate;
			this.deleteRate = deleteRate;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("timeTaken=").append(timeTaken).append(" ms\n");
			sb.append("totalBytesSent=").append(totalBytesSent).append(" bytes\n");
			sb.append("totalBytesReceived=").append(totalBytesReceived).append(" bytes\n");
			sb.append("localProcessing={").append(localProcessingStatistics).append("}\n");
			sb.append("sendData={").append(sendDataStatistics).append("}\n");
			sb.append("remoteCreateProcessing={").append(remoteCreateProcessingStatistics).append("}\n");
			sb.append("remoteReadProcessing={").append(remoteReadProcessingStatistics).append("}\n");
			sb.append("remoteUpdateProcessing={").append(remoteUpdateProcessingStatistics).append("}\n");
			sb.append("remoteDeleteProcessing={").append(remoteDeleteProcessingStatistics).append("}\n");
			sb.append("receiveDataStatistics={").append(receiveDataStatistics).append("}\n");
			sb.append("createRate=").append(createRate).append(" docs/sec\n");
			sb.append("readRate=").append(readRate).append(" docs/sec\n");
			sb.append("updateRate=").append(updateRate).append(" docs/sec\n");
			sb.append("deleteRate=").append(deleteRate).append(" docs/sec");
			return sb.toString();
		}
	}

	private static long getTimeTaken(
			List<? extends AbstractConnectionStatistics> allConnectionStatistics) {
		// The time taken is the maximum time taken by any connection.
		long maxTimeTaken = 0;
		for (AbstractConnectionStatistics connectionStatistics : allConnectionStatistics) {
			long timeTaken = connectionStatistics.getTotalTimeMillis();
			if (timeTaken > maxTimeTaken) {
				maxTimeTaken = timeTaken;
			}
		}
		return maxTimeTaken;
	}
	
	private static long getTotalBytesSent(
			List<? extends AbstractConnectionStatistics> allConnectionStatistics) {
		long totalBytesSent = 0;
		for (AbstractConnectionStatistics connectionStatistics : allConnectionStatistics) {
			totalBytesSent += connectionStatistics.getJsonBytesSent();
		}
		return totalBytesSent;
	}
	
	private static long getTotalBytesReceived(
			List<? extends AbstractConnectionStatistics> allConnectionStatistics) {
		long totalBytesReceived = 0;
		for (AbstractConnectionStatistics connectionStatistics : allConnectionStatistics) {
			totalBytesReceived += connectionStatistics.getJsonBytesReceived();
		}
		return totalBytesReceived;
	}
	
	private static SampleStatistics getLocalProcessingStatistics(
			List<? extends AbstractConnectionStatistics> allConnectionStatistics) {
		long[] values = new long[allConnectionStatistics.size()];
		for (int i = 0; i < allConnectionStatistics.size(); ++i) {
			AbstractConnectionStatistics connectionStatistics = allConnectionStatistics.get(i);
			values[i] = connectionStatistics.getLocalProcessingTimeMillis();
		}
		return SampleStatistics.statisticsForPopulation(values);
	}

	private static SampleStatistics getSendDataStatistics(
			List<? extends AbstractConnectionStatistics> allConnectionStatistics) {
		long[] values = new long[allConnectionStatistics.size()];
		for (int i = 0; i < allConnectionStatistics.size(); ++i) {
			AbstractConnectionStatistics connectionStatistics = allConnectionStatistics.get(i);
			values[i] = connectionStatistics.getSendDataTimeMillis();
		}
		return SampleStatistics.statisticsForPopulation(values);
	}
	
	private static SampleStatistics getReceiveDataStatistics(
			List<? extends AbstractConnectionStatistics> allConnectionStatistics) {
		long[] values = new long[allConnectionStatistics.size()];
		for (int i = 0; i < allConnectionStatistics.size(); ++i) {
			AbstractConnectionStatistics connectionStatistics = allConnectionStatistics.get(i);
			values[i] = connectionStatistics.getReceivedDataTimeMillis();
		}
		return SampleStatistics.statisticsForPopulation(values);
	}
	
	/**
	 * Returns benchmark results for the connection statistics for bulk inserts.
	 * 
	 * @param allConnectionStatistics the bulk insert connection statistics
	 * @return the benchmark results
	 */
	public static BulkInsertBenchmarkResults getBulkInsertResults(
			ParsedArguments parsedArguments,
			List<BulkInsertConnectionStatistics> allConnectionStatistics) {
		long timeTaken = getTimeTaken(allConnectionStatistics);
		long totalBytesSent = getTotalBytesSent(allConnectionStatistics);
		long totalBytesReceived = getTotalBytesReceived(allConnectionStatistics);
		
		long[] values = new long[allConnectionStatistics.size()];
		// Get statistics for local processing.
		SampleStatistics localProcessingStatistics = getLocalProcessingStatistics(allConnectionStatistics);
		// Get statistics for sending data.
		SampleStatistics sendDataStatistics = getSendDataStatistics(allConnectionStatistics);
		// Get statistics for remote processing.
		for (int i = 0; i < allConnectionStatistics.size(); ++i) {
			BulkInsertConnectionStatistics connectionStatistics = allConnectionStatistics.get(i);
			values[i] = connectionStatistics.getRemoteProcessingTimeMillis();
		}
		SampleStatistics remoteProcessingStatistics = SampleStatistics.statisticsForPopulation(values);
		// Get statistics for receiving data.
		SampleStatistics receiveDataStatistics = getReceiveDataStatistics(allConnectionStatistics);

		// Calculate the rate of documents inserted per second.
		long numBulkInsertedDocs = (parsedArguments.numDocumentsPerBulkInsert *
				parsedArguments.numBulkInsertOperations *
				parsedArguments.numConnections);
		double insertRate = numBulkInsertedDocs / (remoteProcessingStatistics.max / 1000.0);
		
		return new BulkInsertBenchmarkResults(timeTaken,
				totalBytesSent,
				totalBytesReceived,
				localProcessingStatistics,
				sendDataStatistics,
				remoteProcessingStatistics,
				receiveDataStatistics,
				insertRate);
	}
	
	/**
	 * Returns benchmark results for the connection statistics for CRUD operations.
	 * 
	 * @param allConnectionStatistics the CRUD connection statistics
	 * @return the benchmark results
	 */
	public static CrudBenchmarkResults getCrudResults(
			int numConnections, CrudOperations.CrudOperationCounts operationCounts,
			List<CrudConnectionStatistics> allConnectionStatistics) {
		long timeTaken = getTimeTaken(allConnectionStatistics);
		long totalBytesSent = getTotalBytesSent(allConnectionStatistics);
		long totalBytesReceived = getTotalBytesReceived(allConnectionStatistics);
		
		long[] values = new long[allConnectionStatistics.size()];
		// Get statistics for local processing.
		SampleStatistics localProcessingStatistics = getLocalProcessingStatistics(allConnectionStatistics);
		// Get statistics for sending data.
		SampleStatistics sendDataStatistics = getSendDataStatistics(allConnectionStatistics);
		// Get statistics for remote processing of create operations.
		for (int i = 0; i < allConnectionStatistics.size(); ++i) {
			CrudConnectionStatistics connectionStatistics = allConnectionStatistics.get(i);
			values[i] = connectionStatistics.getRemoteCreateProcessingTimeMillis();
		}
		SampleStatistics remoteCreateProcessingStatistics = SampleStatistics.statisticsForPopulation(values);
		// Get statistics for remote processing of read operations.
		for (int i = 0; i < allConnectionStatistics.size(); ++i) {
			CrudConnectionStatistics connectionStatistics = allConnectionStatistics.get(i);
			values[i] = connectionStatistics.getRemoteReadProcessingTimeMillis();
		}
		SampleStatistics remoteReadProcessingStatistics = SampleStatistics.statisticsForPopulation(values);
		// Get statistics for remote processing of update operations.
		for (int i = 0; i < allConnectionStatistics.size(); ++i) {
			CrudConnectionStatistics connectionStatistics = allConnectionStatistics.get(i);
			values[i] = connectionStatistics.getRemoteUpdateProcessingTimeMillis();
		}
		SampleStatistics remoteUpdateProcessingStatistics = SampleStatistics.statisticsForPopulation(values);
		// Get statistics for remote processing of delete operations.
		for (int i = 0; i < allConnectionStatistics.size(); ++i) {
			CrudConnectionStatistics connectionStatistics = allConnectionStatistics.get(i);
			values[i] = connectionStatistics.getRemoteDeleteProcessingTimeMillis();
		}
		SampleStatistics remoteDeleteProcessingStatistics = SampleStatistics.statisticsForPopulation(values);
		// Get statistics for receiving data.
		SampleStatistics receiveDataStatistics = getReceiveDataStatistics(allConnectionStatistics);

		// Calculate the rate of documents created per second.
		long numCreatedDocs = (numConnections * operationCounts.numCreateOperations);
		double createRate = numCreatedDocs / (remoteCreateProcessingStatistics.max / 1000.0);
		// Calculate the rate of documents read per second.
		long numReadDocs = (numConnections * operationCounts.numReadOperations);
		double readRate = numReadDocs / (remoteReadProcessingStatistics.max / 1000.0);
		// Calculate the rate of documents updated per second.
		long numUpdatedDocs = (numConnections * operationCounts.numUpdateOperations);
		double updateRate = numUpdatedDocs / (remoteUpdateProcessingStatistics.max / 1000.0);
		// Calculate the rate of documents deleted per second.
		long numDeletedDocs = (numConnections * operationCounts.numDeleteOperations);
		double deleteRate = numDeletedDocs / (remoteDeleteProcessingStatistics.max / 1000.0);
		
		return new CrudBenchmarkResults(timeTaken,
				totalBytesSent,
				totalBytesReceived,
				localProcessingStatistics,
				sendDataStatistics,
				remoteCreateProcessingStatistics,
				remoteReadProcessingStatistics,
				remoteUpdateProcessingStatistics,
				remoteDeleteProcessingStatistics,
				receiveDataStatistics,
				createRate,
				readRate,
				updateRate,
				deleteRate);
	}
	
	/**
	 * Essential statistics about a data set.
	 */
	public static final class SampleStatistics {
		public final double min;
		public final double max;
		public final long sum;
		public final double mean;
		public final double median;
		public final double deviation;
		
		public SampleStatistics(double min, double max, long sum,
				double mean, double median, double deviation) {
			this.min = min;
			this.max = max;
			this.sum = sum;
			this.mean = mean;
			this.median = median;
			this.deviation = deviation;
		}
		
		private static SampleStatistics statisticsForPopulation(long[] values) {
			// Make a copy of the array before sorting as a courtesy.
			values = Arrays.copyOf(values, values.length);
			Arrays.sort(values);
			
			// Find the minimum and maximum.
			long min = values[0];
			long max = values[values.length - 1];
			// Find the median.
			double median;
			if ((values.length % 2) == 1) {
				median = values[(values.length - 1) / 2];
			} else {
				int firstMedianIndex = values.length / 2;
				int secondMedianIndex = firstMedianIndex - 1;
				median = ((double) (values[firstMedianIndex] + values[secondMedianIndex])) / 2; 
			}
			// Compute the mean.
			long sum = 0;
			for (int i = 0; i < values.length; ++i) {
				sum += values[i];
			}
			double mean = ((double) sum) / values.length;
			// Compute the standard deviation.
			double numerator = 0;
			for (int i = 0; i < values.length; ++i) {
				double difference = values[i] - mean;
				numerator += (difference * difference);
			}
			double variance = numerator / values.length;
			double deviation = Math.sqrt(variance);
			
			return new SampleStatistics(min, max, sum, mean, median, deviation);
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("min=").append(min).append(" ms, ");
			sb.append("max=").append(max).append(" ms, ");
			sb.append("median=").append(median).append(" ms, ");
			sb.append("stdev=").append(deviation).append(" ms");
			return sb.toString();
		}
		
		public String toString(String title) {
			StringBuilder sb = new StringBuilder();
			sb.append(title).append(": ").append(toString());
			return sb.toString();
		}
	}
}
