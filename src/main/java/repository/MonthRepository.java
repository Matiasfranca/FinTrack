package repository;

import model.Month;
import java.time.YearMonth;

public interface MonthRepository {

    /**
     * Retrieves a month from the database based on the year and month.
     * If the month does not exist, it is automatically created and the new instance
     * is returned.
     *
     * @param yearMonth The YearMonth object containing the desired year and month.
     * @return The corresponding Month entity, with its database ID.
     */
    Month getOrCreate(YearMonth yearMonth);

}