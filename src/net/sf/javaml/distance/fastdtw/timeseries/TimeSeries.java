/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.timeseries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.fastdtw.util.Arrays;

// Referenced classes of package timeseries:
//            TimeSeriesPoint

public class TimeSeries {

	TimeSeries() {
		labels = new ArrayList<String>();
		timeReadings = new ArrayList();
		tsArray = new ArrayList();
	}

	public TimeSeries(int numOfDimensions) {
		this();
		labels.add("Time");
		for (int x = 0; x < numOfDimensions; x++)
			labels.add("" + x);

	}

	public TimeSeries(TimeSeries origTS) {
		labels = new ArrayList<String>(origTS.labels);
		timeReadings = new ArrayList(origTS.timeReadings);
		tsArray = new ArrayList(origTS.tsArray);
	}

	public TimeSeries(String inputFile, boolean isFirstColTime) {
		this(inputFile, ZERO_ARRAY, isFirstColTime);
	}

	public TimeSeries(String inputFile, char delimiter) {
		this(inputFile, ZERO_ARRAY, true, true, delimiter);
	}

	public TimeSeries(String inputFile, boolean isFirstColTime, char delimiter) {
		this(inputFile, ZERO_ARRAY, isFirstColTime, true, delimiter);
	}

	public TimeSeries(String inputFile, boolean isFirstColTime,
			boolean isLabeled, char delimiter) {
		this(inputFile, ZERO_ARRAY, isFirstColTime, isLabeled, delimiter);
	}

	public TimeSeries(String inputFile, int colToInclude[],
			boolean isFirstColTime) {
		this(inputFile, colToInclude, isFirstColTime, true, ',');
	}

	public TimeSeries(String inputFile, int colToInclude[],
			boolean isFirstColTime, boolean isLabeled, char delimiter) {
		this();
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = br.readLine();
			StringTokenizer st = new StringTokenizer(line, String
					.valueOf(delimiter));
			if (isLabeled) {
				for (int currentCol = 0; st.hasMoreTokens(); currentCol++) {
					String currentToken = st.nextToken();
					if (colToInclude.length == 0
							|| Arrays.contains(colToInclude, currentCol))
						labels.add(currentToken);
				}

				if (labels.size() == 0)
					throw new InternalError(
							"ERROR:  The first row must contain label information, it is empty!");
				if (!isFirstColTime)
					labels.add(0, "Time");
				else if (isFirstColTime
						&& !((String) labels.get(0)).equalsIgnoreCase("Time"))
					throw new InternalError(
							"ERROR:  The time column (1st col) in a time series must be labeled as 'Time', '"
									+ labels.get(0) + "' was found instead");
			} else {
				if (colToInclude == null || colToInclude.length == 0) {
					labels.add("Time");
					if (isFirstColTime)
						st.nextToken();
					int currentCol = 1;
					for (; st.hasMoreTokens(); labels.add(new String("c"
							+ currentCol++)))
						st.nextToken();

				} else {
					java.util.Arrays.sort(colToInclude);
					labels.add("Time");
					for (int c = 0; c < colToInclude.length; c++)
						if (colToInclude[c] > 0)
							labels.add(new String("c" + c));

				}
				br.close();
				br = new BufferedReader(new FileReader(inputFile));
			}
			do {
				if ((line = br.readLine()) == null)
					break;
				if (line.length() > 0) {
					st = new StringTokenizer(line, String
							.valueOf(delimiter));
					ArrayList<Double> currentLineValues = new ArrayList<Double>();
					for (int currentCol = 0; st.hasMoreTokens(); currentCol++) {
						String currentToken = st.nextToken();
						if (colToInclude.length != 0
								&& !Arrays.contains(colToInclude, currentCol))
							continue;
						Double nextValue;
						try {
							nextValue = Double.valueOf(currentToken);
						} catch (NumberFormatException e) {
							throw new InternalError("ERROR:  '" + currentToken
									+ "' is not a valid number");
						}
						currentLineValues.add(nextValue);
					}

					if (isFirstColTime)
						timeReadings.add(currentLineValues.get(0));
					else
						timeReadings.add(new Double(timeReadings.size()));
					int firstMeasurement;
					if (isFirstColTime)
						firstMeasurement = 1;
					else
						firstMeasurement = 0;
					TimeSeriesPoint readings = new TimeSeriesPoint(
							currentLineValues.subList(firstMeasurement,
									currentLineValues.size()));
					tsArray.add(readings);
				}
			} while (true);
		} catch (FileNotFoundException e) {
			throw new InternalError("ERROR:  The file '" + inputFile
					+ "' was not found.");
		} catch (IOException e) {
			throw new InternalError("ERROR:  Problem reading the file '"
					+ inputFile + "'.");
		}
	}

	public TimeSeries(Instance y) {
		this();

		labels.add("Time");
		labels.add("c1");
		for (int i = 0; i < y.noAttributes(); i++) {
			ArrayList<Double> currentLineValues = new ArrayList<Double>();
			currentLineValues.add(y.value(i));
			timeReadings.add(new Double(timeReadings.size()));
			TimeSeriesPoint readings = new TimeSeriesPoint(currentLineValues
					.subList(0, currentLineValues.size()));
			tsArray.add(readings);
		}
		// do {
		//
		// if (line.length() > 0) {
		// st = new StringTokenizer(line, ",");
		// ArrayList<Double> currentLineValues = new ArrayList<Double>();
		// for (int currentCol = 0; st.hasMoreTokens(); currentCol++) {
		// String currentToken = st.nextToken();
		//
		// Double nextValue = Double.valueOf(currentToken);
		//
		// currentLineValues.add(nextValue);
		// }
		//
		// timeReadings.add(new Double(timeReadings.size()));
		// int firstMeasurement;
		//
		// firstMeasurement = 0;
		// TimeSeriesPoint readings = new
		// TimeSeriesPoint(currentLineValues.subList(firstMeasurement,
		// currentLineValues.size()));
		// tsArray.add(readings);
		// }
		// } while (true);

	}

	public void save(File outFile) throws IOException {
		PrintWriter out = new PrintWriter(new FileOutputStream(outFile));
		out.write(toString());
		out.flush();
		out.close();
	}

	public void clear() {
		labels.clear();
		timeReadings.clear();
		tsArray.clear();
	}

	public int size() {
		return timeReadings.size();
	}

	public int numOfPts() {
		return size();
	}

	public int numOfDimensions() {
		return labels.size() - 1;
	}

	public double getTimeAtNthPoint(int n) {
		return ((Double) timeReadings.get(n)).doubleValue();
	}

	public String getLabel(int index) {
		return (String) labels.get(index);
	}

	public String[] getLabelsArr() {
		String labelArr[] = new String[labels.size()];
		for (int x = 0; x < labels.size(); x++)
			labelArr[x] = (String) labels.get(x);

		return labelArr;
	}

	public ArrayList getLabels() {
		return labels;
	}

	public void setLabels(String newLabels[]) {
		labels.clear();
		for (int x = 0; x < newLabels.length; x++)
			labels.add(newLabels[x]);

	}

	public void setLabels(ArrayList<String> newLabels) {
		labels.clear();
		for (int x = 0; x < newLabels.size(); x++)
			labels.add(newLabels.get(x));

	}

	public double getMeasurement(int pointIndex, int valueIndex) {
		return ((TimeSeriesPoint) tsArray.get(pointIndex)).get(valueIndex);
	}

	public double getMeasurement(int pointIndex, String valueLabel) {
		int valueIndex = labels.indexOf(valueLabel);
		if (valueIndex < 0)
			throw new InternalError("ERROR:  the label '" + valueLabel
					+ "' was " + "not one of:  " + labels);
		else
			return ((TimeSeriesPoint) tsArray.get(pointIndex))
					.get(valueIndex - 1);
	}

	public double getMeasurement(double time, int valueIndex) {
		return 0.0D;
	}

	public double getMeasurement(double time, String valueLabel) {
		int valueIndex = labels.indexOf(valueLabel);
		if (valueIndex < 0)
			throw new InternalError("ERROR:  the label '" + valueLabel
					+ "' was " + "not one of:  " + labels);
		else
			return getMeasurement(time, valueIndex);
	}

	public double[] getMeasurementVector(int pointIndex) {
		return ((TimeSeriesPoint) tsArray.get(pointIndex)).toArray();
	}

	public double[] getMeasurementVector(double time) {
		return null;
	}

	public void setMeasurement(int pointIndex, int valueIndex, double newValue) {
		((TimeSeriesPoint) tsArray.get(pointIndex)).set(valueIndex, newValue);
	}

	public void addFirst(double time, TimeSeriesPoint values) {
		if (labels.size() != values.size() + 1)
			throw new InternalError("ERROR:  The TimeSeriesPoint: " + values
					+ " contains the wrong number of values. " + "expected:  "
					+ labels.size() + ", " + "found: " + (values.size()+1));
		if (time >= ((Double) timeReadings.get(0)).doubleValue()) {
			throw new InternalError(
					"ERROR:  The point being inserted into the beginning of the time series does not have the correct time sequence. ");
		} else {
			timeReadings.add(0, new Double(time));
			tsArray.add(0, values);
			return;
		}
	}

	public void addLast(double time, TimeSeriesPoint values) {
		if (labels.size() != values.size() + 1)
			throw new InternalError("ERROR:  The TimeSeriesPoint: " + values
					+ " contains the wrong number of values. " + "expected:  "
					+ labels.size() + ", " + "found: " + values.size());
		if (size() > 0
				&& time <= ((Double) timeReadings.get(timeReadings.size() - 1))
						.doubleValue()) {
			throw new InternalError(
					"ERROR:  The point being inserted at the end of the time series does not have the correct time sequence. ");
		} else {
			timeReadings.add(new Double(time));
			tsArray.add(values);
			return;
		}
	}

	public void removeFirst() {
		if (size() == 0) {
			System.err
					.println("WARNING:  TimeSeriesPoint:removeFirst() called on an empty time series!");
		} else {
			timeReadings.remove(0);
			tsArray.remove(0);
		}
	}

	public void removeLast() {
		if (size() == 0) {
			System.err
					.println("WARNING:  TimeSeriesPoint:removeLast() called on an empty time series!");
		} else {
			timeReadings.remove(timeReadings.size() - 1);
			tsArray.remove(timeReadings.size() - 1);
		}
	}

	public void normalize() {
		double mean[] = new double[numOfDimensions()];
		for (int col = 0; col < numOfDimensions(); col++) {
			double currentSum = 0.0D;
			for (int row = 0; row < size(); row++)
				currentSum += getMeasurement(row, col);

			mean[col] = currentSum / (double) size();
		}

		double stdDev[] = new double[numOfDimensions()];
		for (int col = 0; col < numOfDimensions(); col++) {
			double variance = 0.0D;
			for (int row = 0; row < size(); row++)
				variance += Math.abs(getMeasurement(row, col) - mean[col]);

			stdDev[col] = variance / (double) size();
		}

		for (int row = 0; row < size(); row++) {
			for (int col = 0; col < numOfDimensions(); col++)
				if (stdDev[col] == 0.0D)
					setMeasurement(row, col, 0.0D);
				else
					setMeasurement(row, col,
							(getMeasurement(row, col) - mean[col])
									/ stdDev[col]);

		}

	}

	public String toString() {
		StringBuffer outStr = new StringBuffer();
		for (int r = 0; r < timeReadings.size(); r++) {
			TimeSeriesPoint values = (TimeSeriesPoint) tsArray.get(r);
			for (int c = 0; c < values.size(); c++)
				outStr.append(values.get(c));

			if (r < timeReadings.size() - 1)
				outStr.append("\n");
		}

		return outStr.toString();
	}

	protected void setMaxCapacity(int capacity) {
		timeReadings.ensureCapacity(capacity);
		tsArray.ensureCapacity(capacity);
	}

	private static final int ZERO_ARRAY[] = new int[0];

	// private static final boolean DEFAULT_IS_TIME_1ST_COL = true;
	//
	// private static final char DEFAULT_DELIMITER = 44;
	//
	// private static final boolean DEFAULT_IS_LABELED = true;

	private final ArrayList<String> labels;

	private final ArrayList<Double> timeReadings;

	private final ArrayList<TimeSeriesPoint> tsArray;

}