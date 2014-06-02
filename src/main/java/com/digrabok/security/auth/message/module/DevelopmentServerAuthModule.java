package com.digrabok.security.auth.message.module;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * This ServerAuthModule (JSR 196) implementation allow configure remoteUser (javax.servlet.http.HttpServletRequest#getRemoteUser) and list of user security groups in provider properties.<br />
 * <br />
 * In module properties you must set: <br />
 * <strong>login</strong> - property specify <em>remoteUser</em> (javax.servlet.http.HttpServletRequest#getRemoteUser) for each request.<br />
 * <strong>groupNames</strong> - property specify list of security groups for each request.
 * <strong>delimiter (optional)</strong> - property specify separator for <strong>groupNames</strong> (if not present, then comma - ",")
 */
public class DevelopmentServerAuthModule implements ServerAuthModule {
    private static final String LOGIN_OPTION_NAME = "login";
    private static final String GROUP_NAMES_OPTION_NAME = "groupNames";
    private static final String GROUP_NAMES_DELIMITER_OPTION_NAME = "delimiter";

    private static final Class[] SUPPORTED_MESSAGE_TYPES = { HttpServletRequest.class };

    private String[] parseGroups(String groupsString) {
        groupsString = groupsString.trim().replaceAll("\\s*" + this.delimiterString + "\\s*", this.delimiterString);
        return groupsString.split(this.delimiterString);
    }

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map options) throws AuthException {
        this.callbackHandler = handler;
        this.login = (String) options.get(LOGIN_OPTION_NAME);
        if (options.containsKey(GROUP_NAMES_DELIMITER_OPTION_NAME)) {
            this.delimiterString = (String) options.get(GROUP_NAMES_DELIMITER_OPTION_NAME);
        }
        this.groupNames = parseGroups((String) options.get(GROUP_NAMES_OPTION_NAME));
    }

    @Override
    public Class[] getSupportedMessageTypes() {
        return SUPPORTED_MESSAGE_TYPES;
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        if (this.login == null) {
            try {
                HttpServletResponse response = (HttpServletResponse)messageInfo.getResponseMessage();
                response.sendError(401, "Login option is empty. Please add login in security provider config.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AuthStatus.FAILURE;
        }

        try {
            this.callbackHandler.handle(new Callback[]{
                    new CallerPrincipalCallback(clientSubject, this.login),
                    new GroupPrincipalCallback(clientSubject, this.groupNames)
            });
        } catch (IOException e) {
            AuthException authEx = new AuthException();
            authEx.initCause(e);
            throw authEx;
        } catch (UnsupportedCallbackException e) {
            AuthException authEx = new AuthException();
            authEx.initCause(e);
            throw authEx;
        }

        return AuthStatus.SUCCESS;
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject subject) throws AuthException {
        return AuthStatus.SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {

    }

    private CallbackHandler callbackHandler;
    private String login;
    private String[] groupNames;
    private String delimiterString = ",";
}
