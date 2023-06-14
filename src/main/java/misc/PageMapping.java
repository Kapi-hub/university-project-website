package misc;

import models.AccountType;

import java.util.HashMap;
import java.util.Map;

public class PageMapping {
    private static final Map<String, PageInfo> pageMap = new HashMap<>() {
        @Override
        public PageInfo get(Object key) { // allow for both "/admin" and "/admin/" to be valid
            PageInfo pageInfo = super.get(key);
            if (pageInfo == null) {
                return super.get(key + "/");
            }
            return pageInfo;
        }
    };

    // place all the page mappings here, fellow teammates ^^
    // key: url, value: resource location and required account type (null if accessible to all)
    // (note that the resource location is relative to the /WEB-INF/pages/ folder)
    // (also note that the url must be entered with a "/" at the end for the mapping to work,
    // i.e. "/admin" is invalid, instead use "/admin/". This is so that my beautiful get() method works to allow for
    // both "/admin" and "/admin/" to be valid URLs to access the admin page in case the user doesn't know which to use)
    static {
        pageMap.put("/", new PageInfo("/home/index.html", null));
        pageMap.put("/login/", new PageInfo("/login/index.html", null));
        pageMap.put("/submit/", new PageInfo("/client/index.html", null));
        pageMap.put("/admin/", new PageInfo("/admin/mainPage/index.html", AccountType.ADMIN));
        pageMap.put("/admin/dashboard/", new PageInfo("/admin/dashboard/index.html", AccountType.ADMIN));
        pageMap.put("/admin/crewAssignments/", new PageInfo("/admin/crewEventsOverview/index.html", AccountType.ADMIN));
        pageMap.put("/crew/dashboard/", new PageInfo("/crew/dashboard/index.html", AccountType.CREW_MEMBER));
        pageMap.put("/crew/calendar/", new PageInfo("/crew/calendar/index.html", AccountType.CREW_MEMBER));
    }

    public static String getResourceLocation(String url) {
        PageInfo pageInfo = pageMap.get(url);
        return pageInfo == null ? null : pageInfo.resourceLocation();
    }

    public static AccountType getRequiredAccountType(String url) {
        PageInfo pageInfo = pageMap.get(url);
        return pageInfo == null ? null : pageInfo.requiredAccountType();
    }

    private record PageInfo(String resourceLocation, AccountType requiredAccountType) {
    }
}
