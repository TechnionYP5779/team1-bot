package course.info;

import org.junit.Ignore;
import org.junit.Test;

public class DataToDBLoaderTest {
	@Ignore
	@Test
	@SuppressWarnings("static-method")
	public void testQuery() {
		DataToDBLoader.loadToDBFromJson("courseInfo.json");
	}
}
