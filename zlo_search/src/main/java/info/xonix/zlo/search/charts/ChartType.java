package info.xonix.zlo.search.charts;

/**
 * User: xonix
 * Date: 8/20/14
 * Time: 8:47 PM
 */
public enum ChartType {
    Trend("Обсуждаемость"),
    ByHour("По часам дня"),
    ByWeekDay("По дням недели"),
    DayInterval("Активность в интервале");

    private String title;

    ChartType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
