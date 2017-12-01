package site.shawnxxy.eventreporter.utils;

import java.util.ArrayList;
import java.util.List;

import site.shawnxxy.eventreporter.constructor.Post;

/**
 * Created by ShawnX on 8/27/17.
 */

public class DataService {
    /**
     * Fake all the event data for now. We will refine this and connect
     * to our backend later.
     */
    public static List<Post> getEventData() {
        List<Post> postData = new ArrayList<Post>();
        for (int i = 0; i < 10; ++i) {
            postData.add(
                    new Post("Post", "1184 W valley Blvd, CA 90101", "This is a huge event"));
        }
        return postData;
    }

}
