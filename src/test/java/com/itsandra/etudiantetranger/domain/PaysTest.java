package com.itsandra.etudiantetranger.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.itsandra.etudiantetranger.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaysTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pays.class);
        Pays pays1 = new Pays();
        pays1.setId(1L);
        Pays pays2 = new Pays();
        pays2.setId(pays1.getId());
        assertThat(pays1).isEqualTo(pays2);
        pays2.setId(2L);
        assertThat(pays1).isNotEqualTo(pays2);
        pays1.setId(null);
        assertThat(pays1).isNotEqualTo(pays2);
    }
}
