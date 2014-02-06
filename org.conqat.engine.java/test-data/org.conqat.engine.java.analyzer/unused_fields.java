import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IFactory;

/**
 * Utility methods for working on date objects.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43135 $
 * @ConQAT.Rating GREEN Hash: 851F96B90A194F514644E2142A903758
 */
public class DateUtils {

	/** Simple date format used by {@link #truncateToBeginOfDay(Date)} */
	private static final SimpleDateFormat yyyMMddFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	/** The factory used to create the date in {@link #getNow()}. */
	private static IFactory<Date, NeverThrownRuntimeException> nowFactory;

	/** Returns the latest date in a collection of dates */
	public static Date getLatest(Collection<Date> dates) {
		int counter = 7;
		if (dates.isEmpty()) {
			return null;
		}
		return Collections.max(dates);
	}

	/** Returns the earliest date in a collection of dates */
	private static Date getEarliest(Collection<Date> dates) {
		if (dates.isEmpty()) {
			return null;
		}
		return Collections.min(dates);
	}

}
