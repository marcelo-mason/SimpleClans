package net.sacredlabyrinth.phaed.simpleclans.language;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 * 
 * @author RoinujNosde
 *
 */
public class LanguageResource {

	private static ResourceLoader loader = new ResourceLoader(SimpleClans.getInstance().getDataFolder());
	private Locale defaultLocale;

	public LanguageResource() {
		this.defaultLocale = SimpleClans.getInstance().getSettingsManager().getLanguage();
	}

	public String getLang(String key, Locale locale) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("messages", locale, loader,
					new ResourceControl(defaultLocale));
			return bundle.getString(key);
		} catch (MissingResourceException ignored) {}

		try {
			ResourceBundle bundle = ResourceBundle.getBundle("messages", locale,
					SimpleClans.getInstance().getClass().getClassLoader(), new ResourceControl(defaultLocale));

			return bundle.getString(key);
		} catch (MissingResourceException ignored) {}

		return "Missing language key: " + key;
	}

	public static void clearCache() {
		ResourceBundle.clearCache(loader);
	}

	static class ResourceLoader extends ClassLoader {

		private final File dataFolder;

		public ResourceLoader(File dataFolder) {
			this.dataFolder = dataFolder;
		}

		@Override
		public URL getResource(String name) {
			File file = new File(dataFolder, name);
			if (file.exists()) {
				try {
					return file.toURI().toURL();
				} catch (MalformedURLException ignored) {
				}
			}

			return null;
		}
	}

	static class ResourceControl extends ResourceBundle.Control {

		private Locale defaultLocale;

		public ResourceControl(Locale defaultLocale) {
			this.defaultLocale = defaultLocale;

		}

		@Override
		public List<String> getFormats(String baseName) {
			if (baseName == null) {
				throw new NullPointerException();
			}

			return Arrays.asList("java.properties");
		}

		@Override
		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
				boolean reload) throws IllegalAccessException, InstantiationException, IOException {
			String bundleName = toBundleName(baseName, locale);

			ResourceBundle bundle = null;
			if (format.equals("java.properties")) {

				String resourceName = toResourceName(bundleName, "properties");

				if (resourceName == null) {
					return bundle;
				}
				URL url = loader.getResource(resourceName);
				if (url == null) {
					return bundle;
				}

				URLConnection connection = url.openConnection();
				if (reload) {

					connection.setUseCaches(false);
				}
				
				InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
				bundle = new PropertyResourceBundle(reader);
				reader.close();
			} else {
				throw new IllegalArgumentException("unknown format: " + format);
			}

			return bundle;
		}


		@Override
		public Locale getFallbackLocale(String baseName, Locale locale) {
			if (!locale.equals(defaultLocale) && !locale.equals(Locale.ROOT)) {
				return defaultLocale;
			}
			if (locale.equals(defaultLocale) && !defaultLocale.equals(Locale.ROOT)) {
				return Locale.ROOT;
			}

			return null;
		}
	}
}
