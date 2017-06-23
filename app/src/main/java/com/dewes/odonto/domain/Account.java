package com.dewes.odonto.domain;

import com.dewes.odonto.api.client.AuthResource;

/**
 * Created by Dewes on 22/06/2017.
 */

public class Account {

    public static final String ACCOUNT_TYPE = "com.dewes.odonto.domain.Account";

    public static final String ACCOUNT_NAME = "Odonto";

    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";

    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Odonto account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Odonto account";

    public static final AuthResource serverAuthenticate = AuthResource.getInstance();
}
