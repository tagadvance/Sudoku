package com.tagadvance.sudoku;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.SwingUtilities;

/**
 * Class for commonly used static functions
 * <p>
 * <strong>Fun classes:</strong>
 * <ul>
 * <li>java.awt.Desktop
 * <li>java.awt.MouseInfo
 * <li>java.awt.Robot
 * <li>java.awt.SystemColor
 * <li>java.awt.Toolkit
 * <li>java.io.Console
 * <li>java.lang.Math
 * <li>java.lang.Runtime
 * <li>java.lang.StringBuilder
 * <li>java.math.BigInteger
 * <li>java.util.Arrays
 * <li>java.util.Collections
 * <li>java.util.prefs.Preferences
 * <li>javax.imageio.ImageIO
 * <li>javax.swing.SwingUtilities
 * <li>javax.swing.UIManager
 * </ul>
 * </p>
 */
public final class Utils {

	public static long analyze(final Runnable run, final long iterations, long timeout,
			boolean thread) {
		int cores = Runtime.getRuntime().availableProcessors(); // cores
		final ExecutorService pool =
				thread ? Executors.newFixedThreadPool(cores) : Executors.newSingleThreadExecutor();
		long start = System.currentTimeMillis();
		for (long l = 0; l < iterations; l++) {
			pool.submit(run);
		}
		pool.shutdown();
		try {
			pool.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return -1;
		}
		long stop = System.currentTimeMillis();
		return stop - start; // a rough approximation
	}

	public static final String NEWLINE = System.getProperty("line.separator");
	public static final Random RANDOM = new Random(); // this is poor oop
	public static final String UTF8 = "UTF-8"; // Unicode Transformation Format

	/**
	 * 8 kB default buffer size
	 */
	public static int DEFAULT_BUFFER_SIZE = 8192;

	/**
	 * @param dividend top
	 * @param divisor bottom
	 * @return
	 * @deprecated
	 */
	public static double percentOf(double dividend, double divisor) {
		return round((dividend / divisor) * 100, 2);
	}

	/**
	 * Returns the Point that represents the coordinates of the pointer on the screen. See
	 * {@link MouseInfo#getPointerInfo()} for more information about coordinate calculation for
	 * multiscreen systems.
	 * 
	 * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true
	 * @throws SecurityException if a security manager exists and its checkPermission method doesn't
	 *         allow the operation
	 */
	public static Point getMouseLocation() {
		return MouseInfo.getPointerInfo().getLocation();
	}

	/**
	 * <tt>return Thread.getAllStackTraces().get(Thread.currentThread());</tt>
	 */
	public static StackTraceElement[] getCurrentStackTrace() {
		return Thread.getAllStackTraces().get(Thread.currentThread());
	}

	public static void printStackTrace(String message) {
		Exception ex = new Exception(message);
		ex.setStackTrace(getCurrentStackTrace());
		ex.printStackTrace();
	}

	/**
	 * @see Thread#dumpStack()
	 * @deprecated please use <code>Thread.dumpStack();</code> instead
	 */
	public static void printStackTrace() {
		Thread.dumpStack();
	}

	/**
	 * 
	 * @param time
	 * @throws IllegalThreadStateException
	 */
	public static void sleep(long time) {
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				String s = "cannot sleep in the swing thread";
				throw new IllegalThreadStateException(s);
			}
			Thread.sleep(time);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public static String pad(int length) {
		char space = ' ';
		return pad(space, length);
	}

	public static String pad(char c, int length) {
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length) {
			sb.append(c);
		}
		return sb.toString();
	}

	public static String padLeft(String s, char c, int length) {
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length - s.length()) {
			sb.append(c);
		}
		sb.append(s);
		return sb.toString();
	}

	public static String padRight(String s, char c, int length) {
		StringBuilder sb = new StringBuilder(s);
		while (sb.length() < length) {
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * This method is slightly slower than <tt>String.trim()</tt> so it should not be used to remove
	 * spaces.
	 * 
	 * @param s the String to be trimmed
	 * @param ch the character to trim
	 * @return
	 */
	public static String trim(String s, char ch) {
		int i = 0, len = s.length();
		for (; i < len && s.charAt(i) == ch; i++);
		for (; i < len && s.charAt(len - 1) == ch; len--);
		return i > 0 || len < s.length() ? s.substring(i, len) : s;
	}

	public static String trim(String s, char... chars) {
		Character[] characters = toObject(chars);
		int i = 0, len = s.length();
		for (; i < len && inArray(s.charAt(i), characters); i++);
		for (; i < len && inArray(s.charAt(len - 1), characters); len--);
		return i > 0 || len < s.length() ? s.substring(i, len) : s;
	}

	public static String trimLeft(String s, char ch) {
		int i = 0;
		for (; i < s.length() && s.charAt(i) == ch; i++);
		return i > 0 ? s.substring(i) : s;
	}

	public static String trimLeft(String s, char... chars) {
		Character[] characters = toObject(chars);
		int i = 0;
		for (; i < s.length() && inArray(s.charAt(i), characters); i++);
		return i > 0 ? s.substring(i) : s;
	}

	public static String trimRight(String s, char ch) {
		int len = s.length();
		for (; len > 0 && s.charAt(len - 1) == ch; len--);
		return len < s.length() ? s.substring(0, len) : s;
	}

	public static String trimRight(String s, char... chars) {
		Character[] characters = toObject(chars);
		int len = s.length();
		for (; len > 0 && inArray(s.charAt(len - 1), characters); len--);
		return len < s.length() ? s.substring(0, len) : s;
	}

	/**
	 * @param lastOccurance use the last occurance of <code>end</code>
	 */
	public static String getStringBetween(String s, String start, String end,
			boolean lastOccurance) {
		int i = s.indexOf(start) + start.length();
		int len = (lastOccurance ? s.lastIndexOf(end) : s.indexOf(end, i)) - i;
		return s.substring(i, i + len);
	}

	public static int getOppositeOf(int i) {
		String s = Integer.toString(i);
		if (i == 0 || s.matches("^[-]?1[0]+$")) { // -10, 100, ect...
			return 0;
		}
		String n = padRight((i < 0 ? "-1" : "1"), '0', s.length() + 1);
		int high = Integer.valueOf(n);
		return getOppositeOf(i, 0, high);
	}

	public static int getOppositeOf(int i, int num1, int num2) {
		int low = Math.min(num1, num2), high = Math.max(num1, num2);
		assert i >= low && i <= high && low < high;
		if (i < 0) {
			return high - Math.abs(Math.abs(i) - Math.abs(low));
		}
		return low + Math.abs(Math.abs(high) - Math.abs(i));
	}

	/**
	 * My version of PHPs <code>empty</code> function. It makes code much easier to read.
	 * 
	 * @deprecated
	 */
	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		} else if (o instanceof Number) {
			Number n = (Number) o;
			return n.doubleValue() == 0;
		} else if (o instanceof Boolean) {
			Boolean b = (Boolean) o;
			return !b.booleanValue();
		} else if (o instanceof Object[]) {
			Object[] array = (Object[]) o;
			for (Object element : array) {
				if (!isEmpty(element)) {
					return false;
				}
			}
			return true;
		} else if (o instanceof Collection) {
			Collection<?> c = (Collection<?>) o;
			return c.size() == 0;
		} else if (o instanceof Map) {
			Map<?, ?> m = (Map<?, ?>) o;
			return m.size() == 0;
		} else {
			return o.toString().equals("");
		}
	}

	public static List<File> listFiles() throws IOException {
		return listFiles(null);
	}

	public static List<File> listFiles(File dir) throws IOException {
		return listFiles(dir, null);
	}

	public static List<File> listFiles(File dir, FileFilter filter) throws IOException {
		return listFiles(dir, filter, new ArrayList<File>());
	}

	public static List<File> listFiles(File dir, FileFilter filter, List<File> list)
			throws IOException {
		if (dir == null) {
			String cwd = System.getProperty("user.dir");
			dir = new File(cwd);
		} else if (!dir.isDirectory() || !dir.canRead()) {
			String message = dir.getAbsolutePath() + " is either not a directory or unreadable";
			throw new IOException(message);
		}
		if (list == null) {
			list = new ArrayList<File>();
		}
		for (File file : dir.listFiles(filter)) {
			list.add(file);
			if (file.isDirectory()) {
				listFiles(file, filter, list);
			}
		}
		return list;
	}

	public static String getFileContents(String filename) throws IOException {
		return getFileContents(new File(filename));
	}

	public static String getFileContents(File file) throws IOException {
		Reader in = new FileReader(file);
		return readAll(in, true).toString();
	}

	public static StringBuilder readAll(Reader in, boolean close) throws IOException {
		return readAll(in, DEFAULT_BUFFER_SIZE, close);
	}

	public static StringBuilder readAll(Reader in, int size, boolean close) throws IOException {
		if (in == null) {
			throw new IllegalArgumentException("in must not be null");
		}
		try {
			StringBuilder sb = new StringBuilder();
			int read;
			char[] buf = new char[size];
			while ((read = in.read(buf)) != -1) {
				sb.append(buf, 0, read);
			}
			return sb;
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (close) {
				try {
					in.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	public static int copy(InputStream in, OutputStream out) throws IOException {
		return copy(in, false, out, false, -1, DEFAULT_BUFFER_SIZE);
	}

	public static int copy(InputStream in, boolean closeInputStream, OutputStream out,
			boolean closeOutputStream, int length, int size) throws IOException {
		if (in == null) {
			throw new IllegalArgumentException("neither in nor out may be null");
		}
		int copied = 0;
		try {
			int read;
			byte[] buf = new byte[size];
			int l = size;
			if (length >= 0 && length < size) {
				l = length;
			}
			while ((read = in.read(buf, 0, l)) != -1 && l > 0) {
				out.write(buf, 0, read);
				copied += read;
				length -= read;
				if (length >= 0 && length < size) {
					l = length;
				}
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (closeInputStream) {
				try {
					in.close();
				} catch (IOException ex) {
				}
			}
			if (closeOutputStream) {
				try {
					out.close();
				} catch (IOException ex) {
				}
			}
		}
		return copied;
	}

	public static void writeToFile(String filename, String str, boolean append) throws IOException {
		writeToFile(new File(filename), str, append);
	}

	public static void writeToFile(File file, String str, boolean append) throws IOException {
		FileWriter out = null;
		try {
			out = new FileWriter(file, append);
			out.write(str);
		} catch (IOException ex) {
			throw ex;
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
	}

	public static <T, E extends T> boolean inArray(T needle, E... haystack) {
		if (needle == null || haystack == null) {
			throw new IllegalArgumentException("neither needle nor haystack may be null");
		}
		for (T blade : haystack) {
			if (needle.equals(blade)) {
				return true;
			}
		}
		return false;
	}

	public static <T, E extends T> int getIndexForValue(T needle, E... haystack) {
		if (needle == null || haystack == null) {
			throw new IllegalArgumentException("neither needle nor haystack may be null");
		}
		for (int i = 0; i < haystack.length; i++) {
			if (needle.equals(haystack[i])) {
				return i;
			}
		}
		return -1;
	}

	public static <T, E extends T> boolean inList(T needle, Collection<E> haystack) {
		if (needle == null || haystack == null) {
			throw new IllegalArgumentException("neither needle nor haystack may be null");
		}
		Iterator<E> i = haystack.iterator();
		while (i.hasNext()) {
			if (needle.equals(i.next())) {
				return true;
			}
		}
		return false;
	}

	public static String implode(String... pieces) {
		String glue = "";
		return implode(glue, pieces);
	}

	public static String implode(String glue, String... pieces) {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < pieces.length; i++) {
			if (i > 0) {
				out.append(glue);
			}
			out.append(pieces[i]);
		}
		return out.toString();
	}

	public static String cURL(String uri) throws IOException { // Client URL
																// Request
																// Library (Yes
																// I stole the
																// name!)
		return cURL(uri, null);
	}

	/**
	 * This method works in its entirety, however, it should only be used as a template or example.
	 */
	public static String cURL(String uri, String args) throws IOException {
		// System.out.println(uri);
		OutputStreamWriter out = null;
		try {
			URL url = new URL(uri);
			URLConnection con = url.openConnection();
			con.setUseCaches(false);

			if (args != null) {
				con.setDoOutput(true);
				out = new OutputStreamWriter(con.getOutputStream());
				if (args.startsWith("?")) {
					args = args.substring(1);
				}
				out.write(args);
				out.close();
			}

			Reader in = new InputStreamReader(con.getInputStream());
			return readAll(in, true).toString();
		} catch (IOException ex) {
			throw ex;
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @return the percent of memory being used by the JVM
	 */
	public static double getUsedMemory() {
		Runtime runtime = Runtime.getRuntime();
		long used = runtime.maxMemory()
				- (runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory()));
		return Utils.percentOf(used, runtime.maxMemory());
	}

	/**
	 * @return the free memory available to the JVM
	 */
	public static long getFreeMemory() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
	}

	public static byte[] encrypt(byte[] b, String password, String algorithm, int iterations)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CipherOutputStream out = null;
		try {
			out = new CipherOutputStream(baos,
					getCipher(password, Cipher.ENCRYPT_MODE, algorithm, iterations));
			out.write(b);
		} catch (InvalidKeyException ex) {
			throw new IOException(ex);
		} catch (InvalidAlgorithmParameterException ex) {
			throw new IOException(ex);
		} catch (InvalidKeySpecException ex) {
			throw new IOException(ex);
		} catch (NoSuchPaddingException ex) {
			throw new IOException(ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new IOException(ex);
		} finally {
			try {
				out.close();
			} catch (Exception ex) {
			}
		}
		return baos.toByteArray();
	}

	public static byte[] decrypt(byte[] b, int size, String password, String algorithm,
			int iterations) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CipherInputStream in = null;
		try {
			in = new CipherInputStream(new ByteArrayInputStream(b),
					getCipher(password, Cipher.DECRYPT_MODE, algorithm, iterations));
			int read;
			byte[] buf = new byte[size];
			while ((read = in.read(buf)) != -1) {
				baos.write(buf, 0, read);
			}
		} catch (InvalidKeyException ex) {
			throw new IOException(ex);
		} catch (InvalidAlgorithmParameterException ex) {
			throw new IOException(ex);
		} catch (InvalidKeySpecException ex) {
			throw new IOException(ex);
		} catch (NoSuchPaddingException ex) {
			throw new IOException(ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new IOException(ex);
		} finally {
			try {
				in.close();
			} catch (Exception ex) {
			}
		}
		return baos.toByteArray();
	}

	public static byte[] compress(byte[] b) throws IOException {
		GZIPOutputStream out = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			out = new GZIPOutputStream(baos);
			out.write(b);
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
				}
			}
		}
		return baos.toByteArray();
	}

	public static byte[] decompress(byte[] b) throws IOException {
		return decompress(b, DEFAULT_BUFFER_SIZE);
	}

	public static byte[] decompress(byte[] b, int size) throws IOException {
		GZIPInputStream in = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			in = new GZIPInputStream(new ByteArrayInputStream(b));
			copy(in, true, baos, true, -1, size);
		} catch (IOException ex) {
			throw ex;
		}
		return baos.toByteArray();
	}

	public static String xmlEncode(Object o) throws IOException {
		ByteArrayOutputStream baos = null;
		XMLEncoder out = null;
		try {
			out = new XMLEncoder(baos = new ByteArrayOutputStream());
			out.writeObject(o);
		} finally {
			out.close();
		}
		return baos.toString(UTF8);
	}

	public static Object xmlDecode(String s) throws IOException {
		XMLDecoder in = null;
		try {
			in = new XMLDecoder(new ByteArrayInputStream(s.getBytes(UTF8)));
			return in.readObject();
		} catch (IOException ex) {
			throw ex;
		} finally {
			in.close();
		}
	}

	public static byte[] serialize(Object o) throws IOException {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(baos = new ByteArrayOutputStream());
			out.writeObject(o);
			return baos.toByteArray();
		} catch (IOException ex) {
			throw ex;
		} finally {
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
	}

	public static Object deserialize(byte[] b) throws IOException {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new ByteArrayInputStream(b));
			return in.readObject();
		} catch (IOException ex) {
			throw ex;
		} catch (ClassNotFoundException ex) {
			throw new IOException(ex);
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * @param num1 inclusive
	 * @param num2 exclusive
	 * @return
	 */
	public static int random(int num1, int num2) {
		if (num1 == num2) {
			return num1;
		}
		int low = Math.min(num1, num2), high = Math.max(num1, num2);
		int range = (int) difference(low, high);
		return low + (int) (Math.random() * range);
	}

	/**
	 * @param num1 inclusive
	 * @param num2 exclusive
	 * @param place places to round past the decimal
	 * @return
	 */
	public static double random(double num1, double num2, int place) {
		if (num1 == num2) {
			return num1;
		}
		double low = Math.min(num1, num2);
		double high = Math.max(num1, num2);
		double range = difference(low, high);
		return round(low + (range * Math.random()), place);
	}

	public static String randomString(final int length) {
		String alphabet =
				"abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
		return randomString(alphabet, length);
	}

	public static String randomString(String characters, int length) {
		String r = shuffle(characters);
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length) {
			sb.append(r.charAt(random(0, r.length())));
		}
		return sb.toString();
	}

	public static String shuffle(String s) {
		List<Character> chars = Arrays.asList(toObject(s.toCharArray()));
		Collections.shuffle(chars);
		Character[] characters = chars.toArray(new Character[chars.size()]);
		return String.valueOf(toPrimitive(characters));
	}

	/**
	 * @return the absolute difference between <tt>num1</tt> and <tt>num2</tt>
	 */
	public static double difference(double num1, double num2) {
		if (num1 == num2) {
			return 0;
		}
		double low = Math.min(num1, num2), high = Math.max(num1, num2);
		if (high < 0) { // both are negative
			return Math.abs(low - high);
		} else if (low < 0) { // only low is negative
			return Math.abs(low) + high;
		} else { // both are positive
			return high - low;
		}
	}

	/**
	 * Returns a "default" cipher for use with cipher IO streams
	 * 
	 * @param password the (<code>String<code>) password to use for this cipher
	 * &#64;param mode
	 *            <code>Cipher.ENCRYPT_MODE</code> or <code>Cipher.DECRYPT_MODE</code>
	 * @see javax.crypto.CipherInputStream
	 * @see javax.crypto.CipherOutputStream
	 * @return
	 */
	public static Cipher getCipher(String password, int mode, String algorithm, int iterations)
			throws InvalidAlgorithmParameterException, InvalidKeySpecException,
			NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {

		// prepare password
		char[] characters = null;
		if (password != null) {
			characters = password.toCharArray();
		}

		// create the key
		KeySpec keySpec = new PBEKeySpec(characters);
		SecretKey key = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);
		Cipher cipher = Cipher.getInstance(key.getAlgorithm());

		// 8-byte salt
		final byte[] salt = {(byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56,
				(byte) 0x35, (byte) 0xE3, (byte) 0x03,};
		AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterations);

		// create the cipher
		cipher.init(mode, key, paramSpec);

		return cipher;
	}

	/**
	 * 93% faster than using DecimalFormat
	 * 
	 * @param d
	 * @param place
	 * @return
	 */
	public static double round(double d, int place) {
		if (place < 1) {
			return (long) d;
		}
		int base = 10;
		long p = (long) Math.pow(base, place);
		return (long) (d * p) / (double) p;
	}

	/**
	 * Formats a floating point number to two places past the decimal with grouping disabled. This
	 * is the same as calling <tt>Utils.format(d, 2);</tt>
	 * 
	 * @param d the number to be formatted
	 * @return the formatted number
	 * @see Utils#format(double d, int decimal)
	 */
	public static String format(double d) {
		return format(d, 2);
	}

	/**
	 * Formats a floating point number to <code>d</code> places past the decimal with grouping
	 * disabled.
	 * 
	 * @param d the number to be formatted
	 * @param decimal places past the decimal point to truncate the number
	 * @return the formatted number
	 * @see Utils#format(double d, int decimal, boolean useGrouping)
	 */
	public static String format(double d, int decimal) {
		return format(d, decimal, false);
	}

	/**
	 * Formats a floating point number to <code>d</code> places past the decimal.
	 * 
	 * @param d the number to be formatted
	 * @param decimal places past the decimal point to truncate the number
	 * @param useGrouping whether or not to use grouping (thousands, millions, ect...)
	 * @return the formatted number
	 */
	public static String format(double d, int decimal, boolean useGrouping) {
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMinimumFractionDigits(decimal);
		formatter.setMaximumFractionDigits(decimal);
		formatter.setGroupingUsed(useGrouping);
		String f = formatter.format(d);
		if (f.matches("^-[0.]*$")) { // negative zero
			f = f.substring(1);
		}
		return f;
	}

	/**
	 * alias of <code>Toolkit.getDefaultToolkit().beep();</code>
	 * 
	 * @deprecated this is more for reference than anything else
	 */
	public static void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	public static BufferedImage screenCapture() throws AWTException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle r = new Rectangle();
		for (GraphicsDevice gd : ge.getScreenDevices()) {
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			Rectangle bounds = gc.getBounds();
			r.x = Math.min(r.x, bounds.x); // left-most
			r.y = Math.min(r.y, bounds.y); // top-most
			int width = bounds.width - r.x;
			if (bounds.x > 0) {
				width += bounds.x;
			}
			r.width = Math.max(r.width, width); // right-most
			int height = bounds.height - r.y;
			if (bounds.y > 0) {
				height += bounds.y;
			}
			r.height = Math.max(r.height, height); // bottom-most
		}
		return screenCapture(r);
	}

	/**
	 * 
	 * @param points
	 * @return
	 * @deprecated this is more for reference than anything else
	 */
	public static Rectangle calculateBounds(Point[] points) {
		if (points == null) {
			throw new IllegalArgumentException("points must not be null");
		} else if (points.length == 0) {
			return new Rectangle();
		}
		Rectangle r = new Rectangle(points[0]);
		for (int i = 1; i < points.length; i++) {
			r.add(points[i]);
		}
		return r;
	}

	public static BufferedImage screenCapture(int device) throws AWTException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getScreenDevices()[device];
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		return screenCapture(gc.getBounds());
	}

	public static BufferedImage screenCaptureDefault() throws AWTException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		return screenCapture(gc.getBounds());
	}

	public static BufferedImage screenCapture(Component c) throws AWTException {
		Rectangle bounds = new Rectangle(c.getLocationOnScreen(), c.getSize());
		return screenCapture(bounds);
	}

	public static BufferedImage screenCapture(Rectangle r) throws AWTException {
		Robot robot = new Robot();
		return robot.createScreenCapture(r);
	}

	public static CharSequence chop(CharSequence s) {
		if (s.length() == 0) {
			return "";
		}
		return s.subSequence(0, s.length() - 1);
	}

	public static String toMD5(String input) {
		return toMD5(input.getBytes());
	}

	public static String toMD5(byte[] input) {
		String algorithm = "MD5";
		try {
			return hash(input, algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String toSHA1(byte[] input) {
		String algorithm = "SHA-1";
		try {
			return hash(input, algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String hash(byte[] input, String algorithm) throws NoSuchAlgorithmException {
		MessageDigest message = MessageDigest.getInstance(algorithm);
		message.reset(); // reset previous use, if any
		message.update(input);
		int hexRadix = 16; // binary
		return new BigInteger(message.digest()).toString(hexRadix);
	}

	public static <E> E def(E obj, E def) {
		return obj == null ? def : obj;
	}

	/************************************************
	 * primitive array <-> Object array conversion
	 ************************************************/

	public static byte[] toPrimitive(Byte... bytes) {
		byte[] buf = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			buf[i] = bytes[i].byteValue();
		}
		return buf;
	}

	public static short[] toPrimitive(Short... shorts) {
		short[] buf = new short[shorts.length];
		for (int i = 0; i < shorts.length; i++) {
			buf[i] = shorts[i].shortValue();
		}
		return buf;
	}

	public static int[] toPrimitive(Integer... integers) {
		int[] buf = new int[integers.length];
		for (int i = 0; i < integers.length; i++) {
			buf[i] = integers[i].intValue();
		}
		return buf;
	}

	public static long[] toPrimitive(Long... longs) {
		long[] buf = new long[longs.length];
		for (int i = 0; i < longs.length; i++) {
			buf[i] = longs[i].longValue();
		}
		return buf;
	}

	public static float[] toPrimitive(Float... floats) {
		float[] buf = new float[floats.length];
		for (int i = 0; i < floats.length; i++) {
			buf[i] = floats[i].floatValue();
		}
		return buf;
	}

	public static double[] toPrimitive(Double... doubles) {
		double[] buf = new double[doubles.length];
		for (int i = 0; i < doubles.length; i++) {
			buf[i] = doubles[i].longValue();
		}
		return buf;
	}

	public static boolean[] toPrimitive(Boolean... booleans) {
		boolean[] buf = new boolean[booleans.length];
		for (int i = 0; i < booleans.length; i++) {
			buf[i] = booleans[i].booleanValue();
		}
		return buf;
	}

	public static char[] toPrimitive(Character... characters) {
		char[] buf = new char[characters.length];
		for (int i = 0; i < characters.length; i++) {
			buf[i] = characters[i].charValue();
		}
		return buf;
	}

	public static Byte[] toObject(byte... bytes) {
		Byte[] buf = new Byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			buf[i] = Byte.valueOf(bytes[i]);
		}
		return buf;
	}

	public static Short[] toObject(short... shorts) {
		Short[] buf = new Short[shorts.length];
		for (int i = 0; i < shorts.length; i++) {
			buf[i] = Short.valueOf(shorts[i]);
		}
		return buf;
	}

	public static Integer[] toObject(int... ints) {
		Integer[] buf = new Integer[ints.length];
		for (int i = 0; i < ints.length; i++) {
			buf[i] = Integer.valueOf(ints[i]);
		}
		return buf;
	}

	public static Long[] toObject(long... longs) {
		Long[] buf = new Long[longs.length];
		for (int i = 0; i < longs.length; i++) {
			buf[i] = Long.valueOf(longs[i]);
		}
		return buf;
	}

	public static Float[] toObject(float... floats) {
		Float[] buf = new Float[floats.length];
		for (int i = 0; i < floats.length; i++) {
			buf[i] = Float.valueOf(floats[i]);
		}
		return buf;
	}

	public static Double[] toObject(double... doubles) {
		Double[] buf = new Double[doubles.length];
		for (int i = 0; i < doubles.length; i++) {
			buf[i] = Double.valueOf(doubles[i]);
		}
		return buf;
	}

	public static Boolean[] toObject(boolean... booleans) {
		Boolean[] buf = new Boolean[booleans.length];
		for (int i = 0; i < booleans.length; i++) {
			buf[i] = Boolean.valueOf(booleans[i]);
		}
		return buf;
	}

	public static Character[] toObject(char... characters) {
		Character[] buf = new Character[characters.length];
		for (int i = 0; i < characters.length; i++) {
			buf[i] = Character.valueOf(characters[i]);
		}
		return buf;
	}

	/**
	 * Java Runtime Environment version
	 * 
	 * @return Java Runtime Environment version
	 */
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}

	/**
	 * Java Runtime Environment vendor
	 * 
	 * @return Java Runtime Environment vendor
	 */
	public static String getJavaVendor() {
		return System.getProperty("java.vendor");
	}

	/**
	 * Java vendor URL
	 * 
	 * @return Java vendor URL
	 */
	public static String getJavaVendorURL() {
		return System.getProperty("java.vendor.url");
	}

	/**
	 * Java installation directory
	 * 
	 * @return Java installation directory
	 */
	public static String getJavaHome() {
		return System.getProperty("java.home");
	}

	/**
	 * Java Virtual Machine specification version
	 * 
	 * @return Java Virtual Machine specification version
	 */
	public static String getJavaVMSpecificationVersion() {
		return System.getProperty("java.vm.specification.version");
	}

	/**
	 * Java Virtual Machine specification vendor
	 * 
	 * @return Java Virtual Machine specification vendor
	 */
	public static String getJavaVMSpecificationVendor() {
		return System.getProperty("java.vm.specification.vendor");
	}

	/**
	 * Java Virtual Machine specification name
	 * 
	 * @return Java Virtual Machine specification name
	 */
	public static String getJavaVMSpecificationName() {
		return System.getProperty("java.vm.specification.name");
	}

	/**
	 * Java Virtual Machine implementation version
	 * 
	 * @return Java Virtual Machine implementation version
	 */
	public static String getJavaVMVersion() {
		return System.getProperty("java.vm.version");
	}

	/**
	 * Java Virtual Machine implementation vendor
	 * 
	 * @return Java Virtual Machine implementation vendor
	 */
	public static String getJavaVMVendor() {
		return System.getProperty("java.vm.vendor");
	}

	/**
	 * Java Virtual Machine implementation name
	 * 
	 * @return Java Virtual Machine implementation name
	 */
	public static String getJavaVMName() {
		return System.getProperty("java.vm.name");
	}

	/**
	 * Java Runtime Environment specification version
	 * 
	 * @return Java Runtime Environment specification version
	 */
	public static String getJavaSpecificationVersion() {
		return System.getProperty("java.specification.version");
	}

	/**
	 * Java Runtime Environment specification vendor
	 * 
	 * @return Java Runtime Environment specification vendor
	 */
	public static String getJavaSpecificationVendor() {
		return System.getProperty("java.specification.vendor");
	}

	/**
	 * Java Runtime Environment specification name
	 * 
	 * @return Java Runtime Environment specification name
	 */
	public static String getJavaSpecificationName() {
		return System.getProperty("java.specification.name");
	}

	/**
	 * Java class format version number
	 * 
	 * @return Java class format version number
	 */
	public static String getJavaClassVersion() {
		return System.getProperty("java.class.version");
	}

	/**
	 * Java class path
	 * 
	 * @return Java class path
	 */
	public static String getJavaClassPath() {
		return System.getProperty("java.class.path");
	}

	/**
	 * List of paths to search when loading libraries
	 * 
	 * @return List of paths to search when loading libraries
	 */
	public static String getJavaLibraryPath() {
		return System.getProperty("java.library.path");
	}

	/**
	 * List of paths to search when loading libraries
	 * 
	 * @return List of paths to search when loading libraries
	 */
	public static String[] getJavaLibraryPaths() {
		return System.getProperty("java.library.path").split(getPathSeparator());
	}

	/**
	 * Default temp file path
	 * 
	 * @return Default temp file path
	 */
	public static String getJavaIOTemporaryDirectory() {
		return System.getProperty("java.io.tmpdir");
	}

	/**
	 * Name of JIT compiler to use
	 * 
	 * @return Name of JIT compiler to use
	 */
	public static String getJavaCompiler() {
		return System.getProperty("java.compiler");
	}

	/**
	 * Path of extension directory or directories
	 * 
	 * @return Path of extension directory or directories
	 */
	public static String getJavaExtensionDirectory() {
		return System.getProperty("java.ext.dirs");
	}

	/**
	 * Path of extension directory or directories
	 * 
	 * @return Path of extension directory or directories
	 */
	public static String[] getJavaExtensionDirectories() {
		return System.getProperty("java.ext.dirs").split(getPathSeparator());
	}

	/**
	 * Operating system name
	 * 
	 * @return Operating system name
	 */
	public static String getOSName() {
		return System.getProperty("os.name");
	}

	/**
	 * Operating system architecture
	 * 
	 * @return Operating system architecture
	 */
	public static String getOSArchitecture() {
		return System.getProperty("os.arch");
	}

	/**
	 * Operating system version
	 * 
	 * @return Operating system version
	 */
	public static String getOSVersion() {
		return System.getProperty("os.version");
	}

	/**
	 * File separator ("/" on UNIX)
	 * 
	 * @return File separator ("/" on UNIX)
	 */
	public static String getFileSeparator() {
		return System.getProperty("file.separator");
	}

	/**
	 * Path separator (":" on UNIX)
	 * 
	 * @return Path separator (":" on UNIX)
	 */
	public static String getPathSeparator() {
		return System.getProperty("path.separator");
	}

	/**
	 * Line separator ("\n" on UNIX)
	 * 
	 * @return Line separator ("\n" on UNIX)
	 */
	public static String getLineSeparator() {
		return System.getProperty("line.separator");
	}

	/**
	 * User's account name
	 * 
	 * @return User's account name
	 */
	public static String getAccountName() {
		return System.getProperty("user.name");
	}

	/**
	 * User's home directory
	 * 
	 * @return User's home directory
	 */
	public static String getHomeDirectory() {
		return System.getProperty("user.home");
	}

	/**
	 * User's current working directory
	 * 
	 * @return User's current working directory
	 */
	public static String getCurrentWorkingDirectory() {
		return System.getProperty("user.dir");
	}

}
