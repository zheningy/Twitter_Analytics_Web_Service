package org.hallemojah.frontend;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.Param;

import static org.hallemojah.frontend.LockUtils.*;

@Controller
public class MockSQL {
    @GET("/read")
    public String get(@Param("uuid") String uuid, @Param("seq") Integer sequence_number ) throws InterruptedException {
        final LockUtils.lockStatusCV bundle = addToOp(uuid, sequence_number, true);
        acquire_lock_read(bundle, sequence_number);
        Thread.sleep(1000);
        release_lock(bundle, sequence_number);
        return "read "+uuid+", "+sequence_number+" success!";
    }
    @GET("/write")
    public String write(@Param("uuid") String uuid, @Param("seq") Integer sequence_number ) throws InterruptedException {
        final LockUtils.lockStatusCV bundle = addToOp(uuid, sequence_number, false);
        acquire_lock_write(bundle, sequence_number);
        Thread.sleep(3000);
        release_lock(bundle, sequence_number);
        return "write "+uuid+", "+sequence_number+" success!";
    }
}
