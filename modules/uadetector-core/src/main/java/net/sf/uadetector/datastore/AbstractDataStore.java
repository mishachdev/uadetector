/*******************************************************************************
 * Copyright 2012 André Rouél
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sf.uadetector.datastore;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.uadetector.datareader.DataReader;
import net.sf.uadetector.exception.CanNotOpenStreamException;
import net.sf.uadetector.internal.data.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract implementation to store <em>UAS data</em> only in the heap space.<br>
 * <br>
 * A store must always have an usable instance of {@link Data}. It is recommended to initialize it with the supplied UAS
 * file in the <em>uadetector-resources</em> module.
 * 
 * @author André Rouél
 */
public abstract class AbstractDataStore implements DataStore {

	/**
	 * Corresponding default logger for this class
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDataStore.class);

	/**
	 * Creates an {@code URL} instance from the given {@code String} representation.<br>
	 * <br>
	 * This method tunnels a {@link MalformedURLException} by an {@link IllegalArgumentException}.
	 * 
	 * @param url
	 *            {@code String} representation of an {@code URL}
	 * @return new {@code URL} instance
	 * @throws IllegalArgumentException
	 *             if a {@link MalformedURLException} occurs
	 */
	protected static final URL buildUrl(final String url) {
		URL ret = null;
		try {
			ret = new URL(url);
		} catch (final MalformedURLException e) {
			throw new IllegalArgumentException("The given string is not a valid URL: " + url, e);
		}
		return ret;
	}

	/**
	 * This method reads the given {@link InputStream} by using an {@link DataReader}. The new created instance of
	 * {@link Data} will be returned.
	 * 
	 * @param stream
	 *            {@link InputStream} with <em>UAS data</em>
	 * @return new created instance of {@code Data} and never {@code null}
	 * @throws IllegalArgumentException
	 *             if the given argument is {@code null}
	 */
	protected static final Data readData(final InputStream stream, final DataReader reader) {
		if (stream == null) {
			throw new IllegalArgumentException("Argument 'stream' must not be null.");
		}
		if (reader == null) {
			throw new IllegalArgumentException("Argument 'reader' must not be null.");
		}

		return reader.read(stream);
	}

	/**
	 * This method reads the given {@link URL} by using an {@link DataReader}. The new created instance of {@link Data}
	 * will be returned.
	 * 
	 * @param url
	 *            URL to <em>UAS data</em>
	 * @return new created instance of {@code Data} and never {@code null}
	 * @throws IllegalArgumentException
	 *             if the given argument is {@code null}
	 * @throws CanNotOpenStreamException
	 *             if no stream to the given {@code URL} can be established
	 */
	protected static final Data readData(final URL url, final DataReader reader) {
		if (url == null) {
			throw new IllegalArgumentException("Argument 'url' must not be null.");
		}
		if (reader == null) {
			throw new IllegalArgumentException("Argument 'reader' must not be null.");
		}

		try {
			return readData(url.openStream(), reader);
		} catch (final IOException e) {
			throw new CanNotOpenStreamException(url.toString());
		}
	}

	/**
	 * Current <em>UAS data</em>
	 */
	private Data data;

	/**
	 * The data reader to read in <em>UAS data</em>
	 */
	private final DataReader reader;

	/**
	 * The {@code URL} to get <em>UAS data</em>
	 */
	private final URL dataUrl;

	/**
	 * The {@code URL} to get the latest version information of <em>UAS data</em>
	 */
	private final URL versionUrl;

	/**
	 * Constructs an new instance of {@link AbstractDataStore}.
	 * 
	 * @param data
	 *            first <em>UAS data</em> which will be available in the store
	 * @throws IllegalArgumentException
	 *             if the given argument is {@code null}
	 */
	protected AbstractDataStore(final Data data, final DataReader reader, final URL dataUrl, final URL versionUrl) {
		if (data == null) {
			throw new IllegalArgumentException("Argument 'data' must not be null.");
		}
		if (reader == null) {
			throw new IllegalArgumentException("Argument 'reader' must not be null.");
		}
		if (dataUrl == null) {
			throw new IllegalArgumentException("Argument 'dataUrl' must not be null.");
		}
		if (versionUrl == null) {
			throw new IllegalArgumentException("Argument 'versionUrl' must not be null.");
		}

		this.data = data;
		this.reader = reader;
		this.dataUrl = dataUrl;
		this.versionUrl = versionUrl;
	}

	/**
	 * Constructs an {@code SimpleDataStore} by reading the given {@code dataUrl} as <em>UAS data</em>.
	 * 
	 * @param reader
	 *            data reader to read the given {@code dataUrl}
	 * @param dataUrl
	 *            URL to <em>UAS data</em>
	 * @param versionUrl
	 *            URL to version information about the given <em>UAS data</em>
	 * @throws IllegalArgumentException
	 *             if one of given arguments is {@code null}
	 * @throws IllegalArgumentException
	 *             if the given strings are not valid URLs
	 * @throws CanNotOpenStreamException
	 *             when no streams to the given {@code URL}s can be established
	 */
	protected AbstractDataStore(final DataReader reader, final String dataUrl, final String versionUrl) {
		this(reader, buildUrl(dataUrl), buildUrl(versionUrl));
	}

	/**
	 * Constructs an {@code SimpleDataStore} by reading the given {@code dataUrl} as <em>UAS data</em>.
	 * 
	 * @param reader
	 *            data reader to read the given {@code dataUrl}
	 * @param dataUrl
	 *            URL to <em>UAS data</em>
	 * @param versionUrl
	 *            URL to version information about the given <em>UAS data</em>
	 * @throws IllegalArgumentException
	 *             if the given argument is {@code null}
	 * @throws CanNotOpenStreamException
	 *             when no streams to the given {@code URL}s can be established
	 */
	protected AbstractDataStore(final DataReader reader, final URL dataUrl, final URL versionUrl) {
		this(readData(dataUrl, reader), reader, dataUrl, versionUrl);
	}

	@Override
	public Data getData() {
		return data;
	}

	@Override
	public DataReader getDataReader() {
		return reader;
	}

	@Override
	public URL getDataUrl() {
		return dataUrl;
	}

	@Override
	public URL getVersionUrl() {
		return versionUrl;
	}

	/**
	 * Sets new <em>UAS data</em> in the store.
	 * 
	 * @param data
	 *            <em>UAS data</em> to override the current ({@code null} is not allowed)
	 * @throws IllegalArgumentException
	 *             if the given argument is {@code null}
	 */
	@Override
	public void setData(final Data data) {
		if (data == null) {
			throw new IllegalArgumentException("Argument 'data' must not be null.");
		}

		this.data = data;

		// add some useful UAS data informations to the log
		if (LOG.isDebugEnabled()) {
			LOG.debug(data.toStats());
		}
	}

}
