/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.authentication.boundary;

import com.sourcecoding.pb.business.authentication.controller.UserFactory;
import com.sourcecoding.pb.business.authentication.entity.User;
import java.io.IOException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 *
 * @author Matthias
 */
@Path("auth/linkedin")
@Stateless
public class AuthLinkedInService {

    private static final String API_KEY = "wbnjnfub9nob";
    private static final String SECRET_KEY = "ktyPRBlfx4tvYqPp";
    @Inject
    private UserFactory userFactory;

    @GET
    @Path("dialog")
    public void loginDialog(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        System.out.println("dialog/session-id:" + request.getSession().getId());
        System.out.println("dialog-userFactory:" + userFactory);
        System.out.println("uri: " + request.getRequestURL());
        String callBackURL = request.getRequestURL().toString();
        callBackURL = callBackURL.substring(0, callBackURL.lastIndexOf("/"));
        callBackURL += "/login";

        System.out.println("callback-URL: " + callBackURL);


        OAuthService service = new ServiceBuilder()
                .provider(LinkedInApi.class)
                .callback(callBackURL)
                .apiKey(API_KEY)
                .apiSecret(SECRET_KEY)
                .build();
        HttpSession httpsession = request.getSession();
        httpsession.setAttribute("oauth.service", service);

        Token requestToken = service.getRequestToken();
        System.out.println("requestToken: " + requestToken);
        httpsession.setAttribute("oauth.request_token", requestToken);

        String confirmAccessURL = service.getAuthorizationUrl(requestToken);

        System.out.println(confirmAccessURL);
        try {
            response.sendRedirect(response.encodeRedirectURL(confirmAccessURL));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @GET
    @Path("login")
    @Produces(MediaType.TEXT_HTML)
    public void callback(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, JSONException {
        System.out.println("request: " + request);
      
        System.out.println("login/session-id:" + request.getSession(true).getId());
        System.out.println("login-userFactory:" + userFactory);
        String oauth_token = request.getParameter("oauth_token");
        String oauth_verifier = request.getParameter("oauth_verifier");
        String username = request.getParameter("username");

        if (oauth_verifier.length() > 0) {
            Verifier verifier = new Verifier(oauth_verifier);

            HttpSession httpsession = request.getSession(true);
            OAuthService service = (OAuthService) httpsession.getAttribute("oauth.service");
            Token requestToken = (Token) httpsession.getAttribute("oauth.request_token");
            httpsession.setAttribute("username", username);

            System.out.println("requestToken: " + requestToken);

            Token accessToken = service.getAccessToken(requestToken, verifier);

            httpsession.setAttribute("oauth.access_token", accessToken);

            System.out.println("rawresponse: " + accessToken.getRawResponse());

            OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,picture-url)");
            oAuthRequest.addHeader("x-li-format", "json");
            service.signRequest(accessToken, oAuthRequest);
            org.scribe.model.Response scribeResponse = oAuthRequest.send(); //Do something with response.getBody()
            System.out.println(scribeResponse.getBody());

            JSONObject userLIData = new JSONObject(scribeResponse.getBody());

            User u = new User();
            u.setFirstName(userLIData.getString("firstName"));
            u.setLastName(userLIData.getString("lastName"));
            u.setTitle(userLIData.getString("headline"));
            if (userLIData.has("pictureUrl"))
                u.setPictureUrl(userLIData.getString("pictureUrl"));
            u.setSocialNetId(userLIData.getString("id"));

            userFactory.loginUser(u);

            try {
                System.out.println("contextPath: "+ request.getContextPath());
                response.sendRedirect(request.getContextPath());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        }
        //return Response.ok().build();
    }
}