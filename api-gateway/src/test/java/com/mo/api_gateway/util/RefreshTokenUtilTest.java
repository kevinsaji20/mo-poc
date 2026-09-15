package com.mo.api_gateway.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RefreshTokenUtilTest {

    @Test
    void generateRefreshToken_shouldReturnUrlSafeString_andBeUnique() {
        RefreshTokenUtil util = new RefreshTokenUtil();

        String t1 = util.generateRefreshToken();
        String t2 = util.generateRefreshToken();

        assertThat(t1).isNotNull().isNotEmpty();
        assertThat(t2).isNotNull().isNotEmpty();
        assertThat(t1).isNotEqualTo(t2);

        // url-safe base64 without padding should not contain +, / or =
        assertThat(t1).doesNotContain("+", "/", "=");
    }
}
