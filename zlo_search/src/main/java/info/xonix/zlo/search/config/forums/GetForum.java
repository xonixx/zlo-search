package info.xonix.zlo.search.config.forums;

import info.xonix.zlo.search.logic.forum_adapters.ForumAdapter;

import java.util.*;

/**
 * User: gubarkov
 * Date: 27.02.12
 * Time: 23:02
 */
public class GetForum {
    private List<ForumDescriptor> forumDescriptors;
    private Map<String, ForumDescriptor> forumIdToForumDescriptor = new LinkedHashMap<String, ForumDescriptor>();
    private Map<Integer, ForumDescriptor> forumIntIdToForumDescriptor = new LinkedHashMap<Integer, ForumDescriptor>();

    private static GetForum INSTANCE;

    public GetForum(List<ForumDescriptor> forumDescriptors) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized!");
        }

        this.forumDescriptors = forumDescriptors;
        
        for (ForumDescriptor forumDescriptor : forumDescriptors) {
            final String forumId = forumDescriptor.getForumId();
            final int forumIntId = forumDescriptor.getForumIntId();

            if (forumIdToForumDescriptor.containsKey(forumId)) {
                throw new IllegalArgumentException("Duplicate forumId: " + forumId);
            }
            if (forumIntIdToForumDescriptor.containsKey(forumIntId)) {
                throw new IllegalArgumentException("Duplicate forumIntId: " + forumIntId);
            }

            forumIdToForumDescriptor.put(forumId, forumDescriptor);
            forumIntIdToForumDescriptor.put(forumIntId, forumDescriptor);
        }

        INSTANCE = this;
    }

    public static ForumAdapter adapter(String forumId) {
        return descriptor(forumId).getForumAdapter();
    }

    public static ForumAdapter adapter(int forumIntId) {
        return descriptor(forumIntId).getForumAdapter();
    }

    public static ForumParams params(String forumId) {
        return descriptor(forumId).getForumParams();
    }

    public static Collection<String> ids() {
        return INSTANCE.forumIdToForumDescriptor.keySet();
    }

    public static List<ForumDescriptor> descriptors() {
        return INSTANCE.forumDescriptors;
    }

    public static ForumDescriptor descriptor(String forumId) {
        final ForumDescriptor forumDescriptor = INSTANCE.forumIdToForumDescriptor.get(forumId);

        if (forumDescriptor == null) {
            throw new IllegalArgumentException("Wrong forumId: " + forumId);
        }

        return forumDescriptor;
    }

    public static ForumDescriptor descriptor(int forumIntId) {
        final ForumDescriptor forumDescriptor = INSTANCE.forumIntIdToForumDescriptor.get(forumIntId);

        if (forumDescriptor == null) {
            throw new IllegalArgumentException("Wrong forumIntId: " + forumIntId);
        }

        return forumDescriptor;
    }
}
