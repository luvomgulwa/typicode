package org.practice;

import org.testng.annotations.Test;

public class UserPostCommentValidatorTest {
    @Test
    public void testValidateUserPostComments() {
        UserPostCommentValidator.validateUserPostComments("Antonette");
    }
}
