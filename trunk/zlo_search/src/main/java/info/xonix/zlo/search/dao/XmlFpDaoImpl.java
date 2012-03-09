package info.xonix.zlo.search.dao;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.HashMap;
import java.util.List;

/**
 * User: gubarkov
 * Date: 09.03.12
 * Time: 17:14
 */
public class XmlFpDaoImpl extends DaoImplBase implements XmlFpDao {
    @Override
    public String getDescriptorXmlByUrl(String url) {
        final List<String> list = getJdbcTemplate().queryForList(
                "SELECT xml FROM xmlfp WHERE url=?",
                String.class,
                url);

        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() == 1) {
                return list.get(0);
            }

            throw new IncorrectResultSizeDataAccessException(1, list.size());
        }

        return null;
    }

    @Override
    public void addDescriptorInfo(String url, String xml) {

        final HashMap<String, Object> record = new HashMap<String, Object>();
        record.put("url", url);
        record.put("xml", xml);

        newSimpleJdbcInsert()
                .withTableName("xmlfp")
                .execute(record);
    }

    @Override
    public void updateDescriptorInfo(String url, String xml) {
        getJdbcTemplate().
                update("UPDATE xmlfp SET xml=? WHERE url=?",
                        xml, url);
    }
}
