package model;

import util.PortalException;

public interface QueryActions {
    void addQuery(Query query) throws PortalException;
    void replyToQuery(int queryId, String answer, boolean answered) throws PortalException;
    void markResolved(int queryId) throws PortalException;
}
