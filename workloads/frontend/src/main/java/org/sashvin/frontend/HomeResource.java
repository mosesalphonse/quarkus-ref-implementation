package org.sashvin.frontend;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;


import java.util.List;
import java.util.ArrayList;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.rest.client.inject.RestClient;


@Path("/home")
public class HomeResource {

    @Inject
    Template home;

    @Inject
    @RestClient
    SashCountriesService countriesService;

    @Inject
    @RestClient
    SashCountriesOrmService sashCountriesOrmService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public TemplateInstance get(@QueryParam("user") String name) {
        System.out.println("country name :"+name);
        System.out.println("here 11111 .....");
        FrontendResponse frontendResponse=new FrontendResponse();
        List<Country> extCountryList=new ArrayList();
        List<CountryOrm> intCountryList=new ArrayList();
        System.out.println("here.....");
        Set<Country> country=countriesService.getByName(name);
        Country c=null;
        for(Country s : country)
        {
            System.out.println("name :"+s.name);
            System.out.println("alpha2Code :"+s.alpha2Code);
            System.out.println("capital :"+s.capital);
            c=s;
            extCountryList.add(s);
        }
        System.out.println("country external service response :"+country.toString());

        Set<CountryOrm> countriesOrmList=sashCountriesOrmService.getByName();
        System.out.println("countriesOrmList from ORM internal service :"+countriesOrmList);

        for(CountryOrm o : countriesOrmList)
        {
            System.out.println("orm name :"+o.name);
                    
            intCountryList.add(o);
        }

        System.out.println("extCountryList :"+extCountryList);
        System.out.println("intCountryList :"+intCountryList);
        frontendResponse.setCountryName(name);
        frontendResponse.setExtCountriesList(extCountryList);
        frontendResponse.setIntCountriesList(intCountryList);

        if(name!=null)
        {
            
            return home.data("name",frontendResponse);
        }
        else
            return home.data("name","Sashvin");
    }
}