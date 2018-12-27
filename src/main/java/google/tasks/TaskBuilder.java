package google.tasks;

import java.util.Calendar;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;

public class TaskBuilder {
	private DateTime dueDate;
	private String title;
	private String notes;
	
	public TaskBuilder setTitle(String title) {
		this.title = String.valueOf(title);
		return this;
	}
	
	public TaskBuilder setDueDate(Calendar dueDate) {
		dueDate.add(Calendar.DATE, 1);
		this.dueDate = new DateTime(dueDate.getTime());
		return this;
	}
	
	public TaskBuilder setNotes(String notes) {
		this.notes = String.valueOf(notes);
		return this;
	}
	
	public Task build() {
		return new Task().setTitle(title).setDue(dueDate).setNotes(notes);
	}
}
