package com.socialnetwork.socialnetwork.exceptions;

public enum ErrorCode {
    //User
    ERROR_REGISTERING_USER,
    ERROR_FINDING_USER,
    ERROR_LOGGING_IN,
    ERROR_CREATING_FRIEND_REQUEST,
    ERROR_FINDING_FRIEND_REQUEST,
    ERROR_USERS_NOT_FRIENDS,
    ERROR_FINDING_USER_BY_JWT,
    //Post
    ERROR_FINDING_POST,
    ERROR_CREATING_POST,
    ERROR_UPDATING_POST,
    ERROR_DELETING_POST,
    //Group
    ERROR_GETTING_GROUP_REQUESTS,
    ERROR_CREATING_GROUP,
    ERROR_GETTING_GROUP_POSTS,
    ERROR_DELETING_GROUP,
    ERROR_CREATING_REQUEST_GROUP,
    ERROR_LEAVING_GROUP,
    ERROR_REMOVING_MEMBER,
    ERROR_MANAGING_GROUP_REQUEST
}
