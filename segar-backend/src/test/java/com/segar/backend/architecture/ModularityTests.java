package com.segar.backend.architecture;

import com.segar.backend.BackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(BackendApplication.class);

    @Test
    void verify_modularity() {
        modules.verify();
    }

    @Test
    void generate_documentation() {
        new Documenter(modules)
                .writeDocumentation()
                .writeModuleCanvases();
    }
}
