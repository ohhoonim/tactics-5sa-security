package dev.ohhoonim.grammar;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

public class StringTest {

    @Test
    void only_whitespacing_test() {
        String onlyWhitespacing = "   ";

        assertThat(onlyWhitespacing.isBlank()).isTrue();
        assertThat(onlyWhitespacing.isEmpty()).isFalse();
    }

    @Test
    void empty_test() {
        String emptyString = "";

        assertThat(emptyString.isBlank()).isTrue();
        assertThat(emptyString.isEmpty()).isTrue();
    }

    @Test
    void string_utils_test() {
        String nullString = null;
        assertThat(StringUtils.hasText(nullString)).isFalse();


        String onlyWhitespacing = "   ";
        assertThat(StringUtils.hasText(onlyWhitespacing)).isFalse();

        String emptyString = "";
        assertThat(StringUtils.hasText(emptyString)).isFalse();
    }
}
