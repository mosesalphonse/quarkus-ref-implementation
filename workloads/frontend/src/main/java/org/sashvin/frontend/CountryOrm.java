package org.sashvin.frontend;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryOrm {

    public String name;
    public String id;
    
    
}
