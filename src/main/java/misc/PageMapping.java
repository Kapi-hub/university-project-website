package misc;

import models.AccountType;

import java.util.HashMap;
import java.util.Map;

public class PageMapping {
    private static final Map<String, PageInfo> pageMap = new HashMap<>();

    // place all the page mappings here, fellow teammates :)
    static {
        pageMap.put("/", new PageInfo("/home/index.html", null));
        pageMap.put("/login", new PageInfo("/login/index.html", null));
        pageMap.put("/submit", new PageInfo("/client/index.html", null));
        pageMap.put("/admin", new PageInfo("/admin/mainPage/index.html", AccountType.ADMIN));
        pageMap.put("/admin/dashboard", new PageInfo("/admin/dashboard/index.html", AccountType.ADMIN));
        pageMap.put("/admin/crewAssignments", new PageInfo("/admin/crewEventsOverview/index.html", AccountType.ADMIN));
        pageMap.put("/crew/dashboard", new PageInfo("/crew/dashboard/index.html", AccountType.CREW_MEMBER));
        pageMap.put("/crew/calendar", new PageInfo("/crew/calendar/index.html", AccountType.CREW_MEMBER));
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
