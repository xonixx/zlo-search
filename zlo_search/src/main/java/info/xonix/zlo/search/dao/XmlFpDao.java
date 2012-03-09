package info.xonix.zlo.search.dao;

import javax.annotation.Nullable;

/**
 * User: gubarkov
 * Date: 09.03.12
 * Time: 17:14
 */
public interface XmlFpDao {
    @Nullable
    public String getDescriptorXmlByUrl(String url);

    public void addDescriptorInfo(String url, String xml);

    public void updateDescriptorInfo(String url, String xml);
}
