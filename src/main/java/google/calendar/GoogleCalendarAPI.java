package google.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;
//import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GoogleCalendarAPI {
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "./credentials.json";

	private static Calendar setupService() {
		NetHttpTransport HTTP_TRANSPORT;
		try {
	        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	                .setApplicationName(APPLICATION_NAME)
	                .build();
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        return new AuthorizationCodeInstalledApp(new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				GoogleClientSecrets.load(JSON_FACTORY,
						new InputStreamReader(GoogleCalendarAPI.class.getResourceAsStream(CREDENTIALS_FILE_PATH))),
				SCOPES).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build(),
				new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");
    }
    
	/**
	 * 
	 * @param calendarId - use 'primary' for the default main calendar.
	 * @param e - event to be added
	 */
	public static void addNewEvent(String calendarId, Event e) {
		Calendar service = setupService();
		assert service != null;
		try {
			service.events().insert(calendarId, e).execute();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param calendarId - use 'primary' for the default main calendar.
	 * @return all events in the calendarId
	 */
	public static List<Event> listAllEvents(String calendarId) {
		Calendar service = setupService();
		assert service != null;
		try {
			return service.events().list(calendarId).execute().getItems();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new LinkedList<>();
		}
	}
	
//	// use case example
//    public static void main(String... args) {
//		listAllEvents("primary").stream().forEach(t -> System.out.println(t.getSummary()));
//    	
//    }

}