package google.tasks;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;

public class GoogleTasksAPI {
	private static final String APPLICATION_NAME = "Google Tasks API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(TasksScopes.TASKS);
	private static final String CREDENTIALS_FILE_PATH = "credentials.json";

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		return new AuthorizationCodeInstalledApp(
				new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
						GoogleClientSecrets.load(JSON_FACTORY,
								new InputStreamReader(GoogleTasksAPI.class.getResourceAsStream(CREDENTIALS_FILE_PATH))),
						SCOPES).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
								.setAccessType("offline").build(),
				new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");
	}

	private static Tasks setupService() {
		NetHttpTransport HTTP_TRANSPORT;
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			return new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
					.setApplicationName(APPLICATION_NAME).build();
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static void addNewTask(Task task) {
		Tasks service = setupService();
		assert service != null;
		try {
			service.tasks().insert("@default", task).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Task> listAllTasks() {
		Tasks service = setupService();
		assert service != null;
		try {
			return service.tasks().list("@default").execute().getItems();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new LinkedList<>();
		}
	}

//	public static void main(String... args) {
//		 Build a new authorized API client service.
//
//		com.google.api.services.tasks.model.Tasks tasks = service.tasks()
//				.list(service.tasklists().list().setMaxResults(10L).execute().getItems().get(0).getId()).execute();
//		tasks.getItems().stream().forEach(t -> System.out.println(t.getTitle()));
//		System.out.println("Done!");
//	}
}