package google.calendar;
import java.util.Date;

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
	
	/**
	 * Calendar-Date Can be created using = 
	 *	Calendar cal = Calendar.getInstance();
	 *	cal.set(Calendar.YEAR, 1988);
	 *	cal.set(Calendar.MONTH, Calendar.JANUARY);
	 *	cal.set(Calendar.DAY_OF_MONTH, 1);
	 * Date dateRepresentation = cal.getTime();
	 */
	public EventBuilder setEndDate(Date x) {
		this.end = new EventDateTime().setDate(new DateTime(x));
		return this;
	}
	
	/**
	 * Calendar-Date Can be created using = 
	 *	Calendar cal = Calendar.getInstance();
	 *	cal.set(Calendar.YEAR, 1988);
	 *	cal.set(Calendar.MONTH, Calendar.JANUARY);
	 *	cal.set(Calendar.DAY_OF_MONTH, 1);
	 * Date dateRepresentation = cal.getTime();
	 */
	public EventBuilder setStartDate(Date x) {
		this.start =new EventDateTime().setDate(new DateTime(x));
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
