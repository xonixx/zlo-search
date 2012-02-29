package info.xonix.zlo.search.config.forums;

import info.xonix.zlo.search.logic.forum_adapters.ForumAdapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: gubarkov
 * Date: 27.02.12
 * Time: 23:02
 */
public class GetForum {
    private List<ForumDescriptor> forumDescriptors;
    private Map<String, ForumDescriptor> forumIdToForumDescriptor = new LinkedHashMap<String, ForumDescriptor>();

    private static GetForum INSTANCE;

    public GetForum(List<ForumDescriptor> forumDescriptors) {
        if (INSTANCE != null){
            throw new IllegalStateException("Already initialized!");
        }

        this.forumDescriptors = forumDescriptors;

        for (ForumDescriptor forumDescriptor : forumDescriptors) {
            forumIdToForumDescriptor.put(forumDescriptor.getForumId(), forumDescriptor);
        }

        INSTANCE = this;
    }

    public static ForumAdapter adapter(String forumId) {
        return descriptor(forumId).getForumAdapter();
    }

    public static ForumParams params(String forumId) {
        return descriptor(forumId).getForumParams();
    }

    public static ForumDescriptor descriptor(String forumId) {
        final ForumDescriptor forumDescriptor = INSTANCE.forumIdToForumDescriptor.get(forumId);

        if (forumDescriptor == null) {
            throw new IllegalArgumentException("Wrong forumId: " + forumId);
        }

        return forumDescriptor;
    }
}
