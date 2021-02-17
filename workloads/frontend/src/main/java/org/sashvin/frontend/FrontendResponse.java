package org.sashvin.frontend;

import java.util.List;


public class FrontendResponse {

    private String countryName;
   	private List<Country> extCountriesList;
    private List<CountryOrm> intCountriesList;

        public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
    }
    
    	public List<Country> getExtCountriesList() {
		return extCountriesList;
	}

	public void setExtCountriesList(List<Country> extCountriesList) {
		this.extCountriesList = extCountriesList;
	}

	public List<CountryOrm> getIntCountriesList() {
		return intCountriesList;
	}

	public void setIntCountriesList(List<CountryOrm> intCountriesList) {
		this.intCountriesList = intCountriesList;
	}


    
}
