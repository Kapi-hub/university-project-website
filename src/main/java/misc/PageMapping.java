package misc;

import models.AccountType;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to handle the mapping of URLs to resource locations and required account types
 */
public class PageMapping {
    private static final Map<String, PageInfo> pageMap = new HashMap<>();

    static {
        pageMap.put("/", new PageInfo("/home/index.html", null));
        pageMap.put("/login/", new PageInfo("/login/index.html", null));
        pageMap.put("/submit/", new PageInfo("/client/index.html", null));
        pageMap.put("/admin/", new PageInfo("/admin/mainPage/index.html", AccountType.ADMIN));
        pageMap.put("/admin/dashboard/", new PageInfo("/admin/dashboard/index.html", AccountType.ADMIN));
        pageMap.put("/admin/crewAssignments/", new PageInfo("/admin/crewEventsOverview/index.html", AccountType.ADMIN));
        pageMap.put("/admin/crewAssignments/newMember", new PageInfo("/admin/crewEventsOverview/index.html", AccountType.ADMIN));
        pageMap.put("/admin/crewAssignments/newEvent", new PageInfo("/admin/crewEventsOverview/index.html", AccountType.ADMIN));

        pageMap.put("/crew/dashboard/", new PageInfo("/crew/dashboard/index.html", AccountType.CREW_MEMBER));
        pageMap.put("/crew/calendar/", new PageInfo("/crew/calendar/index.html", AccountType.CREW_MEMBER));
    }

    /**
     * Get the resource location of the page mapped to the given url
     *
     * @param url the url of the request
     * @return the actual location of the resource
     */
    public static String getResourceLocation(String url) {
        PageInfo pageInfo = getPageInfo(url);
        return pageInfo == null ? null : pageInfo.resourceLocation();
    }

    /**
     * Get the required account type of the page mapped to the given url
     * This is used by the StaticAuthenticationFilter to check if the user is allowed to access the page
     *
     * @param url the url of the request
     * @return the required account type to access the page
     */
    public static AccountType getRequiredAccountType(String url) {
        PageInfo pageInfo = getPageInfo(url);
        return pageInfo == null ? null : pageInfo.requiredAccountType();
    }

    /**
     * Record class to store the resource location and required account type of a page
     */
    private record PageInfo(String resourceLocation, AccountType requiredAccountType) {
    }

    /**
     * Get the PageInfo object mapped to the given url
     * This is a helper method for the two methods above
     * It checks if the url is in the map, and if not, it checks if the url with a "/" at the end is in the map
     * This is so that the method works to allow, for example, both "/admin" and "/admin/" to be valid URLs
     *
     * @param key the url of the request
     * @return the PageInfo object mapped to the given url
     */
    private static PageInfo getPageInfo(String key) {
        PageInfo pageInfo = pageMap.get(key);
        if (pageInfo == null) {
            pageInfo = pageMap.get(key + "/");
        }
        return pageInfo;
    }
}
