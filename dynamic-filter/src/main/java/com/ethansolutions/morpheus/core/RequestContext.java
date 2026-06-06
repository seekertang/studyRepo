package com.ethansolutions.morpheus.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestContext {
    private String externalSource;
    private String externalId;
}
