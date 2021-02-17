package org.sash.hibernate.orm;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("countries")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class CountryResource {

    private static final Logger LOGGER = Logger.getLogger(CountryResource.class.getName());

    @Inject
    EntityManager entityManager;

    @GET
    public List<Country> get() {
        return entityManager.createNamedQuery("Countries.findAll", Country.class)
                .getResultList();
    }

    @GET
    @Path("{id}")
    public Country getSingle(@PathParam Integer id) {
        Country entity = entityManager.find(Country.class, id);
        if (entity == null) {
            throw new WebApplicationException("Country with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Country country) {
        if (country.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        entityManager.persist(country);
        return Response.ok(country).status(201).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Country update(@PathParam Integer id, Country country) {
        if (country.getName() == null) {
            throw new WebApplicationException("Country Name was not set on request.", 422);
        }

        Country entity = entityManager.find(Country.class, id);

        if (entity == null) {
            throw new WebApplicationException("Country with id of " + id + " does not exist.", 404);
        }

        entity.setName(country.getName());

        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam Integer id) {
        Country entity = entityManager.getReference(Country.class, id);
        if (entity == null) {
            throw new WebApplicationException("Country with id of " + id + " does not exist.", 404);
        }
        entityManager.remove(entity);
        return Response.status(204).build();
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception exception) {
            LOGGER.error("Failed to handle request", exception);

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            JsonObjectBuilder entityBuilder = Json.createObjectBuilder()
                    .add("exceptionType", exception.getClass().getName())
                    .add("code", code);

            if (exception.getMessage() != null) {
                entityBuilder.add("error", exception.getMessage());
            }

            return Response.status(code)
                    .entity(entityBuilder.build())
                    .build();
        }

    }
}
