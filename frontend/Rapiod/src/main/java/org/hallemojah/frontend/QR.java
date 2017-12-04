package org.hallemojah.frontend;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.Param;

import java.io.IOException;
import java.util.List;

@Controller
public class QR {

    @GET("/health")
    public String health() {
        return "I'm healthy";
    }

}

