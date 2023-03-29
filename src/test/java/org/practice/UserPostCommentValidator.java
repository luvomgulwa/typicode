package org.practice;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserPostCommentValidator {

    private static final String BASE_URI = "https://jsonplaceholder.typicode.com";

    public static void validateUserPostComments(String username) {
        // Fetch the user with the given username
        User user = getUserByUsername(username);

        // Fetch all posts written by the user
        List<Post> posts = getPostsByUserId(user.getId());

        // For each post, fetch the comments and validate the emails
        for (Post post : posts) {
            List<Comment> comments = getCommentsByPostId(post.getId());

            validateEmailsInComments(comments);
        }
    }

    private static User getUserByUsername(String username) {
        Response response = RestAssured.given().baseUri(BASE_URI).when().get("/users?username=" + username);
        List<User> users = response.jsonPath().getList("", User.class);

        if (users.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }

        return users.get(0);
    }

    private static List<Post> getPostsByUserId(int userId) {
        Response response = RestAssured.given().baseUri(BASE_URI).when().get("/posts?userId=" + userId);
        return response.jsonPath().getList("", Post.class);
    }

    private static List<Comment> getCommentsByPostId(int postId) {
        Response response = RestAssured.given().baseUri(BASE_URI).when().get("/comments?postId=" + postId);
        return response.jsonPath().getList("", Comment.class);
    }

    private static void validateEmailsInComments(List<Comment> comments) {
        Pattern emailPattern = Pattern.compile("\\b[\\w.%-]+@[\\w.-]+\\.[a-zA-Z]{2,}\\b");

        List<String> invalidEmails = comments.stream()
                .map(Comment::getEmail)
                .filter(email -> !emailPattern.matcher(email).matches())
                .collect(Collectors.toList());

        if (!invalidEmails.isEmpty()) {
            throw new RuntimeException("Invalid emails found in comments: " + invalidEmails);
        }
    }
}

