package google.calendar;

import java.util.Calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

/**
 * @author Matan
 * Class for constructing a google-event type. Contains the options of writing a begin & end data
 * Set the description and the title.
 * Further enhancement can be done by using the api from:
 * https://developers.google.com/resources/api-libraries/documentation/calendar/v3/java/latest/com/google/api/services/calendar/model/Event.html#getSummary--
 * 
 */
public class EventBuilder {
	private String title;

	private String description;
	private EventDateTime start;
	private EventDateTime end;
	
	public EventBuilder setEndDate(Calendar x) {
		x.add(Calendar.DATE, 1);
		this.end = new EventDateTime().setDate(new DateTime(x.getTime()));
		return this;
	}
	public EventBuilder setStartDate(Calendar x) {
		x.add(Calendar.DATE, 1);
		this.start =new EventDateTime().setDate(new DateTime(x.getTime()));
		return this;
	}
	
	public EventBuilder setTitle(String title) {
		this.title = String.valueOf(title);
		return this;
	}
	public EventBuilder setDescription(String description) {
		this.description = String.valueOf(description);
		return this;
	}

	public Event build() {
		return new Event().setSummary(this.title).setStart(start).setEnd(end).setDescription(description);
	}
}
