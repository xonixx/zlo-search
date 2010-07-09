package info.xonix.zlo.search.progs;

/**
 * Author: Vovan
 * Date: 17.12.2007
 * Time: 21:25:36
 */

public class InsertEmptyRows {

    private static int MAX_ALEXZAM = 4030808; // 4 030 808 - max alexzam db row
    private static int N = 10000;

/*    public static void main(String[] args) {
        new Config();
        int n = 0;

        Site site = Site.forName("zlo");
        DbManager dbm = AppSpringContext.get(DbManager.class);

        int addedEmpty = 0;
        while (n <= MAX_ALEXZAM + N) {
            ArrayList<Integer> nums_n_N = new ArrayList<Integer>();

*//*            DbResult r = DbUtils.executeSelect(
                    site,
                    "select num from messages where num>=? and num<?;",
                    new Object[]{n, n + N}, new VarType[]{VarType.INTEGER, VarType.INTEGER});*//*
            int num;
            while ((num = r.getOneInt()) != -1) {
                nums_n_N.add(num);
            }

            List<Message> needToAdd = new ArrayList<Message>();
            for (int j = n; j < n + N; j++) {
                if (!nums_n_N.contains(j)) {
                    Message m = new Message();
                    m.setNum(j);
                    m.setStatus(MessageStatus.UNKNOWN);
                    needToAdd.add(m);
                }
            }
            nums_n_N.clear();
            System.out.println(MessageFormat.format("Need to add {0} empty messages from {1} to {2} ", needToAdd.size(), n, n + N));

            if (needToAdd.size() > 0)
                dbm.saveMessagesFast(needToAdd);

            addedEmpty += needToAdd.size();
            n = n + N;
        }
        System.out.println("Added empty: " + addedEmpty);
    }*/
}
