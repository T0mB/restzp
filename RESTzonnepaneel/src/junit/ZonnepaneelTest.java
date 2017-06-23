package junit;

import static org.junit.Assert.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class ZonnepaneelTest {
    @Path("myresource")
    public static class MyResource {
        @POST
        @Consumes(MediaType.TEXT_PLAIN)
        @Produces(MediaType.APPLICATION_XML)
        public MyModel createMyModel(int number) {
            return new MyModel(number);
        }
    }
    public static MyResource sut = new MyResource();
    public static InMemoryRestServer server;
    @BeforeClass
    public static void beforeClass() throws Exception {
        server = InMemoryRestServer.create(sut);
    }
    @AfterClass
    public static void afterClass() throws Exception {
        server.close();
    }
    @Test
    public void postSimpleBody() throws Exception {
        Response response = server.newRequest("/myresource").request().buildPost(Entity.text("42")).invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        MyModel myModel = response.readEntity(MyModel.class);
        assertEquals(42, myModel.getResult());
    }
}